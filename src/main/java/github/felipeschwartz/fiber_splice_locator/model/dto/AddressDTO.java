package github.felipeschwartz.fiber_splice_locator.model.dto;

public class AddressDTO {

    private Long id;
    private String geoLocation;
    private String addressType;
    private String street;
    private String streetNumber;
    private String referencePoint;
    private String neighborhood;
    private String city;


    public AddressDTO() {
    }

    public AddressDTO(Long id, String geoLocation, String addressType, String street, String streetNumber, String referencePoint, String neighborhood, String city) {
        this.id = id;
        this.geoLocation = geoLocation;
        this.addressType = addressType;
        this.street = street;
        this.streetNumber = streetNumber;
        this.referencePoint = referencePoint;
        this.neighborhood = neighborhood;
        this.city = city;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(String geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
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

    public String getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(String referencePoint) {
        this.referencePoint = referencePoint;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
