package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { UserMapper.class, AddressMapper.class, CEOMapper.class, ServiceOrderPhotoMapper.class, ServiceOrderStatusDescriptionMapper.class }
)
public interface ServiceOrderMapper {

    ServiceOrderDTO toDTO(ServiceOrder entity);

    @Mapping(target = "serviceOrderId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ServiceOrder toEntity(ServiceOrderDTO dto);
}
