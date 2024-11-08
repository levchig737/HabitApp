package org.habitApp.domain.dto.habitDto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HabitDtoCreateUpdate {
    private String name;
    private String description;
    private String frequency;
}
