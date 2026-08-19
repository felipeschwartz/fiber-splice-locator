package github.felipeschwartz.fiber_splice_locator.model.dto;

import github.felipeschwartz.fiber_splice_locator.model.enums.CEOStatus;
import org.springframework.hateoas.RepresentationModel;

import java.io.Serializable;
import java.util.Objects;

public class CEODTO extends RepresentationModel<CEODTO> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String boxNumber;
    private String notes;
    private AddressDTO address;
    private CEOStatus status;

    public CEODTO() {
    }

    public CEODTO(Long id, String boxNumber, String notes, AddressDTO address, CEOStatus status) {
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

    public AddressDTO getAddress() {
        return address;
    }

    public void setAddress(AddressDTO address) {
        this.address = address;
    }

    public CEOStatus getStatus() {
        return status;
    }

    public void setStatus(CEOStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CEODTO CEODTO = (CEODTO) o;
        return Objects.equals(getId(), CEODTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }
}
