// Java Program to Construct K-D Tree with Visualization and Detailed Comments
// This program implements a 2-dimensional K-D Tree for spatial data storage and retrieval

import java.util.Arrays;      // For array operations like copying and comparison
import java.util.Scanner;     // For reading user input from console
import java.util.InputMismatchException;  // For handling invalid input types
import java.util.ArrayList;   // For dynamic array to store points during traversal
import java.util.List;        // Interface for collections

class KDTree {
    // Dimension of the space - constant set to 2 for 2D points
    // 'final' means this value cannot be changed after initialization
    // 'static' means this belongs to the class, not to individual objects
    private static final int K = 2;

    /**
     * Node class represents each point in the K-D Tree
     * Contains the point coordinates and references to left and right children
     */
    static class Node {
        int[] point;           // Array to store the 2D point coordinates [x, y]
        Node left, right;      // References to left and right child nodes
        int depth;             // Depth of this node in the tree (useful for visualization)

        /**
         * Constructor to create a new node
         * @param arr The point coordinates to store
         */
        public Node(int[] arr) {
            // Copy the point array to prevent external modifications
            // Arrays.copyOf creates a new array with specified length
            this.point = Arrays.copyOf(arr, K);
            // Initialize children as null (no children yet)
            this.left = this.right = null;
            // Depth will be set when inserted into tree
            this.depth = 0;
        }
    }

    // Root node of the entire K-D Tree
    // This is the entry point for all tree operations
    Node root;

    /**
     * Constructor for KDTree - initializes an empty tree
     */
    KDTree() {
        root = null;  // Start with empty tree (no nodes)
    }

    // ==================== INSERTION OPERATIONS ====================

    /**
     * Recursive method to insert a new point in the tree
     * @param root Current node being examined
     * @param point Point to insert
     * @param depth Current depth in the tree
     * @return Updated node (for linking parent to child)
     * 
     * Time Complexity: O(log n) on average, O(n) in worst case
     * Space Complexity: O(1) if we ignore recursion stack
     */
    Node insertRec(Node root, int[] point, int depth) {
        // BASE CASE 1: If current position is empty, create new node here
        if (root == null) {
            Node newNode = new Node(point);
            newNode.depth = depth;  // Store depth for visualization
            return newNode;
        }

        // Check for duplicate point - compare entire arrays
        // Arrays.equals compares element by element
        if (Arrays.equals(root.point, point)) {
            System.out.println("⚠️ Point " + Arrays.toString(point) + " already exists in the tree.");
            return root;  // Return unchanged root
        }

        // Calculate current dimension (cd) using modulo operation
        // depth % K alternates between 0 (x-axis) and 1 (y-axis)
        // This is the key concept of K-D trees - alternating splitting dimensions
        int cd = depth % K;

        // K-D Tree property: Compare on current dimension
        // If new point's coordinate is less than current node's coordinate,
        // go to left subtree, otherwise go to right subtree
        if (point[cd] < root.point[cd]) {
            // Recursively insert in left subtree
            root.left = insertRec(root.left, point, depth + 1);
        } else {
            // Recursively insert in right subtree
            root.right = insertRec(root.right, point, depth + 1);
        }

        // Return the (unchanged) node pointer
        return root;
    }

    /**
     * Public wrapper method for insertion
     * @param point Point to insert into the tree
     */
    void insert(int[] point) {
        // Validate input dimensions
        if (point.length != K) {
            System.out.println("❌ Error: Point must have exactly " + K + " dimensions.");
            return;
        }
        // Start recursive insertion from root at depth 0
        root = insertRec(root, point, 0);
        System.out.println("✅ Point " + Arrays.toString(point) + " inserted successfully.");
    }

    // ==================== SEARCH OPERATIONS ====================

