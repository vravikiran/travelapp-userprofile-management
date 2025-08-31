package com.localapp.mgmt.userprofile.exceptions;

import java.io.Serial;

public class DuplicateUserException extends Exception {

    @Serial
    private static final long serialVersionUID = 3512712329748056392L;

    public DuplicateUserException(String message) {
        super(message);
    }

}
