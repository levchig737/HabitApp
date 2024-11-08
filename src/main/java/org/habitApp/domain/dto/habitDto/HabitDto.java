package org.habitApp.domain.dto.habitDto;

import lombok.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HabitDto {
    private long id;
    private String name;
    private String description;
    private String frequency;
    private long userId;

    @Override
    public String toString() {
        return "HabitDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", frequency='" + frequency + '\'' +
                ", userId=" + userId +
                '}';
    }
}
