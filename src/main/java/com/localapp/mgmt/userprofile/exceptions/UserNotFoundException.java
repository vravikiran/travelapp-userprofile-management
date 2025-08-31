package com.localapp.mgmt.userprofile.exceptions;

import java.io.Serial;

public class UserNotFoundException extends Exception {

    @Serial
    private static final long serialVersionUID = 7306340786265081818L;

    public UserNotFoundException(String message) {
        super(message);
    }

}
