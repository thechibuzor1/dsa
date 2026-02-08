package skiplist;

import java.util.Scanner;

/**
 * Interactive application for testing and visualizing the SkipList data
 * structure. Provides a menu-driven interface for inserting, deleting,
 * searching, and displaying nodes.
 */
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