package com.app.diamondhotelbackend.exception;

public class NotAllSelectedRoomsAvailableException extends RuntimeException {

    public NotAllSelectedRoomsAvailableException(String message) {
        super(message);
    }
}
