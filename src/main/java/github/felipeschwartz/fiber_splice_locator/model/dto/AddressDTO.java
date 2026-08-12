package github.felipeschwartz.fiber_splice_locator.model.dto;

public class AddressDTO {

    private String street;
    private String streetNumber;
    private String complement;
    private String city;
    private String state;
    private String zip;
    private String country;


    public AddressDTO() {
    }

    public AddressDTO(String street, String streetNumber, String complement, String city, String state, String zip, String country) {
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
