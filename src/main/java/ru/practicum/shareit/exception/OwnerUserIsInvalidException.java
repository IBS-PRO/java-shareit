package ru.practicum.shareit.exception;

public class OwnerUserIsInvalidException extends RuntimeException {
    public OwnerUserIsInvalidException(String message) {
        super(message);
    }
}
