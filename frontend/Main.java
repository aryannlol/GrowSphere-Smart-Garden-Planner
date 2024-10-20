package frontend;

import frontend.LoginController;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import backend.DatabaseConnection;
import backend.Plant;
import backend.User;
import backend.UserDAO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Priority;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import javafx.util.Duration;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import java.security.NoSuchAlgorithmException;
import javafx.scene.image.Image;

public class Main extends Application {
    private int gradientIndex = 0;
    private final Color[] colors = {
            Color.web("#ff6347"),  // Tomato
            Color.web("#ff8c00"),  // Dark Orange
            Color.web("#ffd700"),  // Gold
            Color.web("#32cd32"),  // Lime Green
            Color.web("#4682b4"),  // Steel Blue
            Color.web("#6a5acd")   // Slate Blue
    };

    private VBox mainLayout;
    private StackPane root;  // Main root layout
    private UserDAO userDAO = new UserDAO(); // Initialize UserDAO instance

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Community Garden Planner");
        primaryStage.setWidth(1100);
        primaryStage.setHeight(700);

        // Set the application icon
        Image appIcon = new Image(new File("C:/Users/aryan/BAGICHA/resources/plant.png").toURI().toString());
        primaryStage.getIcons().add(appIcon);

        // Create the main layout
        root = new StackPane();
        mainLayout = new VBox(20); // Space between elements
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: transparent;");
        mainLayout.setAlignment(Pos.CENTER); // Center align all elements vertically and horizontally

        // Create the main title with gradient

        Label titleLabel = new Label("COMMUNITY GARDEN PLANNER");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-background-color: transparent; -fx-background-radius: 5; -fx-padding: 10;");

        // Create buttons for login and registration
        Button loginButton = createButton("Login");
        Button registerButton = createButton("Register");

        // Add elements to the main layout
        mainLayout.getChildren().addAll(titleLabel, loginButton, registerButton);

        // Add footer text
        Label footerLabel = new Label("Made by Aryan and Vansh");
        footerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        footerLabel.setTextFill(Color.WHITE);
        mainLayout.getChildren().add(footerLabel);
        VBox.setMargin(footerLabel, new Insets(20, 0, 0, 0));

        // Add main layout to root
        root.getChildren().add(mainLayout);

        // Create Scene
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start gradient animation for the background color
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            gradientIndex = (gradientIndex + 1) % colors.length;
            Color nextColor = colors[gradientIndex];
            Color currentColor = colors[(gradientIndex - 1 + colors.length) % colors.length];
            mainLayout.setStyle(String.format("-fx-background-color: linear-gradient(to right, %s, %s);",
                    currentColor.toString().replace("0x", "#"),
                    nextColor.toString().replace("0x", "#")));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        // Handle login button click
        loginButton.setOnAction(e -> showLoginForm());

