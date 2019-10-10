package com.example.algorismproperties;

//Property class that defines all the keys used in the database
public class Property {
    public String name, address, price, bed, bath, parking, year, description, download, id;
    public Property() {

    }
    public Property(String name, String address, String price, String bed, String bath, String parking, String year, String propDescription, String download, String id) {
        this.name = name;
        this.address = address;
        this.price = price;
        this.bed = bed;
        this.bath = bath;
        this.parking = parking;
        this.year = year;
        this.description = propDescription;
        this.download = download;
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getPrice() {
        return price;
    }
    public String getBed() {
        return bed;
    }
    public String getBath() {
        return bath;
    }
    public String getParking() {
        return parking;
    }
    public String getYear() {
        return year;
    }

    public String getDescription() {
        return description;
    }
    public String getDownload() {
        return download;
    }

    public String getId() {
        return id;
    }
}
