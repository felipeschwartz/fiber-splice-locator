package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.CEODTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.CEO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { AddressMapper.class })
public interface CEOMapper {

    CEODTO toDTO(CEO entity);

    @Mapping(target = "id", ignore = true)
    CEO toEntity(CEODTO dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(CEODTO updatedDTO, CEO existingCEO);
}
