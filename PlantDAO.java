package backend;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PlantDAO {

    // Method to fetch all plants
    public List<Plant> PlantsByRegion(String region) throws SQLException {
        List<Plant> plants = new ArrayList<>();
        String query = "SELECT * FROM plants"; // Fetch all plants

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String type = resultSet.getString("type");
                region = resultSet.getString("region");
                String careInstructions = resultSet.getString("care_instructions");

                // Create a Plant object and add it to the list
                Plant plant = new Plant(id, name, type, region, careInstructions);
                plants.add(plant);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching plants: " + e.getMessage());
            throw e; // Rethrow the exception for further handling if necessary
        }

        return plants; // Return the list of plants
    }

    // Method to fetch distinct regions
    public List<String> fetchRegions() throws SQLException {
        List<String> regions = new ArrayList<>();
        String query = "SELECT DISTINCT region FROM plants"; // Fetch unique regions

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String region = resultSet.getString("region");
                regions.add(region); // Add the region to the list
            }
        } catch (SQLException e) {
            System.out.println("Error fetching regions: " + e.getMessage());
            throw e; // Rethrow the exception for further handling if necessary
        }

        return regions; // Return the list of regions
    }

    // Method to get plants by region
    public List<String> fetchPlantsByRegion(String region) {
        List<String> plants = new ArrayList<>();
        String query = "SELECT name FROM plants WHERE region = ?"; // Adjust your query based on your database schema

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, region);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                plants.add(resultSet.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching plants by region: " + e.getMessage());
        }
        return plants;
    }

    // Method to get care instructions for a specific plant
    public String getCareInstructions(String plantName) throws SQLException {
        String careInstructions = null;
        String query = "SELECT care_instructions FROM plants WHERE name = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, plantName);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                careInstructions = resultSet.getString("care_instructions");
            }
        } catch (SQLException e) {
            System.out.println("Error fetching care instructions: " + e.getMessage());
            throw e; // Rethrow the exception for further handling if necessary
        }
        return careInstructions;
    }
}
