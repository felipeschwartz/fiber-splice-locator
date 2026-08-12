package github.felipeschwartz.fiber_splice_locator.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Address {

    @Column
    private String street;

    @Column
    private String streetNumber;

    @Column
    private String complement;

    @Column
    private String city;

    @Column
    private String state;

    @Column
    private String zip;

    @Column
    private String country;


    public Address() {
    }

    public Address(String street, String streetNumber, String complement, String city, String state, String zip, String country) {
        this.street = street;
        this.streetNumber = streetNumber;
        this.complement = complement;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.country = country;
    }


    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
