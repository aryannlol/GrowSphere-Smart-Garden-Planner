package backend;

import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List; // Import HashSet
import java.util.Scanner;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        backend.UserDAO userDAO = new backend.UserDAO();
        PlantDAO plantDAO = new PlantDAO();

        System.out.println("Welcome to the Community Garden Planner!");

        while (true) { // Loop to keep asking for user action
            System.out.println("\nMain Menu:");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option (1, 2, or 3): ");

            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Consume invalid input
                continue;
            }

            if (choice == 1) {
                // Registration
                String username = registerUser(scanner, userDAO);
                if (username != null) {
                    // After successful registration, prompt for region
                    recommendPlants(scanner, plantDAO, username);
                }
            } else if (choice == 2) {
                // Login
                String username = loginUser(scanner, userDAO);
                if (username != null) {
                    // After successful login, update user data
                    updateUserSelection(scanner, plantDAO, userDAO, username);

                }
            } else if (choice == 3) {
                System.out.println("Exiting the program. Goodbye!");
                break;
            } else {
                System.out.println("Invalid choice! Please select 1, 2, or 3.");
            }
        }

        scanner.close();
    }

    private static String registerUser(Scanner scanner, UserDAO userDAO) {
        while (true) {
            System.out.println("\n--- Registration ---");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();
            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            User newUser = new User(username, password, email);

            try {
                userDAO.registerUser(newUser);
                System.out.println("Registration successful! Welcome, " + username + "!");

                // Prompt for region and selected plants
                System.out.print("Select your region: ");
                String region = scanner.nextLine();
                System.out.print("Enter the plants you wish to grow (comma-separated): ");
                String selectedPlants = scanner.nextLine();

                // Update user region and selected plants
                userDAO.updateUserRegionAndPlants(username, region, selectedPlants);
                return username; // Return the username for further use
            } catch (SQLException e) {
                if (e.getErrorCode() == 1062) { // Duplicate entry error code
                    System.out.println("Username already exists. Please choose a different username.");
                } else {
                    System.out.println("Error during registration: " + e.getMessage());
                }
                System.out.println("Please try again.");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String loginUser(Scanner scanner, UserDAO userDAO) {
        while (true) {
            System.out.println("\n--- Login ---");
            System.out.print("Enter username: ");
            String username = scanner.nextLine();
            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            try {
                if (userDAO.authenticateUser(username, password)) {
                    System.out.println("Login successful! Welcome, " + username + "!");
                    return username; // Login successful
                } else {
                    System.out.println("Invalid username or password. Please try again.");
                }
            } catch (SQLException e) {
                System.out.println("Error during login: " + e.getMessage());
                System.out.println("Please try again.");
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private static void updateUserSelection(Scanner scanner, PlantDAO plantDAO, UserDAO userDAO, String username) {
        while (true) {
            // Present options to the user
            System.out.println("\nSelect your region for plant recommendations:");
            System.out.println("1. North India");
            System.out.println("2. South India");
            System.out.println("3. East India");
            System.out.println("4. West India");
            System.out.print("Enter the number corresponding to your region: ");
    
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Consume invalid input
                continue;
            }
    
            String region = "";
            switch (choice) {
                case 1:
                    region = "North India";
                    break;
                case 2:
                    region = "South India";
                    break;
                case 3:
                    region = "East India";
                    break;
                case 4:
                    region = "West India";
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid region.");
                    continue; // Loop again for valid input
            }
    
            // Fetch and display recommended plants for the selected region
            try {
                List<Plant> recommendedPlants = plantDAO.PlantsByRegion(region);
                Set<String> uniquePlantNames = new HashSet<>(); // To store unique plant names
    
                if (recommendedPlants.isEmpty()) {
                    System.out.println("No plants found for the specified region.");
                } else {
                    System.out.println("\nRecommended plants for " + username + " in " + region + ":");
                    for (Plant plant : recommendedPlants) {
                        // Check for uniqueness based on plant name
                        if (uniquePlantNames.add(plant.getName())) {
                            System.out.println("Name: " + plant.getName() + ", Type: " + plant.getType() +
                                    ", Care Instructions: " + plant.getCareInstructions());
                        }
                    }
    
                    // Update the user's selected region and plants in the database
                    userDAO.updateUserSelection(username, region, uniquePlantNames);
                }
            } catch (SQLException e) {
                System.out.println("Error fetching plant recommendations: " + e.getMessage());
            }
            break; // Exit after displaying recommendations
        }
    }
    
    
    
    private static void recommendPlants(Scanner scanner, PlantDAO plantDAO, String username) {
        while (true) {
            // Present options to the user
            System.out.println("\nSelect your region for plant recommendations:");
            System.out.println("1. North India");
            System.out.println("2. South India");
            System.out.println("3. East India");
            System.out.println("4. West India");
            System.out.print("Enter the number corresponding to your region: ");
            
            int choice;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (Exception e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // Consume invalid input
                continue;
            }
    
            String region = "";
            switch (choice) {
                case 1:
                    region = "North India";
                    break;
                case 2:
                    region = "South India";
                    break;
                case 3:
                    region = "East India";
                    break;
                case 4:
                    region = "West India";
                    break;
                default:
                    System.out.println("Invalid choice. Please select a valid region.");
                    continue; // Loop again for valid input
            }
    
            // Fetch and display recommended plants for the selected region
            try {
                List<Plant> recommendedPlants = plantDAO.PlantsByRegion(region);
                Set<String> uniquePlantNames = new HashSet<>(); // To store unique plant names
    
                if (recommendedPlants.isEmpty()) {
                    System.out.println("No plants found for the specified region.");
                } else {
                    System.out.println("\nRecommended plants for " + username + " in " + region + ":");
                    for (Plant plant : recommendedPlants) {
                        // Check for uniqueness based on plant name
                        if (uniquePlantNames.add(plant.getName())) {
                            System.out.println("Name: " + plant.getName() + ", Type: " + plant.getType() +
                                    ", Care Instructions: " + plant.getCareInstructions());
                        }
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error fetching plant recommendations: " + e.getMessage());
            }
            break; // Exit after displaying recommendations
        }
    }
    
    
    private static void displayUserData(UserDAO userDAO, String username) {
        try {
            User user = userDAO.getUserByUsername(username); // Implement this method in UserDAO
            System.out.println("Username: " + user.getUsername());
            System.out.println("Region: " + user.getRegion());
            System.out.println("Selected Plants: " + user.getSelectedPlants());
        } catch (SQLException e) {
            System.out.println("Error fetching user data: " + e.getMessage());
        }
    }
}
