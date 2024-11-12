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

    /**
     * HabitEntity to HabitDto
     * @param habit HabitEntity
     * @return HabitDto
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    @Mapping(source = "userId", target = "userId")
    HabitDto habitToHabitDto(HabitEntity habit);

    /**
     * HabitDto to HabitEntity
     * @param habitDto HabitDto
     * @return HabitEntity
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    @Mapping(source = "userId", target = "userId")
    HabitEntity habitDtoToHabit(HabitDto habitDto);


    ////HabitDtoCreateUpdate
    /**
     * HabitEntity to HabitDtoCreateUpdate
     * @param habit HabitEntity
     * @return HabitDtoCreateUpdate
     */
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    HabitDtoCreateUpdate habitToHabitDtoCreateUpdate(HabitEntity habit);

    /**
     * HabitDtoCreateUpdate to HabitEntity
     * @param habitDtoCreateUpdate HabitDtoCreateUpdate
     * @return HabitEntity
     */
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    HabitEntity habitDtoCreateUpdateToHabit(HabitDtoCreateUpdate habitDtoCreateUpdate);


    ////HabitDtoResponse
    /**
     * HabitEntity to HabitDtoResponse
     * @param habit HabitEntity
     * @return HabitDtoResponse
     */
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    HabitDtoResponse habitToHabitDtoResponse(HabitEntity habit);

    /**
     * HabitDtoResponse to HabitEntity
     * @param HabitDtoResponse HabitDtoResponse
     * @return HabitEntity
     */
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "frequency", target = "frequency")
    HabitEntity habitDtoResponseToHabit(HabitDtoResponse HabitDtoResponse);
}

