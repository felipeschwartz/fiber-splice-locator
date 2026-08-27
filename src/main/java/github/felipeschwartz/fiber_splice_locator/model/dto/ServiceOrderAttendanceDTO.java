package github.felipeschwartz.fiber_splice_locator.model.dto;

import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;

public class ServiceOrderAttendanceDTO {
    private ServiceOrderStatus status;
    private String statusDescription;
    private String geoLocation;

    public ServiceOrderStatus getStatus() { return status; }
    public void setStatus(ServiceOrderStatus status) { this.status = status; }
    public String getStatusDescription() { return statusDescription; }
    public void setStatusDescription(String statusDescription) { this.statusDescription = statusDescription; }
    public String getGeoLocation() { return geoLocation; }
    public void setGeoLocation(String geoLocation) { this.geoLocation = geoLocation; }
}
