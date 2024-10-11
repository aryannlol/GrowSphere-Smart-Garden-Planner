package backend;

public class User {
    private String username;
    private String password;
    private String email;
    private String region;          // New field for region
    private String selectedPlants;  // New field for selected plants

    // Constructor for registration
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // Constructor for retrieving user with region and selected plants
    public User(String username, String password, String email, String region, String selectedPlants) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.region = region;           // Initialize region
        this.selectedPlants = selectedPlants; // Initialize selected plants
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    // Getters for new fields
    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;  // Setter for region
    }

    public String getSelectedPlants() {
        return selectedPlants;
    }

    public void setSelectedPlants(String selectedPlants) {
        this.selectedPlants = selectedPlants; // Setter for selected plants
    }
}
