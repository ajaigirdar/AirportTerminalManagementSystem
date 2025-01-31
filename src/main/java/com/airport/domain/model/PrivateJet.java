package com.airport.domain.model;

public class PrivateJet extends Aircraft{

    private boolean hasLuxuryService;
    private int maxSpeed;

    public PrivateJet(String model, int capacity, double fuelCapacity, boolean hasLuxuryService, int maxSpeed) {
        super(model, capacity, fuelCapacity);
        this.hasLuxuryService = hasLuxuryService;
        this.maxSpeed = maxSpeed;
    }

    public boolean isHasLuxuryService() {
        return hasLuxuryService;
    }

    public void setHasLuxuryService(boolean hasLuxuryService) {
        this.hasLuxuryService = hasLuxuryService;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public String toString() {
        return super.toString() +
                ", Luxury Service: " + hasLuxuryService +
                ", Max Speed: " + maxSpeed;
    }
}
