package github.felipeschwartz.fiber_splice_locator.mapper;

import github.felipeschwartz.fiber_splice_locator.model.dto.UserDTO;
import github.felipeschwartz.fiber_splice_locator.model.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        uses = { AddressMapper.class }
)
public interface UserMapper {

    UserDTO toDTO(User entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", source = "password")
    User toEntity(UserDTO dto);

}