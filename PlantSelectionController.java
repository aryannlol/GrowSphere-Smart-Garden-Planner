package frontend;

import backend.PlantDAO;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.sql.SQLException;
import java.util.List;

public class PlantSelectionController {

    @FXML
    private ComboBox<String> regionComboBox;
    @FXML
    private ComboBox<String> plantComboBox;
    @FXML
    private Label instructionsLabel;

    @FXML
    public void initialize() {
        // Populate regions from the database
        try {
            PlantDAO plantDAO = new PlantDAO();
            List<String> regions = plantDAO.fetchRegions();
            regionComboBox.getItems().addAll(regions);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void fetchPlants() {
        String selectedRegion = regionComboBox.getValue();
        if (selectedRegion != null) {
            PlantDAO plantDAO = new PlantDAO();
            List<String> plants = plantDAO.fetchPlantsByRegion(selectedRegion);
            plantComboBox.getItems().clear();
            plantComboBox.getItems().addAll(plants);
        }
    }
}
