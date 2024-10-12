package frontend;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PlantSelectionScreen {
    private String username;
    private List<String> plants; // List of plants for selection
    private VBox mainLayout; // Declare the layout as a class variable

    // Constructor
    public PlantSelectionScreen(String username, List<String> plants) {
        this.username = username;
        this.plants = plants; // Initialize plants
    }

    public void show(Stage stage) {
        mainLayout = new VBox(20); // Create a VBox layout with more spacing for vertical alignment
        mainLayout.setAlignment(Pos.CENTER); // Center align the layout

        // Create a label for the title
        Label titleLabel = new Label("Select Plants for Your Region");
        titleLabel.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 40)); // Set font size and bold
        mainLayout.getChildren().add(titleLabel);

        // Create buttons for each plant, now in a vertical arrangement
        for (String plant : plants) {
            Button plantButton = new Button(plant);
            plantButton.setPrefWidth(350); // Increase the button width
            plantButton.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 28)); // Bold font for buttons

            // Action for the button
            plantButton.setOnAction(e -> showConfirmationDialog(plant));

            // Add hover effect
            plantButton.setOnMouseEntered(event -> plantButton.setStyle("-fx-background-color: lightblue;"));
            plantButton.setOnMouseExited(event -> plantButton.setStyle("-fx-background-color: white;"));

            mainLayout.getChildren().add(plantButton); // Add buttons to the VBox directly
        }

        // Wrap the main layout in a ScrollPane to make it scrollable
        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true); // Ensure the scrollpane fits the width of the window
        scrollPane.setPannable(true); // Allow scrolling by dragging with the mouse

        // Add background animation
        addBackgroundGradientAnimation();

        // Set the scene and show the stage
        Scene scene = new Scene(scrollPane, 400, 500); // Adjust height for vertical layout
        stage.setScene(scene);
        stage.setTitle("Plant Selection");
        stage.show();
    }

    private void addBackgroundGradientAnimation() {
        Color[] colors = new Color[]{
                Color.web("#ff7f50"), // Coral
                Color.web("#6a5acd"), // Slate Blue
                Color.web("#3cb371"), // Medium Sea Green
                Color.web("#ff4500")  // Orange Red
        };

        int[] gradientIndex = {0}; // To keep track of the current color index

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            Color nextColor = colors[gradientIndex[0] % colors.length];
            Color currentColor = colors[(gradientIndex[0] - 1 + colors.length) % colors.length];
            mainLayout.setStyle(String.format("-fx-background-color: linear-gradient(to right, %s, %s);",
                    currentColor.toString().replace("0x", "#"),
                    nextColor.toString().replace("0x", "#")));
            gradientIndex[0]++;
        }));

        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // Confirmation dialog when a plant is clicked
    private void showConfirmationDialog(String plantName) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Selection");
        alert.setHeaderText("Are you sure you want to select " + plantName + "?");
        alert.setContentText("Click 'Yes' to confirm.");

        // Display the dialog and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed, update the plant selection in the database
            updatePlantSelectionInDatabase(plantName);
        }
    }

    // Update the plant selection in the database
    private void updatePlantSelectionInDatabase(String plantName) {
        String url = "jdbc:mysql://localhost:3306/"; // Update the database URL if needed
        String user = ""; // Your database username
        String password = ""; // Your database password

        // Update to use the correct column name
        String updateQuery = "UPDATE users SET selected_plants = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, plantName); // Set the plant name
            pstmt.setString(2, this.username); // Set the username

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                showPlantCareInstructions(plantName); // Show plant care instructions after successful update
            } else {
                showErrorDialog("Error", "Failed to update plant selection.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "Could not update the plant selection. Please try again.");
        }
    }
    

    // Display plant care instructions after the plant is selected
    private void showPlantCareInstructions(String plantName) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Plant Care Instructions");
        alert.setHeaderText("Care Instructions for " + plantName);
        alert.setContentText("Here are some care tips for " + plantName + "...");
        alert.showAndWait();
    }

    // Show error dialog in case of issues
    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
