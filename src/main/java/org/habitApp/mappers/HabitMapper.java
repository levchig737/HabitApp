package org.habitApp.mappers;

import org.habitApp.domain.dto.habitDto.HabitDto;
import org.habitApp.domain.entities.HabitEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HabitMapper {
    HabitDto habitToHabitDto(HabitEntity habit);
    HabitEntity habitDtoToHabit(HabitDto habitDto);
}

