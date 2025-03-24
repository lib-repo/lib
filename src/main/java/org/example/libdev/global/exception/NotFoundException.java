package org.example.libdev.global.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends BusinessException {
    private final String resourceName;

    public NotFoundException(String resourceName, ErrorCode errorCode) {
        super(resourceName + "not found", ErrorCode.NOT_FOUND);
        this.resourceName = resourceName;
    }

    public static class UserNotFoundException extends NotFoundException {
        public UserNotFoundException(String resourceName) {
            super(resourceName, ErrorCode.USER_NOT_FOUND);
        }
    }

    public static class BookNotFoundException extends NotFoundException {
        public BookNotFoundException(String resourceName) {
            super(resourceName, ErrorCode.BOOK_NOT_FOUND);
        }
    }

    public static class RentNotFoundException extends NotFoundException {
        public RentNotFoundException(String resourceName) {
            super(resourceName , ErrorCode.RENT_NOT_FOUND);
        }
    }

    public static class AvailabilityNotFoundException extends NotFoundException {
        public AvailabilityNotFoundException(String resourceName) {
            super(resourceName, ErrorCode.AVAILABILITY_NOT_FOUND);
        }
    }

    public static class LibraryNotFoundException extends NotFoundException {
        public LibraryNotFoundException(String resourceName) {
            super(resourceName, ErrorCode.AVAILABILITY_NOT_FOUND);
        }
    }

    public static class LibraryAgreementNotFoundException extends NotFoundException {
        public LibraryAgreementNotFoundException(String resourceName) {
            super(resourceName, ErrorCode.NOT_FOUND);
        }
    }
}



