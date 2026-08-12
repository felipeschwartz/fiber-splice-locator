package github.felipeschwartz.fiber_splice_locator.model.entities;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "ceos")
public class CEO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String boxNumber;

    @Column
    private String notes;

    @Embedded
    private Address address;

    public CEO() {
    }

    public CEO(Long id, String boxNumber, String notes, Address address) {
        this.id = id;
        this.boxNumber = boxNumber;
        this.notes = notes;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBoxNumber() {
        return boxNumber;
    }

    public void setBoxNumber(String boxNumber) {
        this.boxNumber = boxNumber;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CEO ceo = (CEO) o;
        return Objects.equals(getId(), ceo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
