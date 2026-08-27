package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { UserMapper.class, AddressMapper.class, CEOMapper.class, ServiceOrderPhotoMapper.class, ServiceOrderStatusDescriptionMapper.class }
)
public interface ServiceOrderMapper {

    ServiceOrderDTO toDTO(ServiceOrder entity);

    @Mapping(target = "serviceOrderId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ServiceOrder toEntity(ServiceOrderDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "serviceOrderId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "ceo", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "serviceOrderPhotos", ignore = true)
    @Mapping(target = "serviceOrderStatusDescriptions", ignore = true)
    void updateEntityFromDTO(ServiceOrderDTO dto, @MappingTarget ServiceOrder entity);
}