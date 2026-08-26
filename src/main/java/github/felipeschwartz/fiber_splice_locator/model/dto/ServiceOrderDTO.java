package github.felipeschwartz.fiber_splice_locator.model.dto;

import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderStatusDescription;
import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ServiceOrderDTO extends RepresentationModel<ServiceOrderDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long serviceOrderId;
    private CEODTO ceo;
    private ServiceOrderStatus status;
    private UserDTO user;
    private Set<ServiceOrderPhotoDTO> serviceOrderPhotos = new HashSet<>();
    private Set<ServiceOrderStatusDescription> serviceOrderStatusDescriptions = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ServiceOrderDTO() {
    }

    public ServiceOrderDTO(Long serviceOrderId, CEODTO ceo, ServiceOrderStatus status, UserDTO user, Set<ServiceOrderPhotoDTO> serviceOrderPhotos, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.serviceOrderId = serviceOrderId;
        this.ceo = ceo;
        this.status = status;
        this.user = user;
        this.serviceOrderPhotos = serviceOrderPhotos != null ? serviceOrderPhotos : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public CEODTO getCeo() {
        return ceo;
    }

    public void setCeo(CEODTO ceo) {
        this.ceo = ceo;
    }

    public ServiceOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceOrderStatus status) {
        this.status = status;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public Set<ServiceOrderPhotoDTO> getServiceOrderPhotos() {
        return serviceOrderPhotos;
    }

    public void setServiceOrderPhotos(Set<ServiceOrderPhotoDTO> serviceOrderPhotos) {
        this.serviceOrderPhotos = serviceOrderPhotos;
    }

    public Set<ServiceOrderStatusDescription> getServiceOrderStatusDescriptions() {
        return serviceOrderStatusDescriptions;
    }

    public void setServiceOrderStatusDescriptions(Set<ServiceOrderStatusDescription> serviceOrderStatusDescriptions) {
        this.serviceOrderStatusDescriptions = serviceOrderStatusDescriptions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ServiceOrderDTO that = (ServiceOrderDTO) o;
        return Objects.equals(getServiceOrderId(), that.getServiceOrderId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOrderId());
    }
}