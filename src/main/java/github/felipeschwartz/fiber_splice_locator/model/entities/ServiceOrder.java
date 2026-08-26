package github.felipeschwartz.fiber_splice_locator.model.entities;

import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "service_orders")
public class ServiceOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long serviceOrderId;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(
            name = "ceo_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_service_order_ceo")
    )
    private CEO ceo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceOrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_service_order_user")
    )
    private User user;

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ServiceOrderPhoto> serviceOrderPhotos = new HashSet<>();

    @OneToMany(mappedBy = "serviceOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ServiceOrderStatusDescription> serviceOrderStatusDescriptions = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ServiceOrder() {
    }

    public ServiceOrder(Long serviceOrderId, CEO ceo, ServiceOrderStatus status, User user, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.serviceOrderId = serviceOrderId;
        this.ceo = ceo;
        this.status = status;
        this.user = user;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getServiceOrderId() {
        return serviceOrderId;
    }

    public void setServiceOrderId(Long serviceOrderId) {
        this.serviceOrderId = serviceOrderId;
    }

    public CEO getCeo() {
        return ceo;
    }

    public void setCeo(CEO ceo) {
        this.ceo = ceo;
    }

    public ServiceOrderStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceOrderStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<ServiceOrderPhoto> getServiceOrderPhotos() {
        return serviceOrderPhotos;
    }

    public void setServiceOrderPhotos(Set<ServiceOrderPhoto> serviceOrderPhotos) {
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
        ServiceOrder that = (ServiceOrder) o;
        return Objects.equals(getServiceOrderId(), that.getServiceOrderId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getServiceOrderId());
    }
}