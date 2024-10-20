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
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.effect.ColorAdjust;
import javafx.util.Duration;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert; // Import Alert class
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

public class RegionSelectionScreen {
    public VBox mainLayout; // Declare the main layout variable
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
                "Lemon", "Turmeric", "Black Pepper", "Coffee", "Cashew", "Cocoa"));
    }

    public void showRegionSelection() {
        // Clear previous content
        mainLayout.getChildren().clear();

        // Create a Label for the title
        Label titleLabel = new Label("SELECT  REGION");
        titleLabel.setStyle("-fx-font-size: 50px; -fx-font-weight: bold; -fx-text-fill: black;");

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

        // Create an ImageView for the weather icon
        ImageView weatherIcon = new ImageView();
        try {
            weatherIcon.setImage(new Image(new FileInputStream("C:/Users/aryan/BAGICHA/resources/images/weather.png"))); // Update this path
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Weather image not found."); // Debug message
        }
        weatherIcon.setFitWidth(100); // Adjust size as needed
        weatherIcon.setFitHeight(100);

        // Add a click event to show the weather message
        weatherIcon.setOnMouseClicked(e -> showWeatherMessage("Today's weather: Sunny, 28°C")); // Update message as needed

        // Create a StackPane to layer the weather icon on top of the main layout
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(regionGrid); // Add the region grid

        // Position the weather icon in the corner of the main layout
        StackPane.setAlignment(weatherIcon, Pos.TOP_RIGHT);

        // Create an HBox to add some margin to the weather icon
        HBox weatherContainer = new HBox(weatherIcon);
        weatherContainer.setAlignment(Pos.TOP_RIGHT);
        HBox.setMargin(weatherIcon, new Insets(10)); // Set margin for the weather icon

        // Add the title label, stack pane, and weather container to the main layout
        mainLayout.getChildren().addAll(titleLabel, stackPane, weatherContainer);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        // Add the background gradient animation
        addBackgroundGradientAnimation();
    }

    private void addBackgroundGradientAnimation() {
        // Define an array of colors for the gradient animation
        Color[] colors = new Color[]{
                Color.web("#ff7f50"), // Coral
                Color.web("#6a5acd"), // SlateBlue
                Color.web("#3cb371"), // MediumSeaGreen
                Color.web("#ff4500")  // OrangeRed
        };

        // Create an array to hold the current gradient index
        int[] gradientIndex = {0};

        // Create a Timeline for the gradient animation
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), e -> {
            // Get the next color in the gradient
            Color nextColor = colors[gradientIndex[0] % colors.length];
            Color currentColor = colors[(gradientIndex[0] - 1 + colors.length) % colors.length];

            // Set the background style with a linear gradient
            mainLayout.setStyle(String.format("-fx-background-color: linear-gradient(to right, %s, %s);",
                    currentColor.toString().replace("0x", "#"),
                    nextColor.toString().replace("0x", "#")));

            // Increment the gradient index
            gradientIndex[0]++;
        }));

        // Set the timeline to repeat indefinitely
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play(); // Start the animation
    }



    private void showWeatherMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Weather Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    private void addRegionButton(GridPane grid, String regionName, String imagePath, int col, int row) {
        // Create a VBox for the region button
        VBox regionBox = new VBox();

        // Create a label for the region name
        Label regionLabel = new Label(regionName);
        regionLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: black;");

        // Create a button with an image
        Button regionButton = new Button();
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(new FileInputStream(imagePath))); // Load the image
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        imageView.setFitWidth(168); // Adjust image size
        imageView.setFitHeight(168);
        regionButton.setGraphic(imageView);
        regionButton.setPrefSize(160, 160);

        // Add action to the button
        regionButton.setOnAction(e -> {
            System.out.println(regionName + " button clicked!"); // Debug print statement
            showPlantScreen(regionName);
        });

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
        Stage stage = (Stage) mainLayout.getScene().getWindow(); // Get the current stage
        PlantSelectionScreen plantSelectionScreen = new PlantSelectionScreen(username, plants, stage); // Pass the stage
        plantSelectionScreen.show(stage); // Display the plant selection screen in the same window
    }

    private void updateDatabase(String selectedRegion) {
        // Update the user's selected region in the database
        String sql = "UPDATE users SET region = ? WHERE username = ?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/community_garden_planner", "", "");
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, selectedRegion);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Handle SQL exceptions
        }
    }
}
