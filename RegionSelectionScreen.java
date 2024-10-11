package frontend;

import backend.UserDAO;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.scene.paint.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class RegionSelectionScreen {
    private VBox mainLayout; // Declare the main layout variable
    private String username; // Declare username variable
    private Color[] colors = {Color.web("#FF5733"), Color.web("#33FF57"), Color.web("#3357FF")}; // Example colors
    private int gradientIndex = 0; // Index to keep track of the gradient colors
    private Map<String, List<String>> regionPlants; // Map to store plants by region

    // Constructor
    public RegionSelectionScreen(VBox layout, String username) {
        this.mainLayout = layout; // Initialize mainLayout
        this.username = username;  // Initialize username
        initializePlantData(); // Initialize the plant data
        showRegionSelection(); // Call the method to show the region selection
    }

    private void initializePlantData() {
        // Setup plants for each region
        regionPlants = new HashMap<>();

        regionPlants.put("North India", Arrays.asList(
                "Rose", "Wheat", "Mango", "Carrot", "Cauliflower", "Broccoli",
                "Peas", "Pomegranate", "Cherry", "Corn", "Sunflower", "Turnip", "Mustard"));

        regionPlants.put("East India", Arrays.asList(
                "Banana", "Coconut", "Rice", "Lettuce", "Tomato", "Onion",
                "Pineapple", "Jackfruit", "Tea", "Papaya", "Ginger", "Jute", "Taro"));

        regionPlants.put("West India", Arrays.asList(
                "Cactus", "Bajra", "Grapes", "Spinach", "Chili", "Bell Pepper",
                "Coriander", "Groundnut", "Sesame", "Watermelon", "Guar", "Pomegranate", "Fenugreek"));

        regionPlants.put("South India", Arrays.asList(
                "Curry Leaf", "Tamarind", "Sugarcane", "Potato", "Radish", "Guava",
                "Lemon", "Turmeric", "Black Pepper", "Coffee", "Cashew", "Cocoa", "Drumstick (Moringa)"));
    }


    private void showRegionSelection() {
        // Clear previous content
        mainLayout.getChildren().clear();

        // Create a Label for the title
        Label titleLabel = new Label("SELECT REGION");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: black;"); // Style the label

        // Create a GridPane for layout
        GridPane regionGrid = new GridPane();
        regionGrid.setVgap(10);
        regionGrid.setHgap(10);
        regionGrid.setPadding(new Insets(20));
        regionGrid.setAlignment(Pos.CENTER);

        // Create buttons for each region with hover effects
        addRegionButton(regionGrid, "North India", "C:/Users/aryan/BAGICHA/resources/north.png", 0, 0);
        addRegionButton(regionGrid, "East India", "C:/Users/aryan/BAGICHA/resources/east.png", 1, 0);
        addRegionButton(regionGrid, "West India", "C:/Users/aryan/BAGICHA/resources/west.png", 0, 1);
        addRegionButton(regionGrid, "South India", "C:/Users/aryan/BAGICHA/resources/south.png", 1, 1);

        // Add the title label and the region grid to the main layout
        mainLayout.getChildren().addAll(titleLabel, regionGrid);
        mainLayout.setAlignment(Pos.CENTER); // Center align the main layout
        mainLayout.setPadding(new Insets(20)); // Add padding around the layout

        // Start gradient animation for the background color
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2.5), e -> {
            gradientIndex = (gradientIndex + 1) % colors.length;
            Color nextColor = colors[gradientIndex];
            Color currentColor = colors[(gradientIndex - 1 + colors.length) % colors.length];
            mainLayout.setStyle(String.format("-fx-background-color: linear-gradient(to right, %s, %s);",
                    currentColor.toString().replace("0x", "#"),
                    nextColor.toString().replace("0x", "#")));
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play(); // Start the animation
    }

    private void addRegionButton(GridPane grid, String regionName, String imagePath, int col, int row) {
        // Create a VBox for the region button
        VBox regionBox = new VBox();

        // Create a label for the region name with bold font
        Label regionLabel = new Label(regionName);
        regionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Create a button with an image
        Button regionButton = new Button();
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(new FileInputStream(imagePath))); // Load the image
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView.setFitWidth(150); // Adjust image size
        imageView.setFitHeight(150);
        regionButton.setGraphic(imageView);
        regionButton.setPrefSize(150, 150);

        // Add hover effects
        ColorAdjust colorAdjust = new ColorAdjust();
        regionButton.setOnMouseEntered(e -> {
            colorAdjust.setBrightness(0.2); // Brighten on hover
            regionButton.setEffect(colorAdjust);
        });
        regionButton.setOnMouseExited(e -> {
            colorAdjust.setBrightness(0); // Reset brightness
            regionButton.setEffect(null);
        });

        // Add action to the button
        regionButton.setOnAction(e -> showPlantScreen(regionName));

        // Add elements to the VBox
        regionBox.getChildren().addAll(regionLabel, regionButton);
        regionBox.setAlignment(Pos.CENTER); // Center align elements in VBox

        // Add the VBox to the grid
        grid.add(regionBox, col, row);
        grid.setAlignment(Pos.CENTER); // Center align the grid
    }

    private void showPlantScreen(String region) {
        updateDatabase(region);

        // Get the list of plants for the selected region
        List<String> plants = regionPlants.get(region);

        // Create an instance of PlantSelectionScreen and call the show method
        PlantSelectionScreen plantSelectionScreen = new PlantSelectionScreen(username, plants);
        Stage stage = (Stage) mainLayout.getScene().getWindow(); // Get the current stage
        plantSelectionScreen.show(stage); // Display the plant selection screen in the same window
    }





    private void updateDatabase(String selectedRegion) {
        // Update the user's selected region in the database
        String sql = "UPDATE users SET region = ? WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/community_garden_planner", "root", "minecraft@OP1");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, selectedRegion);
            preparedStatement.setString(2, username); // Assuming 'username' is the logged-in user
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Region updated successfully: " + selectedRegion);
            } else {
                System.out.println("Region update failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updatePlantSelectionInDatabase(String plantName) throws SQLException {
        // Update the user's plant selection in the database
        String sql = "INSERT INTO plant_selection (username, plant_name) " +
                "SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM plant_selection WHERE username = ? AND plant_name = ?)";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/community_garden_planner", "root", "minecraft@OP1");
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, plantName);
            preparedStatement.setString(3, username);
            preparedStatement.setString(4, plantName);
            preparedStatement.executeUpdate(); // Execute the insert statement
            System.out.println("Plant selected: " + plantName);
        }
    }
}
