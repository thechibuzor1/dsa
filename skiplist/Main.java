package skiplist;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Skiplist skiplist = new Skiplist();
        
        System.out.println("=== Skip List Interactive Application ===");
        
        boolean running = true;
        while (running) {
            printMenu();
            
            System.out.print("\nEnter your choice (1-6): ");
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    // Insert node
                    insertNodeOperation(scanner, skiplist);
                    break;
                    
                case "2":
                    // Delete node
                    deleteNodeOperation(scanner, skiplist);
                    break;
                    
                case "3":
                    // Search node
                    searchNodeOperation(scanner, skiplist);
                    break;
                    
                case "4":
                    // Print skip list
                    skiplist.printSkipList();
                    break;
                    
                case "5":
                    // Generate random nodes
                    generateRandomNodes(scanner, skiplist);
                    break;
                    
                case "6":
                    // Exit
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
    
    private static void printMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Insert a node");
        System.out.println("2. Delete a node");
        System.out.println("3. Search for a node");
        System.out.println("4. Print skip list");
        System.out.println("5. Generate random nodes");
        System.out.println("6. Exit");
    }
    
    private static void insertNodeOperation(Scanner scanner, Skiplist skiplist) {
        System.out.println("\n=== INSERT NODE ===");
        
        while (true) {
            System.out.print("Enter integer value to insert (or 'b' to go back): ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("b")) {
                break;
            }
            
            try {
                int value = Integer.parseInt(input);
                Node inserted = skiplist.insertNode(value);
                
                if (inserted != null) {
                    System.out.println("Successfully inserted node with value: " + value);
                    
                    // Ask if user wants to see the updated list
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
    
    private static void deleteNodeOperation(Scanner scanner, Skiplist skiplist) {
        System.out.println("\n=== DELETE NODE ===");
        
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
                    System.out.println("Successfully deleted node with value: " + value);
                    
                    // Ask if user wants to see the updated list
                    System.out.print("Show updated skip list? (y/n): ");
                    String show = scanner.nextLine().trim();
                    if (show.equalsIgnoreCase("y")) {
                        skiplist.printSkipList();
                    }
                } else {
                    System.out.println("Node with value " + value + " not found in the skip list.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }
    
    private static void searchNodeOperation(Scanner scanner, Skiplist skiplist) {
        System.out.println("\n=== SEARCH NODE ===");
        
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
                    System.out.println("Node with value " + value + " FOUND in the skip list.");
                    
                    // Show some context if possible
                    System.out.print("Show full skip list? (y/n): ");
                    String show = scanner.nextLine().trim();
                    if (show.equalsIgnoreCase("y")) {
                        skiplist.printSkipList();
                    }
                } else {
                    System.out.println("Node with value " + value + " NOT FOUND in the skip list.");
                    System.out.println("Closest node has value: " + (found != null ? found.key : "N/A"));
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter an integer.");
            }
        }
    }
    
    private static void generateRandomNodes(Scanner scanner, Skiplist skiplist) {
        System.out.println("\n=== GENERATE RANDOM NODES ===");
        
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
            
            System.out.println("\nDone! " + count + " random nodes have been inserted.");
            
            // Ask if user wants to see the updated list
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