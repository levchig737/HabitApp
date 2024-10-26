package org.habitApp.dto.habitDto;

import org.habitApp.models.Habit;
import org.mapstruct.Mapper;

@Mapper
public interface HabitMapper {
    HabitDto habitToHabitDto(Habit habit);
    Habit habitDtoToHabit(HabitDto habitDto);
}

