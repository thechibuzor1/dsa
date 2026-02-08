package skiplist;

import java.util.Random;

/**
 * SkipList implementation that provides average O(log n) search, insertion, and deletion.
 * A skip list is a probabilistic data structure that allows fast search within an ordered
 * sequence of elements using multiple layers of linked lists.
 */
public class Skiplist {

    // Reference to the top-left most node (head of the highest level)
    private Node head;
    
    // Reference to the top-right most node (tail of the highest level)
    private Node tail;
    
    // Sentinel values representing negative and positive infinity
    // These act as boundary nodes that are always present
    private final int NEG_INFINITY = Integer.MIN_VALUE;
    private final int POS_INFINITY = Integer.MAX_VALUE;
    
    // Current height (number of levels) in the skip list
    private int heightOfSkipList = 0;
    
    // Random number generator for probabilistic level determination during insertion
    public Random random = new Random();

    /**
     * Constructor initializes an empty skip list with head and tail sentinel nodes
     * at the base level (level 0).
     */
    public Skiplist() {
        // Create head (-∞) and tail (+∞) sentinel nodes
        head = new Node(NEG_INFINITY);
        tail = new Node(POS_INFINITY);
        
        // Link head to tail at the base level
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Searches for a node with the given key in the skip list.
     * Uses the skip list property to traverse from top-left and move down levels.
     * 
     * @param key The value to search for
     * @return The node with the given key if found, or the node with the largest key
     *         less than the search key (closest predecessor)
     */
    public Node searchSkipList(int key) {
        Node n = head; // Start from the top-left most node
        
        // Traverse down through the levels
        while (n.below != null) {
            n = n.below; // Move down one level
            
            // Move right at the current level while the next node's key is ≤ search key
            while (key >= n.next.key) {
                n = n.next;
            }
        }
        
        // At this point, n is at the bottom level
        // If key exists, n.key == key, otherwise n is the largest node < key
        return n;
    }

    /**
     * Inserts a new node with the given key into the skip list.
     * The node may be promoted to higher levels based on random probability (coin flips).
     * 
     * @param key The value to insert
     * @return The newly inserted node, or the existing node if key already exists
     */
    public Node insertNode(int key) {
        // Find the insertion position at the bottom level
        Node position = searchSkipList(key);
        Node q; // Will hold reference to newly inserted node at current level
        
        // Track the current level we're inserting at (starting from bottom level 0)
        int level = -1;
        int numberOfHeads = -1;
        
        // If key already exists, return the existing node (skip list typically allows duplicates)
        if (position.key == key) {
            return position;
        }
        
        // Insert at bottom level and potentially promote to higher levels
        do {
            numberOfHeads++;
            level++;
            
            // Ensure we have enough levels to insert at the current level
            canIncreaseLevel(level);
            
            q = position; // Remember the insertion position at current level
            
            // Move up to find where to insert at the next higher level
            while (position.above == null) {
                position = position.prev; // Move left to find a node with an above reference
            }
            position = position.above; // Move up one level
            
            // Insert the node at the current level
            q = insertAfterAbove(position, q, key);
            
        // Continue promoting the node to higher levels with 50% probability
        } while (random.nextBoolean() == true);
        
        return q; // Return the top-most inserted node
    }

    /**
     * Deletes a node with the given key from all levels of the skip list.
     * 
     * @param key The value to delete
     * @return The deleted node, or null if key was not found
     */
    public Node deleteNode(int key) {
        // Find the node to delete (at bottom level)
        Node nodeToDelete = searchSkipList(key);
        
        // If key doesn't exist, return null
        if (nodeToDelete.key != key) {
            return null;
        }
        
        // Remove references to the node from all levels
        removeReferencesToNode(nodeToDelete);
        
        // Traverse up through all levels and remove the node from each level
        while (nodeToDelete != null) {
            removeReferencesToNode(nodeToDelete);
            
            if (nodeToDelete.above != null) {
                nodeToDelete = nodeToDelete.above; // Move up to next level
            } else {
                break; // No more levels above
            }
        }
        
        return nodeToDelete;
    }

    /**
     * Helper method to remove a node from a doubly linked list at a specific level.
     * Updates the next/prev references of neighboring nodes to bypass the node to delete.
     * 
     * @param nodeToDelete The node to remove from the linked list
     */
    private void removeReferencesToNode(Node nodeToDelete) {
        Node afterNodeToDelete = nodeToDelete.next;
        Node beforeNodeToDelete = nodeToDelete.prev;
        
        // Bypass the node to delete by updating neighbor references
        beforeNodeToDelete.next = afterNodeToDelete;
        afterNodeToDelete.prev = beforeNodeToDelete;
    }

    /**
     * Ensures the skip list has enough levels to insert at the specified level.
     * If the requested level exceeds current height, adds new levels.
     * 
     * @param level The level we need to insert at
     */
    private void canIncreaseLevel(int level) {
        if (level >= heightOfSkipList) {
            heightOfSkipList++; // Increase the height counter
            addAnotherLevel(); // Add a new top level
        }
    }

    /**
     * Adds a new empty level to the top of the skip list.
     * Creates new head and tail sentinel nodes and links them to the previous top level.
     */
    private void addAnotherLevel() {
        // Create new sentinel nodes for the new top level
        Node newHead = new Node(NEG_INFINITY);
        Node newTail = new Node(POS_INFINITY);
        
        // Link new head and tail horizontally
        newHead.next = newTail;
        newTail.prev = newHead;
        
        // Link new level vertically to the previous top level
        newHead.below = head;
        newTail.below = tail;
        head.above = newHead;
        tail.above = newTail; 
        
        // Update head and tail references to point to the new top level
        head = newHead;
        tail = newTail;
    }

    /**
     * Inserts a new node after node q and above node position.below.below.
     * Handles the complex linking required for skip list insertion.
     * 
     * @param position Node at the higher level that helps determine where to insert
     * @param q Node after which the new node should be inserted at current level
     * @param key The value for the new node
     * @return The newly inserted node
     */
    private Node insertAfterAbove(Node position, Node q, int key) {
        Node newNode = new Node(key);
        Node nodeBeforeNewNode = position.below.below; // Two levels below position
        
        // Set horizontal links (prev/next) for the new node
        setBeforeAndAfterReferences(q, newNode);
        
        // Set vertical links (above/below) for the new node
        setAboveAndBelowReferences(position, key, newNode, nodeBeforeNewNode);
        
        return newNode;
    }

    /**
     * Sets the horizontal (prev/next) references for a newly inserted node.
     * Integrates the node into the doubly linked list at its level.
     * 
     * @param q The node after which the new node should be inserted
     * @param newNode The node being inserted
     */
    private void setBeforeAndAfterReferences(Node q, Node newNode) {
        // Link newNode between q and q.next
        newNode.next = q.next;
        newNode.prev = q;
        
        // Update neighbors to point to newNode
        q.next.prev = newNode;
        q.next = newNode;
    }

    /**
     * Sets the vertical (above/below) references for a newly inserted node.
     * Establishes the tower structure by linking nodes across different levels.
     * 
     * @param position Reference node at higher level
     * @param key The key being inserted
     * @param newNode The node being inserted at current level
     * @param nodeBeforeNewNode Node two levels below position, helps find the node below
     */
    private void setAboveAndBelowReferences(Node position, int key, 
                                           Node newNode, Node nodeBeforeNewNode) {
        // If there's a node below this level, establish vertical connection
        if (nodeBeforeNewNode != null) {
            // Find the node at the lower level with the same key
            while (true) {
                if (nodeBeforeNewNode.next.key != key) {
                    nodeBeforeNewNode = nodeBeforeNewNode.next;
                } else {
                    break;
                }
            }
            
            // Link newNode vertically with the node below
            newNode.below = nodeBeforeNewNode.next;
            nodeBeforeNewNode.next.above = newNode;
        }
        
        // If there's a node above this level, establish vertical connection
        if (position != null) {
            if (position.next.key == key) {
                newNode.above = position.next;
            }
        }
    }

    /**
     * Prints a visual representation of the skip list from top to bottom.
     * Each level is shown with nodes connected by arrows.
     * Sentinel nodes are displayed as -∞ (head) and +∞ (tail).
     */
    public void printSkipList() {
        StringBuilder sb = new StringBuilder();
        
        // Start from the top level
        Node currentLevel = head;
        int level = heightOfSkipList;
        
        // Traverse each level from top to bottom
        while (currentLevel != null) {
            sb.append("Level ").append(level).append(": ");
            
            // Traverse all nodes at the current level
            Node currentNode = currentLevel;
            while (currentNode != null) {
                // Display sentinel nodes with special symbols
                switch (currentNode.key) {
                    case NEG_INFINITY -> sb.append("-∞");
                    case POS_INFINITY -> sb.append("+∞");
                    default -> sb.append(currentNode.key);
                }
                
                // Add arrow if not at the last node
                if (currentNode.next != null) {
                    sb.append(" → ");
                }
                
                currentNode = currentNode.next;
            }
            
            sb.append("\n");
            
            // Move down to next level
            currentLevel = currentLevel.below;
            level--;
        }
        
        System.out.println(sb.toString());
    }
}