package backend;

public class Plant {
    private int id;
    private String name;
    private String type;
    private String region;
    private String careInstructions;

    // Constructor
    public Plant(int id, String name, String type, String region, String careInstructions) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.region = region;
        this.careInstructions = careInstructions;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getRegion() {
        return region;
    }

    public String getCareInstructions() {
        return careInstructions;
    }
}
