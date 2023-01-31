package com.gmail.dimabah;

import java.util.Objects;
import java.util.SimpleTimeZone;
import javax.persistence.*;
@Entity
public class Apartment {
    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String city;
    private String district;
    @Column(nullable = false)
    private String address;
    @Column(nullable = false)
    private double area;
    @Column(nullable = false)
    private int room;
    @Column(nullable = false)
    private double price;

    public Apartment(String city, String district, String address, double area, int room, double price) {
        this.city = city;
        this.district = district;
        this.address = address;
        this.area = area;
        this.room = room;
        this.price = price;
    }

    public Apartment() {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getArea() {
        return area;
    }

    public void setArea(double area) {
        this.area = area;
    }

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Apartment{" +
                "city='" + city + '\'' +
                ", district='" + district + '\'' +
                ", address='" + address + '\'' +
                ", area=" + area +
                ", room=" + room +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Apartment apartment = (Apartment) o;
        return Double.compare(apartment.area, area) == 0 &&
                room == apartment.room &&
                Double.compare(apartment.price, price) == 0 &&
                Objects.equals(city, apartment.city) &&
                Objects.equals(district, apartment.district) &&
                Objects.equals(address, apartment.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, district, address, area, room, price);
    }
    public String getFormattedObject(){
        return String.format("%-6d | %-25s | %-25s | %-40s | %-15.1f | %-6d | %-10.2f",
                id,city,district,address,area,room,price);
    }
    public String getHeader(){
        return String.format("%-6s | %-25s | %-25s | %-40s | %-15s | %-6s | %-10s",
                "id","city/town/village","district","address","area","rooms","price");
    }
}
