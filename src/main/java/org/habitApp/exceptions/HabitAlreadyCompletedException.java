package org.habitApp.exceptions;

public class HabitAlreadyCompletedException extends RuntimeException {
    public HabitAlreadyCompletedException(String message) {
        super(message);
    }
}
