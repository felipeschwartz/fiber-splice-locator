package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.ServiceOrderPhotoDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.ServiceOrderPhoto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ServiceOrderPhotoMapper {

    @Mapping(target = "id", source = "serviceOrderPhotoId")
    @Mapping(target = "serviceOrderId", source = "serviceOrder.serviceOrderId")
    ServiceOrderPhotoDTO toDTO(ServiceOrderPhoto entity);

    @Mapping(target = "serviceOrderPhotoId", ignore = true)
    @Mapping(target = "serviceOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    ServiceOrderPhoto toEntity(ServiceOrderPhotoDTO dto);

    // Só a ordem é editável: caminho, nome, tipo e tamanho vêm do upload e não podem ser trocados pelo cliente.
    @Mapping(target = "serviceOrderPhotoId", ignore = true)
    @Mapping(target = "serviceOrder", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "storagePath", ignore = true)
    @Mapping(target = "storedFilename", ignore = true)
    @Mapping(target = "originalFilename", ignore = true)
    @Mapping(target = "contentType", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    void updateEntityFromDTO(ServiceOrderPhotoDTO dto, @MappingTarget ServiceOrderPhoto entity);
}