    /**
     * Recursive method to search for a point in the tree
     * @param root Current node being examined
     * @param point Point to search for
     * @param depth Current depth in the tree
     * @return true if point exists, false otherwise
     * 
     * Time Complexity: O(log n) on average, O(n) in worst case
     */
    boolean searchRec(Node root, int[] point, int depth) {
        // BASE CASE: Reached leaf's child - point not found
        if (root == null) {
            return false;
        }
        
        // Check if current node contains the point we're looking for
        if (Arrays.equals(root.point, point)) {
            return true;  // Point found!
        }

        // Calculate current dimension for comparison
        int cd = depth % K;

        // Navigate based on K-D Tree property
        // Compare on current dimension to decide which subtree might contain the point
        if (point[cd] < root.point[cd]) {
            // Search in left subtree
            return searchRec(root.left, point, depth + 1);
        } else {
            // Search in right subtree
            return searchRec(root.right, point, depth + 1);
        }
    }

    /**
     * Public wrapper method for searching
     * @param point Point to search for
     * @return true if point exists, false otherwise
     */
    boolean search(int[] point) {
        // Validate input dimensions
        if (point.length != K) {
            System.out.println("❌ Error: Point must have exactly " + K + " dimensions.");
            return false;
        }
        return searchRec(root, point, 0);
    }

    // ==================== RANGE SEARCH OPERATIONS ====================

    /**
     * Recursive method to find all points within a given rectangular range
     * @param root Current node being examined
     * @param lower Lower bounds [minX, minY]
     * @param upper Upper bounds [maxX, maxY]
     * @param depth Current depth in the tree
     * @param result List to store points found in range
     * 
     * Time Complexity: O(√n + k) where k is number of points in range
     */
    void rangeSearchRec(Node root, int[] lower, int[] upper, int depth, List<int[]> result) {
        // BASE CASE: Empty subtree
        if (root == null) {
            return;
        }

        // Check if current node's point lies within the query rectangle
        boolean inside = true;
        for (int i = 0; i < K; i++) {
            // Point must be >= lower bound and <= upper bound for all dimensions
            if (root.point[i] < lower[i] || root.point[i] > upper[i]) {
                inside = false;
                break;  // No need to check further dimensions
            }
        }

        // If point is inside the rectangle, add to result list
        if (inside) {
            result.add(root.point);
        }

        // Calculate current dimension for pruning decisions
        int cd = depth % K;

        // PRUNING STRATEGY: Only explore subtrees that could contain points in range
        // If lower bound is less than or equal to current node's coordinate,
        // left subtree might contain points in range
        if (lower[cd] <= root.point[cd]) {
            rangeSearchRec(root.left, lower, upper, depth + 1, result);
        }
        
        // If upper bound is greater than or equal to current node's coordinate,
        // right subtree might contain points in range
        if (upper[cd] >= root.point[cd]) {
            rangeSearchRec(root.right, lower, upper, depth + 1, result);
        }
    }

    /**
     * Public wrapper method for range search
     * @param lower Lower bounds [minX, minY]
     * @param upper Upper bounds [maxX, maxY]
     */
    void rangeSearch(int[] lower, int[] upper) {
        // Validate input dimensions
        if (lower.length != K || upper.length != K) {
            System.out.println("❌ Error: Range boundaries must have exactly " + K + " dimensions.");
            return;
        }
        
        // Validate range logic - lower must be <= upper
        for (int i = 0; i < K; i++) {
            if (lower[i] > upper[i]) {
                System.out.println("❌ Error: Lower bound must be ≤ upper bound for dimension " + i);
                return;
            }
        }
        
        // Create list to store results
        List<int[]> result = new ArrayList<>();
        // Start recursive search
        rangeSearchRec(root, lower, upper, 0, result);
        
        // Display results
        System.out.println("📊 Points in range " + Arrays.toString(lower) + " to " + Arrays.toString(upper) + ":");
        if (result.isEmpty()) {
            System.out.println("   No points found in this range.");
        } else {
            for (int[] point : result) {
                System.out.println("   " + Arrays.toString(point));
            }
            System.out.println("   Total: " + result.size() + " point(s)");
        }
    }

    // ==================== DELETION OPERATIONS ====================

