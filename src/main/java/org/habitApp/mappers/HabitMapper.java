package org.habitApp.mappers;

import org.habitApp.domain.dto.habitDto.HabitDto;
import org.habitApp.domain.dto.habitDto.HabitDtoCreateUpdate;
import org.habitApp.domain.dto.habitDto.HabitDtoResponse;
import org.habitApp.domain.dto.habitDto.HabitReportDto;
import org.habitApp.domain.entities.HabitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface HabitMapper {
    //// HabitDto
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    @Mapping(source = "userId", target = "userId")
    HabitDto habitToHabitDto(HabitEntity habit);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    @Mapping(source = "userId", target = "userId")
    HabitEntity habitDtoToHabit(HabitDto habitDto);


    ////HabitDtoCreateUpdate
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    HabitDtoCreateUpdate habitToHabitDtoCreateUpdate(HabitEntity habit);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    HabitEntity habitDtoCreateUpdateToHabit(HabitDtoCreateUpdate habitDtoCreateUpdate);


    ////HabitDtoResponse
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    HabitDtoResponse habitToHabitDtoResponse(HabitEntity habit);

    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    HabitEntity habitDtoResponseToHabit(HabitDtoResponse habitDtoResponse);
}

