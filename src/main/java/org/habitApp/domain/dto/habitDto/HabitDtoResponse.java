package org.habitApp.domain.dto.habitDto;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HabitDtoResponse {
    private long id;
    private String name;
    private String description;
    private String frequency;
    private long userId;

    public HabitDtoResponse(String name, String description, String frequency) {
        this.name = name;
        this.description = description;
        this.frequency = frequency;
    }
}
