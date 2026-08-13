package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderPhotoDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceOrderPhotoMapper {

    @Mapping(target = "serviceOrderId", source = "serviceOrder.serviceOrderId")
    ServiceOrderPhotoDTO toDTO(ServiceOrderPhoto entity);

    @Mapping(target = "serviceOrderPhotoId", ignore = true)
    @Mapping(target = "serviceOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ServiceOrderPhoto toEntity(ServiceOrderPhotoDTO dto);

    @Mapping(target = "serviceOrderPhotoId", ignore = true)
    @Mapping(target = "serviceOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDTO(ServiceOrderPhotoDTO dto, @MappingTarget ServiceOrderPhoto entity);
}