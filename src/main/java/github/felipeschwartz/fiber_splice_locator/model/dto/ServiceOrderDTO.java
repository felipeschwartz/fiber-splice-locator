package github.felipeschwartz.fiber_splice_locator.model.dto;

import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ServiceOrderDTO extends RepresentationModel<ServiceOrderDTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private CEODTO CEODTO;
    private ServiceOrderStatus status;
    private UserDTO user;
    private Set<ServiceOrderPhotoDTO> serviceOrderPhotoDTOS = new HashSet<>();
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ServiceOrderDTO() {
    }

    public ServiceOrderDTO(Long id, CEODTO CEODTO, ServiceOrderStatus status, UserDTO user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.CEODTO = CEODTO;
        this.status = status;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CEODTO getCeo() {
        return CEODTO;
    }

    public void setCeo(CEODTO CEODTO) {
        this.CEODTO = CEODTO;
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
        return serviceOrderPhotoDTOS;
    }

    public void setServiceOrderPhotos(Set<ServiceOrderPhotoDTO> serviceOrderPhotoDTOS) {
        this.serviceOrderPhotoDTOS = serviceOrderPhotoDTOS;
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
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
