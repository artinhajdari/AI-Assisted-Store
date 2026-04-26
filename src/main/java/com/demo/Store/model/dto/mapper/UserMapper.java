package com.demo.Store.model.dto.mapper;

import com.demo.Store.model.User;
import com.demo.Store.model.dto.UserDto;
import com.demo.Store.util.DateUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

    @Mapping(target = "lastLogin", source = "lastLogin", qualifiedByName = "instantToZonedString")
    @Mapping(target = "locked", source = "locked", qualifiedByName = "convertLockedSafely")
    UserDto toUserDto(User user);

    List<UserDto> toDtos(List<User> user);

    User toUser(UserDto userDto);

    @Named("convertLockedSafely")
    public static Boolean convertLockedSafely(Boolean locked) {
        if (Objects.isNull(locked)) {
            return Boolean.FALSE;
        }
        return locked;
    }

    @Named("instantToZonedString")
    public static String instantToZonedString(Instant instant) {
        if (Objects.isNull(instant)) {
            return "";
        }
        return DateUtils.convertToLocalizedString(instant);
    }

}
