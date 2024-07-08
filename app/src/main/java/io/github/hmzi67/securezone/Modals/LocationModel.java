package io.github.hmzi67.securezone.Modals;

public class LocationModel {
    private double latitude;
    private double longitude;
    private String address;

    public LocationModel() {
    }

    public LocationModel(double latitude, double longitude, String address) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }
}
