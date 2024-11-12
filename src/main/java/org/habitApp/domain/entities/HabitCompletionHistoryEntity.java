package org.habitApp.domain.entities;

import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Сущность для представления истории выполнения привычки.
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HabitCompletionHistoryEntity {
    private long id;
    private long habitId;
    private long userId;
    private LocalDate completionDate;
}
