package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.AddressDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.Address;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AddressMapper {

    AddressDTO toDTO(Address entity);

    Address toEntity(AddressDTO dto);
}
