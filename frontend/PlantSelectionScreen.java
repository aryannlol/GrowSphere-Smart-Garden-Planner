package frontend;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class PlantSelectionScreen {
    private String username;
    private List<String> plants;
    private VBox mainLayout;
    private Stage stage;

    public PlantSelectionScreen(String username, List<String> plants, Stage stage) {
        this.username = username;
        this.plants = plants;
        this.stage = stage;
    }

    public void show(Stage stage) {
        mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Select Plants for Your Region");
        titleLabel.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 50));
        mainLayout.getChildren().add(titleLabel);

        for (String plant : plants) {
            Button plantButton = new Button(plant);
            plantButton.setPrefWidth(350);
            plantButton.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 28));

            String imagePath = "C:/Users/aryan/BAGICHA/resources/images/" + plant.toLowerCase().replace(" ", "_") + ".png";
            ImageView imageView = new ImageView();
            try {
                imageView.setImage(new Image(new FileInputStream(imagePath)));
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                plantButton.setGraphic(imageView);
            } catch (FileNotFoundException e) {
                System.out.println("Image not found: " + imagePath);
            }

            plantButton.setOnAction(e -> showConfirmationDialog(plant));
            plantButton.setOnMouseEntered(event -> plantButton.setStyle("-fx-background-color: lightblue;"));
            plantButton.setOnMouseExited(event -> plantButton.setStyle("-fx-background-color: white;"));
            mainLayout.getChildren().add(plantButton);
        }

        // Add the back button
        Button backButton = new Button("Back");
        backButton.setPrefWidth(150);
        backButton.setFont(javafx.scene.text.Font.font("Arial", javafx.scene.text.FontWeight.BOLD, 28));
        backButton.setOnAction(e -> navigateBack());

        mainLayout.getChildren().add(backButton);

        ScrollPane scrollPane = new ScrollPane(mainLayout);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);
        addBackgroundGradientAnimation();

        Scene scene = new Scene(scrollPane, 400, 500);
        this.stage.setScene(scene);
        this.stage.setTitle("Plant Selection");
        this.stage.show();
    }

    private void addBackgroundGradientAnimation() {
        Color[] colors = new Color[]{
                Color.web("#ff7f50"),
                Color.web("#6a5acd"),
                Color.web("#3cb371"),
                Color.web("#ff4500")
        };

        int[] gradientIndex = {0};
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

    private void showConfirmationDialog(String plantName) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirm Selection");
        alert.setHeaderText("Are you sure you want to select " + plantName + "?");
        alert.setContentText("Click 'Yes' to confirm.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updatePlantSelectionInDatabase(plantName);
        }
    }

    private void updatePlantSelectionInDatabase(String plantName) {
        String url = "jdbc:mysql://localhost:3306/community_garden_planner";
        String user = "";
        String password = "";

        String updateQuery = "UPDATE users SET selected_plants = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, plantName);
            pstmt.setString(2, this.username);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                showPlantCareInstructions(plantName);
            } else {
                showErrorDialog("Error", "Failed to update plant selection.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorDialog("Database Error", "Could not update the plant selection. Please try again.");
        }
    }

    private void showPlantCareInstructions(String plantName) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Plant Care Instructions");
        alert.setHeaderText("Care Instructions for " + plantName);
        alert.setContentText("Water every few days, especially during flowering. Provide regular pruning to maintain shape.");
        alert.showAndWait();
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateBack() {
        // Create a new instance of RegionSelectionScreen
        RegionSelectionScreen regionSelectionScreen = new RegionSelectionScreen(new VBox(), username);

        // Clear the current scene
        Scene scene = new Scene(regionSelectionScreen.mainLayout, 400, 500);
        stage.setScene(scene); // Set the new scene with region selection layout
        stage.setTitle("Region Selection"); // Set the title for the new scene
        regionSelectionScreen.showRegionSelection(); // Show the region selection
    }

}
