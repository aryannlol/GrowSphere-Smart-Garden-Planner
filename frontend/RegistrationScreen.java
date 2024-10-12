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

public class RegistrationScreen {

    public static void display(Stage primaryStage) {
        primaryStage.setTitle("Register");

        // Create registration form elements
        Label userLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Button registerButton = new Button("Register");
        Label successLabel = new Label(""); // For success messages

        // Handle registration button action
        registerButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            // Database connection and insertion logic
            try {
                Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "", "");
                String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();

                successLabel.setText("Registration successful!");
            } catch (SQLException ex) {
                ex.printStackTrace();
                successLabel.setText("Error: " + ex.getMessage());
            }
        });

        // Layout for the registration screen
        VBox layout = new VBox(10);
        layout.getChildren().addAll(userLabel, usernameField, passLabel, passwordField, registerButton, successLabel);

        Scene scene = new Scene(layout, 300, 200);
        primaryStage.setScene(scene);
    }
}