    /**
     * Recursive method to find minimum node along a given dimension
     * @param root Current node being examined
     * @param d Dimension to find minimum in (0 for x, 1 for y)
     * @param depth Current depth
     * @return Node with minimum value in dimension d
     */
    Node findMinRec(Node root, int d, int depth) {
        // BASE CASE: Empty subtree
        if (root == null) {
            return null;
        }

        int cd = depth % K;

        // If current node splits on the dimension we're querying
        if (cd == d) {
            // Minimum must be in left subtree (if exists)
            // This is because all smaller values in this dimension go left
            if (root.left == null) {
                return root;
            }
            return findMinRec(root.left, d, depth + 1);
        }

        // If current node splits on other dimension
        // Need to check both subtrees as minimum could be anywhere
        Node leftMin = findMinRec(root.left, d, depth + 1);
        Node rightMin = findMinRec(root.right, d, depth + 1);
        
        // Compare candidates and return the minimum
        Node minNode = root;
        
        if (leftMin != null && leftMin.point[d] < minNode.point[d]) {
            minNode = leftMin;
        }
        if (rightMin != null && rightMin.point[d] < minNode.point[d]) {
            minNode = rightMin;
        }
        
        return minNode;
    }

    /**
     * Recursive method to delete a point from the tree
     * @param root Current node being examined
     * @param point Point to delete
     * @param depth Current depth
     * @return Updated node
     * 
     * This is the most complex operation in K-D trees
     */
    Node deleteRec(Node root, int[] point, int depth) {
        // BASE CASE: Point not found
        if (root == null) {
            return null;
        }

        int cd = depth % K;

        // FOUND THE NODE TO DELETE
        if (Arrays.equals(root.point, point)) {
            // CASE 1: Node has right child
            if (root.right != null) {
                // Find minimum in right subtree along current dimension
                Node min = findMinRec(root.right, cd, depth + 1);
                // Replace current node's point with the minimum
                root.point = Arrays.copyOf(min.point, K);
                // Recursively delete the minimum node from right subtree
                root.right = deleteRec(root.right, min.point, depth + 1);
            }
            // CASE 2: Node has left child but no right child
            else if (root.left != null) {
                // Find minimum in left subtree along current dimension
                Node min = findMinRec(root.left, cd, depth + 1);
                // Replace current node's point with the minimum
                root.point = Arrays.copyOf(min.point, K);
                // Move left subtree to right (to maintain K-D Tree property)
                root.right = deleteRec(root.left, min.point, depth + 1);
                root.left = null;
            }
            // CASE 3: Leaf node (no children)
            else {
                return null;  // Simply remove the node
            }
            return root;
        }

        // NOT THE TARGET NODE - search in appropriate subtree
        if (point[cd] < root.point[cd]) {
            root.left = deleteRec(root.left, point, depth + 1);
        } else {
            root.right = deleteRec(root.right, point, depth + 1);
        }
        return root;
    }

    /**
     * Public wrapper method for deletion
     * @param point Point to delete from the tree
     */
    void delete(int[] point) {
        // Validate input
        if (point.length != K) {
            System.out.println("❌ Error: Point must have exactly " + K + " dimensions.");
            return;
        }
        
        // Check if point exists before attempting deletion
        if (!search(point)) {
            System.out.println("❌ Point " + Arrays.toString(point) + " not found in the tree.");
            return;
        }
        
        // Perform deletion
        root = deleteRec(root, point, 0);
        System.out.println("🗑️ Point " + Arrays.toString(point) + " deleted successfully.");
    }

    // ==================== VISUALIZATION METHODS ====================

    /**
     * Recursive method to collect all points for visualization
     * @param root Current node
     * @param points List to store points
     */
    void collectPoints(Node root, List<int[]> points) {
        if (root == null) return;
        points.add(root.point);
        collectPoints(root.left, points);
        collectPoints(root.right, points);
    }

