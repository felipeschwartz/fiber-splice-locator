package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderStatusDescriptionDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderStatusDescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { ServiceOrderMapper.class }
)
public interface ServiceOrderStatusDescriptionMapper {

    ServiceOrderStatusDescriptionDTO toDTO(ServiceOrder entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ServiceOrderStatusDescription toEntity(ServiceOrderDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDTO(ServiceOrderStatusDescriptionDTO dto, @MappingTarget ServiceOrderStatusDescription entity);
}