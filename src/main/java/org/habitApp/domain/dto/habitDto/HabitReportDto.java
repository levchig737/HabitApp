package org.habitApp.domain.dto.habitDto;

import lombok.*;
import org.habitApp.models.Period;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HabitReportDto {
    private long id;
    private int streak;
    private double completionPercentage;
    private int completionCount;
    private Period period;
}
