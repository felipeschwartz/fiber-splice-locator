package github.felipeschwartz.fiber_splice_locator.model.entities;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 8)
    private String token;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean used;

    public PasswordResetToken() {
    }

    public PasswordResetToken(String token, User user, Instant expiresAt, boolean used) {
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.used = used;
    }

    public PasswordResetToken(String token, User user, Instant expiresAt) {
        this(token, user, expiresAt, false);
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }
}
