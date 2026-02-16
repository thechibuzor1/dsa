package skiplist;

import java.util.Random;
import java.util.Scanner;


/**
 * Interactive application for testing and visualizing the SkipList data
 * structure. Provides a menu-driven interface for inserting, deleting,
 * searching, and displaying nodes.
 */
 
/**
 * Represents a single node in a skip list data structure.
 * Each node contains a key/value and maintains references to adjacent nodes
 * in four directions: above, below, next (right), and prev (left).
 * 
 * Skip list nodes form a multi-level, doubly-linked structure where:
 * - Horizontal links (next/prev) connect nodes at the same level
 * - Vertical links (above/below) connect nodes at different levels
 */
class Node {
    
    // Vertical references: connect nodes across different levels (towers)
    public Node above;  // Reference to the node directly above this one (higher level)
    public Node below;  // Reference to the node directly below this one (lower level)
    
    // Horizontal references: connect nodes at the same level
    public Node next;   // Reference to the next node to the right (larger key)
    public Node prev;   // Reference to the previous node to the left (smaller key)
    
    // The data stored in this node (using integer as key for simplicity)
    // In a more general implementation, this could be a key-value pair
    public int key;

    /**
     * Constructs a new node with the specified key.
     * Initializes all references to null, creating an isolated node.
     * The node must be properly linked into the skip list structure
     * through subsequent operations.
     * 
     * @param key The integer value to store in this node.
     *            For sentinel nodes, use Integer.MIN_VALUE (head/-∞) 
     *            or Integer.MAX_VALUE (tail/+∞).
     */
    public Node(int key) {
        this.key = key;  // Store the key value
        
        // Initialize all references to null
        this.above = null;  // No node above initially
        this.below = null;  // No node below initially
        this.next = null;   // No node to the right initially
        this.prev = null;   // No node to the left initially
    }
}
 
/**
 * SkipList implementation that provides average O(log n) search, insertion, and deletion.
 * A skip list is a probabilistic data structure that allows fast search within an ordered
 * sequence of elements using multiple layers of linked lists.
 */
class Skiplist {

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
public class Main {

    /**
     * Main entry point for the SkipList interactive application. Creates a skip
     * list instance and presents a user menu for operations.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Skiplist skiplist = new Skiplist();

        System.out.println("=== Skip List Interactive Application ===");
        System.out.println("A skip list is a probabilistic data structure that provides");
        System.out.println("O(log n) average time complexity for search, insert, and delete operations.");
        System.out.println();

        boolean running = true;
        while (running) {
            printMenu();

            System.out.print("\nEnter your choice (1-6): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    // Insert node operation
                    insertNodeOperation(scanner, skiplist);
                    break;

                case "2":
                    // Delete node operation
                    deleteNodeOperation(scanner, skiplist);
                    break;

                case "3":
                    // Search node operation
                    searchNodeOperation(scanner, skiplist);
                    break;

                case "4":
                    // Print the current state of the skip list
                    skiplist.printSkipList();
                    break;

                case "5":
                    // Generate random nodes for testing
                    generateRandomNodes(scanner, skiplist);
                    break;

                case "6":
                    // Exit the application
                    running = false;
                    System.out.println("Exiting application. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                    break;
            }
        }

        scanner.close();
    }

    /**
     * Displays the main menu with available operations.
     */
    private static void printMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Insert a node");
        System.out.println("2. Delete a node");
        System.out.println("3. Search for a node");
        System.out.println("4. Print skip list");
        System.out.println("5. Generate random nodes");
        System.out.println("6. Exit");
    }

