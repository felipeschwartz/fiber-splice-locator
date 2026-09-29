package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.UserDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { AddressMapper.class }
)
public interface UserMapper {

    @Mapping(target = "password", ignore = true)
    UserDTO toDTO(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password")
    User toEntity(UserDTO dto);

    // active só muda pelo endpoint de desativação, que aplica a hierarquia de quem pode desativar quem.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "active", ignore = true)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);

}