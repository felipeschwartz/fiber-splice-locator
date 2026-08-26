package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderStatusDescriptionDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderStatusDescription;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING
)
public interface ServiceOrderStatusDescriptionMapper {

    @Mapping(
            target = "serviceOrderId",
            source = "serviceOrder.serviceOrderId"
    )
    ServiceOrderStatusDescriptionDTO toDTO(
            ServiceOrderStatusDescription entity
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ServiceOrderStatusDescription toEntity(
            ServiceOrderStatusDescriptionDTO dto
    );

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "serviceOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDTO(
            ServiceOrderStatusDescriptionDTO dto,
            @MappingTarget ServiceOrderStatusDescription entity
    );
}