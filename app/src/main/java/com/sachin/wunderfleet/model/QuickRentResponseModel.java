package com.sachin.wunderfleet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class QuickRentResponseModel {

    @SerializedName("reservationId")
    @Expose
    private Integer reservationId;
    @SerializedName("carId")
    @Expose
    private Integer carId;
    @SerializedName("cost")
    @Expose
    private Integer cost;
    @SerializedName("drivenDistance")
    @Expose
    private Integer drivenDistance;
    @SerializedName("licencePlate")
    @Expose
    private String licencePlate;
    @SerializedName("startAddress")
    @Expose
    private String startAddress;
    @SerializedName("userId")
    @Expose
    private Integer userId;
    @SerializedName("isParkModeEnabled")
    @Expose
    private Boolean isParkModeEnabled;
    @SerializedName("damageDescription")
    @Expose
    private String damageDescription;
    @SerializedName("fuelCardPin")
    @Expose
    private String fuelCardPin;
    @SerializedName("endTime")
    @Expose
    private Integer endTime;
    @SerializedName("startTime")
    @Expose
    private Integer startTime;

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public Integer getDrivenDistance() {
        return drivenDistance;
    }

    public void setDrivenDistance(Integer drivenDistance) {
        this.drivenDistance = drivenDistance;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public void setLicencePlate(String licencePlate) {
        this.licencePlate = licencePlate;
    }

    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getIsParkModeEnabled() {
        return isParkModeEnabled;
    }

    public void setIsParkModeEnabled(Boolean isParkModeEnabled) {
        this.isParkModeEnabled = isParkModeEnabled;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public void setDamageDescription(String damageDescription) {
        this.damageDescription = damageDescription;
    }

    public String getFuelCardPin() {
        return fuelCardPin;
    }

    public void setFuelCardPin(String fuelCardPin) {
        this.fuelCardPin = fuelCardPin;
    }

    public Integer getEndTime() {
        return endTime;
    }

    public void setEndTime(Integer endTime) {
        this.endTime = endTime;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

}