        // Handle register button click
        registerButton.setOnAction(e -> showRegisterForm());
    }

    private void showLoginForm() {
        // Clear the previous content
        mainLayout.getChildren().clear();

        // Create login form
        Label loginLabel = new Label("Login");
        loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        loginLabel.setTextFill(Color.WHITE);

        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setPrefWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(250);

        Label feedbackLabel = new Label();
        feedbackLabel.setTextFill(Color.RED);

        Button submitLoginButton = createButton("Submit");
        submitLoginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            try {
                if (userDAO.validateUser(username, password)) {
                    feedbackLabel.setText("Logged in!");

                    // Create a new VBox layout for the region selection screen
                    VBox mainLayout = new VBox(); // Create a new VBox for the main layout
                    new RegionSelectionScreen(mainLayout, username); // Pass the layout and username

                    // Set the new scene to the primary stage
                    Stage primaryStage = (Stage) submitLoginButton.getScene().getWindow(); // Get the current primary stage
                    primaryStage.setScene(new Scene(mainLayout, 1100, 700)); // Set the new scene
                    primaryStage.show(); // Show the updated stage
                } else {
                    feedbackLabel.setText("Invalid username or password.");
                }
            } catch (SQLException | NoSuchAlgorithmException ex) {
                feedbackLabel.setText("Error: " + ex.getMessage());
            }
        });


        Button backButton = createButton("Back");
        backButton.setOnAction(e -> resetToMainMenu());

        GridPane loginForm = new GridPane();
        loginForm.setVgap(10);
        loginForm.setHgap(10);
        loginForm.setPadding(new Insets(20));
        loginForm.setAlignment(Pos.CENTER);
        loginForm.add(loginLabel, 0, 0);
        loginForm.add(usernameField, 0, 1);
        loginForm.add(passwordField, 0, 2);
        loginForm.add(feedbackLabel, 0, 3);
        loginForm.add(submitLoginButton, 0, 4);
        loginForm.add(backButton, 0, 5);

        VBox centeredLoginLayout = new VBox();
        centeredLoginLayout.setAlignment(Pos.CENTER);
        centeredLoginLayout.getChildren().add(loginForm);
        VBox.setVgrow(centeredLoginLayout, Priority.ALWAYS);

        mainLayout.getChildren().add(centeredLoginLayout);
    }

    private void showRegisterForm() {
        // Clear the previous content
        mainLayout.getChildren().clear();

        // Create registration form
        Label registerLabel = new Label("Register");
        registerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        registerLabel.setTextFill(Color.WHITE);

        TextField regUsernameField = new TextField();
        regUsernameField.setPromptText("Username");
        regUsernameField.setPrefWidth(250);

        TextField regEmailField = new TextField();  // Add email field
        regEmailField.setPromptText("Email");
        regEmailField.setPrefWidth(250);

        PasswordField regPasswordField = new PasswordField();
        regPasswordField.setPromptText("Password");
        regPasswordField.setPrefWidth(250);

        Label feedbackLabel = new Label();
        feedbackLabel.setTextFill(Color.RED);

        Button submitRegisterButton = createButton("Submit");
        submitRegisterButton.setOnAction(e -> {
            // Collect the username, email, and password from the input fields
            String username = regUsernameField.getText().trim();
            String email = regEmailField.getText().trim();  // Get email from the new field
            String password = regPasswordField.getText().trim();

            // Validate input fields
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                feedbackLabel.setText("Cannot leave fields empty.");
                return; // Stop further execution
            }

            // Create a User object
            User newUser = new User(username, password, email, null, null); // Pass email to the User object

            try {
                userDAO.registerUser(newUser); // Call the registerUser method with the User object
                System.out.println("Registered successfully!");
                resetToMainMenu(); // Reset to the main menu after registration
            } catch (SQLException | NoSuchAlgorithmException ex) {
                feedbackLabel.setText("Registration failed: " + ex.getMessage());
            }
        });


        Button backButton = createButton("Back");
        backButton.setOnAction(e -> resetToMainMenu());

        GridPane registerForm = new GridPane();
        registerForm.setVgap(10);
        registerForm.setHgap(10);
        registerForm.setPadding(new Insets(20));
        registerForm.setAlignment(Pos.CENTER);
        registerForm.add(registerLabel, 0, 0);
        registerForm.add(regUsernameField, 0, 1);
        registerForm.add(regEmailField, 0, 2); // Add email field to the form
        registerForm.add(regPasswordField, 0, 3); // Move password field to the next row
        registerForm.add(feedbackLabel, 0, 4);
        registerForm.add(submitRegisterButton, 0, 5);
        registerForm.add(backButton, 0, 6);

        VBox centeredRegisterLayout = new VBox();
        centeredRegisterLayout.setAlignment(Pos.CENTER);
        centeredRegisterLayout.getChildren().add(registerForm);
        VBox.setVgrow(centeredRegisterLayout, Priority.ALWAYS);

        mainLayout.getChildren().add(centeredRegisterLayout);
    }

    private void resetToMainMenu() {
        // Clear the previous content
        mainLayout.getChildren().clear();

        // Create the login button with its event handler
        Button loginButton = createButton("Login");
        loginButton.setOnAction(e -> showLoginForm());

        // Create the register button with its event handler
        Button registerButton = createButton("Register");
        registerButton.setOnAction(e -> showRegisterForm());

        // Re-add the existing title label and footer label
        mainLayout.getChildren().addAll(
                createTitleLabel(),
                loginButton,  // Newly created loginButton
                registerButton,  // Newly created registerButton
                createFooterLabel()
        );
    }


    private Label createTitleLabel() {
        Label titleLabel = new Label("COMMUNITY GARDEN PLANNER");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 50));
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setStyle("-fx-background-color: transparent; -fx-background-radius: 5; -fx-padding: 10;");
        return titleLabel;
    }

    private Button createButton(String text) {
        Button button = new Button(text);
        button.setMinWidth(150); // Set minimum width for consistency
        button.setStyle("-fx-background-color: #ff7f50; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");

        button.setOnMouseEntered(e -> {
            button.setStyle("-fx-background-color: #ff4f00; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12;");
        });
        button.setOnMouseExited(e -> {
            button.setStyle("-fx-background-color: #ff7f50; -fx-font-size: 18px; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10;");
        });

        return button;
    }

    private Label createFooterLabel() {
        Label footerLabel = new Label("Made by Aryan and Vansh");
        footerLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
        footerLabel.setTextFill(Color.WHITE);
        return footerLabel;
    }
}
