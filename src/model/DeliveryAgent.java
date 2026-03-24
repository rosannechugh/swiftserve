package model;

public class DeliveryAgent {
	private String name;
    private boolean isAvailable;

    public DeliveryAgent(String name) {
        this.name = name;
        this.isAvailable = true;
    }

    public String getName() { return name; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean status) {
        this.isAvailable = status;
    }

}