    /**
     * Creates a text-based visualization of the 2D space
     * Shows all points in a grid format
     */
    void visualize2DSpace() {
        System.out.println("\n🗺️  VISUALIZATION OF 2D SPACE:");
        
        // Collect all points
        List<int[]> points = new ArrayList<>();
        collectPoints(root, points);
        
        if (points.isEmpty()) {
            System.out.println("   Tree is empty - no points to visualize");
            return;
        }
        
        // Find boundaries
        int minX = Integer.MAX_VALUE, maxX = Integer.MIN_VALUE;
        int minY = Integer.MAX_VALUE, maxY = Integer.MIN_VALUE;
        
        for (int[] p : points) {
            minX = Math.min(minX, p[0]);
            maxX = Math.max(maxX, p[0]);
            minY = Math.min(minY, p[1]);
            maxY = Math.max(maxY, p[1]);
        }
        
        // Add padding
        minX = Math.min(minX - 1, 0);
        maxX = maxX + 1;
        minY = Math.min(minY - 1, 0);
        maxY = maxY + 1;
        
        // Create grid for visualization
        System.out.println("\n   Y↑");
        for (int y = maxY; y >= minY; y--) {
            System.out.print(String.format("%3d | ", y));
            for (int x = minX; x <= maxX; x++) {
                boolean pointFound = false;
                for (int[] p : points) {
                    if (p[0] == x && p[1] == y) {
                        System.out.print("● ");
                        pointFound = true;
                        break;
                    }
                }
                if (!pointFound) {
                    System.out.print(". ");
                }
            }
            System.out.println();
        }
        
        // Print X-axis
        System.out.print("    +-");
        for (int x = minX; x <= maxX; x++) {
            System.out.print("--");
        }
        System.out.println();
        
        System.out.print("      ");
        for (int x = minX; x <= maxX; x++) {
            System.out.print(x + " ");
            if (x < 10) System.out.print(" ");
        }
        System.out.println("  X→");
        
        // Legend
        System.out.println("\n   Legend: ● = Point in tree, . = Empty space");
    }

    /**
     * Creates a tree structure visualization
     * Shows the hierarchical structure of the K-D Tree
     */
    void visualizeTree() {
        System.out.println("\n🌳 K-D TREE STRUCTURE:");
        if (root == null) {
            System.out.println("   (empty tree)");
            return;
        }
        visualizeTreeRec(root, "", true);
    }

    /**
     * Recursive helper for tree visualization
     * @param node Current node
     * @param prefix String prefix for indentation
     * @param isTail Whether this is the last child
     */
    void visualizeTreeRec(Node node, String prefix, boolean isTail) {
        if (node != null) {
            // Determine splitting dimension based on depth
            String splitDim = (node.depth % K == 0) ? "X" : "Y";
            
            // Print current node with its coordinates and splitting dimension
            System.out.println(prefix + (isTail ? "└── " : "├── ") + 
                             Arrays.toString(node.point) + " [split: " + splitDim + "]");
            
            // Prepare prefix for children
            String childPrefix = prefix + (isTail ? "    " : "│   ");
            
            // Recursively print children
            if (node.left != null || node.right != null) {
                if (node.left != null) {
                    visualizeTreeRec(node.left, childPrefix, node.right == null);
                }
                if (node.right != null) {
                    visualizeTreeRec(node.right, childPrefix, true);
                }
            }
        }
    }

    /**
     * Shows detailed information about the tree structure
     */
    void showTreeStats() {
        System.out.println("\n📊 TREE STATISTICS:");
        if (root == null) {
            System.out.println("   Tree is empty");
            return;
        }
        
        List<int[]> points = new ArrayList<>();
        collectPoints(root, points);
        
        System.out.println("   Total nodes: " + points.size());
        
        // Calculate height
        int height = calculateHeight(root);
        System.out.println("   Tree height: " + height);
        
        // Calculate balance factor
        int leftHeight = calculateHeight(root.left);
        int rightHeight = calculateHeight(root.right);
        int balanceFactor = Math.abs(leftHeight - rightHeight);
        System.out.println("   Balance factor: " + balanceFactor + 
                         (balanceFactor <= 1 ? " (balanced)" : " (unbalanced)"));
        
        // Show distribution
        System.out.println("   Left subtree nodes: " + countNodes(root.left));
        System.out.println("   Right subtree nodes: " + countNodes(root.right));
    }

    /**
     * Calculate height of a subtree
     */
    int calculateHeight(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(calculateHeight(node.left), calculateHeight(node.right));
    }

    /**
     * Count nodes in a subtree
     */
    int countNodes(Node node) {
        if (node == null) return 0;
        return 1 + countNodes(node.left) + countNodes(node.right);
    }