    /**
     * Handles the node insertion operation with user input. Allows multiple
     * insertions until user chooses to go back.
     */
    private static void insertNodeOperation(Scanner scanner, Skiplist skiplist) {
        System.out.println("\n=== INSERT NODE ===");
        System.out.println("Enter integer values to insert into the skip list.");
        System.out.println("Each insertion has a 50% chance of promoting the node to higher levels.");

        while (true) {
            System.out.print("Enter integer value to insert (or 'b' to go back): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("b")) {
                break; // Return to main menu
            }

            try {
                int value = Integer.parseInt(input);
                Node inserted = skiplist.insertNode(value);

                if (inserted != null) {
                    System.out.println("✓ Successfully inserted node with value: " + value);

                    // Offer to show the updated list
                    System.out.print("Show updated skip list? (y/n): ");
                    String show = scanner.nextLine().trim();
                    if (show.equalsIgnoreCase("y")) {
                        skiplist.printSkipList();
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

    /**
     * Handles the node deletion operation with user input. Searches for and
     * removes nodes from all levels of the skip list.
     */
    private static void deleteNodeOperation(Scanner scanner, Skiplist skiplist) {
        System.out.println("\n=== DELETE NODE ===");
        System.out.println("Remove nodes from the skip list by their value.");
        System.out.println("The node will be deleted from all levels where it appears.");

        while (true) {
            System.out.print("Enter integer value to delete (or 'b' to go back): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("b")) {
                break;
            }

            try {
                int value = Integer.parseInt(input);
                Node deleted = skiplist.deleteNode(value);

                if (deleted != null) {
                    System.out.println("✓ Successfully deleted node with value: " + value);

                    // Offer to show the updated list
                    System.out.print("Show updated skip list? (y/n): ");
                    String show = scanner.nextLine().trim();
                    if (show.equalsIgnoreCase("y")) {
                        skiplist.printSkipList();
                    }
                } else {
                    System.out.println("✗ Node with value " + value + " not found in the skip list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

    /**
     * Handles the node search operation with user input. Demonstrates the
     * search algorithm and shows the closest node if not found.
     */
    private static void searchNodeOperation(Scanner scanner, Skiplist skiplist) {
        System.out.println("\n=== SEARCH NODE ===");
        System.out.println("Search for nodes in the skip list.");
        System.out.println("The search algorithm starts from the top-left and moves");
        System.out.println("right and down to find the target value.");

        while (true) {
            System.out.print("Enter integer value to search (or 'b' to go back): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("b")) {
                break;
            }

            try {
                int value = Integer.parseInt(input);
                Node found = skiplist.searchSkipList(value);

                if (found != null && found.key == value) {
                    System.out.println("✓ Node with value " + value + " FOUND in the skip list.");

                    // Offer to show the full list for context
                    System.out.print("Show full skip list? (y/n): ");
                    String show = scanner.nextLine().trim();
                    if (show.equalsIgnoreCase("y")) {
                        skiplist.printSkipList();
                    }
                } else {
                    System.out.println("✗ Node with value " + value + " NOT FOUND in the skip list.");
                    System.out.println("Closest node has value: " + (found != null ? found.key : "N/A"));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }

    /**
     * Generates a specified number of random nodes for testing purposes.
     * Demonstrates the probabilistic nature of skip list height growth.
     */
    private static void generateRandomNodes(Scanner scanner, Skiplist skiplist) {
        System.out.println("\n=== GENERATE RANDOM NODES ===");
        System.out.println("Insert random values to test the skip list's behavior.");
        System.out.println("Each node has a 50% chance of being promoted to higher levels.");

        System.out.print("How many random nodes would you like to insert? ");

        try {
            int count = Integer.parseInt(scanner.nextLine().trim());

            if (count <= 0) {
                System.out.println("Please enter a positive number.");
                return;
            }

            System.out.println("Generating " + count + " random nodes...");

            for (int i = 0; i < count; i++) {
                int randomValue = (int) (Math.random() * 100); // Values 0-99
                skiplist.insertNode(randomValue);
                System.out.println("Inserted: " + randomValue);
            }

            System.out.println("\n✓ Done! " + count + " random nodes have been inserted.");

            // Offer to show the updated list
            System.out.print("Show updated skip list? (y/n): ");
            String show = scanner.nextLine().trim();
            if (show.equalsIgnoreCase("y")) {
                skiplist.printSkipList();
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter an integer.");
        }
    }
}