package frontend;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginScreen {

    public static void display(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // Create login form elements
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Label errorLabel = new Label(""); // For error messages

        // Handle login button action
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Database check for login
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/community_garden_planner", "root", "minecraft@OP1")) {

                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    errorLabel.setText("Login successful!");

                    // Navigate to the region selection screen after login success
                    VBox mainLayout = new VBox(); // Create a new VBox for the main layout
                    new RegionSelectionScreen(mainLayout, username); // Pass the layout and username

                    primaryStage.setScene(new Scene(mainLayout, 600, 400)); // Set the new scene
                    primaryStage.show(); // Show the updated stage
                } else {
                    errorLabel.setText("Invalid credentials.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                errorLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Layout for the login screen
        VBox layout = new VBox(10);
        layout.getChildren().addAll(userLabel, usernameField, passLabel, passwordField, loginButton, errorLabel);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show(); // Ensure the stage is shown
    }
}
