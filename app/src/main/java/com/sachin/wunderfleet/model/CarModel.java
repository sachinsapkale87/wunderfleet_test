package com.sachin.wunderfleet.model;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarModel {


    @SerializedName("carId")
    @Expose
    private Integer carId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("lat")
    @Expose
    private Double lat;
    @SerializedName("lon")
    @Expose
    private Double lon;
    @SerializedName("licencePlate")
    @Expose
    private String licencePlate;
    @SerializedName("fuelLevel")
    @Expose
    private Integer fuelLevel;
    @SerializedName("vehicleStateId")
    @Expose
    private Integer vehicleStateId;
    @SerializedName("vehicleTypeId")
    @Expose
    private Integer vehicleTypeId;
    @SerializedName("pricingTime")
    @Expose
    private String pricingTime;
    @SerializedName("pricingParking")
    @Expose
    private String pricingParking;
    @SerializedName("reservationState")
    @Expose
    private Integer reservationState;
    @SerializedName("isClean")
    @Expose
    private Boolean isClean;
    @SerializedName("isDamaged")
    @Expose
    private Boolean isDamaged;
    @SerializedName("distance")
    @Expose
    private String distance;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("zipCode")
    @Expose
    private String zipCode;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("locationId")
    @Expose
    private Integer locationId;

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getTitle() {
        if(TextUtils.isEmpty(title)){
            title = "No name available";
        }
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public Integer getFuelLevel() {
        return fuelLevel;
    }

    public void setFuelLevel(Integer fuelLevel) {
        this.fuelLevel = fuelLevel;
    }

    public Integer getVehicleStateId() {
        return vehicleStateId;
    }

    public void setVehicleStateId(Integer vehicleStateId) {
        this.vehicleStateId = vehicleStateId;
    }

    public Integer getVehicleTypeId() {
        return vehicleTypeId;
    }

    public void setVehicleTypeId(Integer vehicleTypeId) {
        this.vehicleTypeId = vehicleTypeId;
    }

    public String getPricingTime() {
        return pricingTime;
    }

    public void setPricingTime(String pricingTime) {
        this.pricingTime = pricingTime;
    }

    public String getPricingParking() {
        return pricingParking;
    }

    public void setPricingParking(String pricingParking) {
        this.pricingParking = pricingParking;
    }

    public Integer getReservationState() {
        return reservationState;
    }

    public void setReservationState(Integer reservationState) {
        this.reservationState = reservationState;
    }

    public Boolean getIsClean() {
        return isClean;
    }

    public void setIsClean(Boolean isClean) {
        this.isClean = isClean;
    }

    public Boolean getIsDamaged() {
        return isDamaged;
    }

    public void setIsDamaged(Boolean isDamaged) {
        this.isDamaged = isDamaged;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Integer getLocationId() {
        return locationId;
    }

    public void setLocationId(Integer locationId) {
        this.locationId = locationId;
    }


}
