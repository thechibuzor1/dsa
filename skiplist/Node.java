package skiplist;

/**
 * Represents a single node in a skip list data structure.
 * Each node contains a key/value and maintains references to adjacent nodes
 * in four directions: above, below, next (right), and prev (left).
 * 
 * Skip list nodes form a multi-level, doubly-linked structure where:
 * - Horizontal links (next/prev) connect nodes at the same level
 * - Vertical links (above/below) connect nodes at different levels
 */
public class Node {
    
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