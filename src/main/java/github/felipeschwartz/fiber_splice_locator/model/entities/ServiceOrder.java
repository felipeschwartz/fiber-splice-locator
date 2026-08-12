package github.felipeschwartz.fiber_splice_locator.model.entities;

import github.felipeschwartz.fiber_splice_locator.model.enums.ServiceOrderStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_orders")
public class ServiceOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "service_order_photo_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_service_order_photo")
    )
    private Set<ServiceOrderPhoto> serviceOrderPhoto = new HashSet<>();
}
