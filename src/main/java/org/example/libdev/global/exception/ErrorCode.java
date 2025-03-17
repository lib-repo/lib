package org.example.libdev.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE("C-001", "Invalid Input Value"),
    METHOD_NOT_ALLOWED("C-002", "Method Not Allowed"),
    INTERNAL_SERVER_ERROR("C-004", "Server Error"),
    INVALID_TYPE_VALUE("C-005", "Invalid Type Value"),
    HANDLE_ACCESS_DENIED("C-006", "Access is Denied"),
    NOT_FOUND("C-007", "Not Found"),

    // Rent
    RENT_NOT_FOUND("R-001", "Rent Not Found"),
    RENT_ALREADY_RENTED("R-002", "Book Already Rented"),
    RENT_STATUS_INVALID("R-003", "Invalid Rent Status for Renewal"),
    RENT_RENEW_EXCEEDED("R-004", "Renewal Limit Exceeded"),

    // Book
    BOOK_NOT_FOUND("B-001", "Book Not Found"),

    // Availability

    AVAILABILITY_NOT_FOUND("A-001", "Availability Not Found"),

    // User
    EMAIL_DUPLICATE("U-001", "Duplicate Email Address"),
    NICKNAME_DUPLICATE("U-002", "Duplicate Nickname"),
    USER_NOT_FOUND("U-003", "User Not Found");


    private final String code;
    private final String message;

    ErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }
}