    /**
     * Display all points in sorted order (in-order traversal)
     */
    void displayAllPoints() {
        System.out.println("\n📋 ALL POINTS IN TREE (in-order traversal):");
        if (root == null) {
            System.out.println("   Tree is empty.");
            return;
        }
        
        List<int[]> points = new ArrayList<>();
        collectPoints(root, points);
        
        // Sort for better readability
        points.sort((a, b) -> {
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]);
        });
        
        for (int[] point : points) {
            System.out.println("   " + Arrays.toString(point));
        }
        System.out.println("   Total: " + points.size() + " point(s)");
    }

    // ==================== USER INTERFACE METHODS ====================

    /**
     * Read a 2D point from user input with validation
     * @param scanner Scanner object for input
     * @return Array containing [x, y] coordinates
     */
    static int[] readPoint(Scanner scanner) {
        int[] point = new int[K];
        for (int i = 0; i < K; i++) {
            while (true) {
                try {
                    String dimName = (i == 0) ? "X" : "Y";
                    System.out.print("   Enter " + dimName + "-coordinate: ");
                    point[i] = scanner.nextInt();
                    break;  // Exit loop if input is valid
                } catch (InputMismatchException e) {
                    System.out.println("   ❌ Invalid input. Please enter an integer.");
                    scanner.nextLine();  // Clear the invalid input
                }
            }
        }
        return point;
    }

    /**
     * Display the main menu
     */
    static void displayMenu() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    K-D TREE OPERATIONS MENU");
        System.out.println("=".repeat(60));
        System.out.println("  INSERT OPERATIONS:");
        System.out.println("    1. ➕ Insert a new point");
        System.out.println();
        System.out.println("  SEARCH OPERATIONS:");
        System.out.println("    2. 🔍 Search for a point");
        System.out.println("    3. 📏 Range search (find points in rectangle)");
        System.out.println();
        System.out.println("  DELETE OPERATIONS:");
        System.out.println("    4. 🗑️  Delete a point");
        System.out.println();
        System.out.println("  VISUALIZATION:");
        System.out.println("    5. 🗺️  Visualize 2D space");
        System.out.println("    6. 🌳 Visualize tree structure");
        System.out.println("    7. 📊 Show tree statistics");
        System.out.println("    8. 📋 Display all points");
        System.out.println();
        System.out.println("  SYSTEM:");
        System.out.println("    9. 🧹 Clear tree (start fresh)");
        System.out.println("    10. 🚪 Exit program");
        System.out.println("=".repeat(60));
        System.out.print("Choose an option (1-10): ");
    }

    /**
     * Clear the entire tree
     */
    void clearTree() {
        root = null;
        System.out.println("🧹 Tree cleared successfully.");
    }

    // ==================== MAIN METHOD ====================

    /**
     * Main method - entry point of the program
     * Contains the main program loop and menu handling
     */
    public static void main(String[] args) {
        // Create a new K-D Tree instance
        KDTree tree = new KDTree();
        // Create scanner for user input
        Scanner scanner = new Scanner(System.in);
        
        // Display welcome message
        System.out.println("\n" + "⭐".repeat(60));
        System.out.println("            WELCOME TO K-D TREE DEMONSTRATION PROGRAM");
        System.out.println("⭐".repeat(60));
        System.out.println("\n📐 This program implements a 2-dimensional K-D Tree");
        System.out.println("📌 Points are stored as (X, Y) coordinates");
        System.out.println("🎯 You can insert, search, delete, and visualize points");
        
        // Insert sample points for demonstration
        System.out.println("\n📌 Inserting sample points for demonstration:");
        int[][] samplePoints = { 
            { 3, 6 },    // Point A
            { 17, 15 },  // Point B
            { 13, 15 },  // Point C
            { 6, 12 },   // Point D
            { 9, 1 },    // Point E
            { 2, 7 },    // Point F
            { 10, 19 }   // Point G
        };
        
        // Insert each sample point
        for (int[] point : samplePoints) {
            tree.insert(point);
        }
        
        // Show initial visualization
        System.out.println("\n🎯 Initial tree created with 7 sample points.");
        tree.visualize2DSpace();
        tree.visualizeTree();

        // Main program loop - runs until user chooses to exit
        while (true) {
            // Display the menu
            displayMenu();

            int choice;
            try {
                // Read user's menu choice
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                // Handle non-integer input
                System.out.println("❌ Invalid input. Please enter a number between 1 and 10.");
                scanner.nextLine();  // Clear the invalid input
                continue;  // Go back to menu
            }

            // Process user's choice using switch statement
            switch (choice) {
                case 1 -> {
                    // INSERT POINT
                    System.out.println("\n➕ INSERT NEW POINT:");
                    System.out.println("   Enter coordinates for the new point:");
                    int[] insertPoint = readPoint(scanner);
                    tree.insert(insertPoint);
                    // Show updated visualization
                    tree.visualize2DSpace();
                    tree.visualizeTree();
                }

                case 2 -> {
                    // SEARCH POINT
                    System.out.println("\n🔍 SEARCH FOR A POINT:");
                    System.out.println("   Enter coordinates to search for:");
                    int[] searchPoint = readPoint(scanner);
                    
                    // Perform search
                    long startTime = System.nanoTime();  // Measure search time
                    boolean found = tree.search(searchPoint);
                    long endTime = System.nanoTime();
                    
                    // Display result with visual feedback
                    if (found) {
                        System.out.println("   ✅ Point " + Arrays.toString(searchPoint) + " FOUND in the tree!");
                    } else {
                        System.out.println("   ❌ Point " + Arrays.toString(searchPoint) + " NOT FOUND in the tree.");
                    }
                    System.out.println("   ⏱️  Search took: " + (endTime - startTime) + " nanoseconds");
                }

                case 3 -> {
                    // RANGE SEARCH
                    System.out.println("\n📏 RANGE SEARCH:");
                    System.out.println("   Enter LOWER bound of the rectangle:");
                    int[] lower = readPoint(scanner);
                    System.out.println("   Enter UPPER bound of the rectangle:");
                    int[] upper = readPoint(scanner);
                    
                    // Perform range search
                    System.out.println("\n   Searching for points in rectangle:");
                    System.out.println("   From: " + Arrays.toString(lower));
                    System.out.println("   To:   " + Arrays.toString(upper));
                    tree.rangeSearch(lower, upper);
                }

                case 4 -> {
                    // DELETE POINT
                    System.out.println("\n🗑️  DELETE A POINT:");
                    System.out.println("   Enter coordinates of the point to delete:");
                    int[] deletePoint = readPoint(scanner);
                    
                    // Perform deletion
                    tree.delete(deletePoint);
                    // Show updated visualization
                    tree.visualize2DSpace();
                    tree.visualizeTree();
                }

                case 5 -> // VISUALIZE 2D SPACE
                    tree.visualize2DSpace();

                case 6 -> // VISUALIZE TREE STRUCTURE
                    tree.visualizeTree();

                case 7 -> // SHOW TREE STATISTICS
                    tree.showTreeStats();

                case 8 -> // DISPLAY ALL POINTS
                    tree.displayAllPoints();

                case 9 -> {
                    // CLEAR TREE
                    System.out.println("\n🧹 CLEAR TREE:");
                    System.out.print("   Are you sure? This will delete all points (y/n): ");
                    String confirm = scanner.next();
                    if (confirm.toLowerCase().startsWith("y")) {
                        tree.clearTree();
                        tree.visualize2DSpace();
                    } else {
                        System.out.println("   Operation cancelled.");
                    }
                }

                case 10 -> {
                    // EXIT PROGRAM
                    System.out.println("\n🚪 Exiting program...");
                    System.out.println("📊 Final tree statistics:");
                    tree.showTreeStats();
                    System.out.println("\n👋 Thank you for using the K-D Tree program!");
                    scanner.close();  // Close the scanner
                    System.exit(0);   // Terminate the program
                }

                default -> // Invalid choice
                    System.out.println("❌ Invalid option. Please choose a number between 1 and 10.");
            }
            
            // Pause before showing menu again (for better readability)
            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();  // Consume newline
            scanner.nextLine();  // Wait for Enter
        }
    }
}