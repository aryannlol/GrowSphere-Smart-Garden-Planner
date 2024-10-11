package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserDAO {

    private int loggedInUserId = -1; // To store the logged-in user's ID (-1 means no user is logged in)

    // Method to hash passwords
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    // Method to register a new user
    public void registerUser(User user) throws SQLException, NoSuchAlgorithmException {
        String hashedPassword = hashPassword(user.getPassword());
        String query = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, user.getUsername());
            statement.setString(2, hashedPassword); // Store the hashed password
            statement.setString(3, user.getEmail());
            statement.executeUpdate();
        }
    }

    // Method to authenticate user
    public boolean authenticateUser(String username, String password) throws SQLException, NoSuchAlgorithmException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");

                // Hash the input password and compare
                String hashedInputPassword = hashPassword(password);
                return storedHashedPassword.equals(hashedInputPassword); // Check if hashed passwords match
            } else {
                return false; // User not found
            }
        }
    }

    // Method to log in the user and store their ID
    public boolean validateUser(String username, String password) throws SQLException, NoSuchAlgorithmException {
        String query = "SELECT id, password FROM users WHERE username = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedPasswordHash = resultSet.getString("password");
                if (storedPasswordHash.equals(hashPassword(password))) {
                    loggedInUserId = resultSet.getInt("id"); // Store logged-in user ID
                    return true;
                }
            }

            return false; // User not found or incorrect password
        }
    }

    // Method to retrieve the logged-in user's ID
    public int getLoggedInUserId() {
        return loggedInUserId;
    }

    // Method to update user's region and selected plants
    public void updateUserRegionAndPlants(String username, String region, String selectedPlants) throws SQLException {
        String query = "UPDATE users SET region = ?, selected_plants = ? WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, region);
            statement.setString(2, selectedPlants);
            statement.setString(3, username);
            statement.executeUpdate();
        }
    }
    public void updateUserRegion(String username, String region) throws SQLException {
        String query = "UPDATE users SET region = ? WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, region);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }

    // Method to retrieve a user's information by username
    public User getUserByUsername(String username) throws SQLException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new User(
                        resultSet.getString("username"),
                        resultSet.getString("password"),
                        resultSet.getString("email"),
                        resultSet.getString("region"),
                        resultSet.getString("selected_plants")
                );
            } else {
                return null; // User not found
            }
        }
    }

    // Method to retrieve all usernames (optional)
    public List<String> getAllUsernames() throws SQLException {
        List<String> usernames = new ArrayList<>();
        String query = "SELECT username FROM users";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                usernames.add(resultSet.getString("username"));
            }
        }

        return usernames;
    }

    // Method to update user's region and selected plants using a Set of plants
    public void updateUserSelection(String username, String region, Set<String> plants) throws SQLException {
        String updateQuery = "UPDATE users SET region = ?, selected_plants = ? WHERE username = ?";

        // Convert the Set<String> of plants to a comma-separated string
        String selectedPlants = String.join(",", plants);

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setString(1, region);
            statement.setString(2, selectedPlants);
            statement.setString(3, username);
            statement.executeUpdate();
        }
    }
}
