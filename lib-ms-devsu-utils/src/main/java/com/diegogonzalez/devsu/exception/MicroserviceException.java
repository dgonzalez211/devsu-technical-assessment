package com.diegogonzalez.devsu.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

/*
 * Author: Diego González
 *
 * This code is the exclusive property of the author. Any unauthorized use,
 * distribution, or modification is prohibited without the author's explicit consent.
 *
 * Disclaimer: This code is provided "as is" without any warranties of any kind,
 * either express or implied, including but not limited to warranties of merchantability
 * or fitness for a particular purpose.
 */
@Getter
@JsonIgnoreProperties({"cause", "stackTrace", "localizedMessage", "suppressed", "status", "errorEnum"})
public class MicroserviceException extends RuntimeException {
    private final int status;
    private final String code;
    private final ApplicationResponse errorEnum;

    public MicroserviceException(final ApplicationResponse applicationResponse) {
        this(applicationResponse, null);
    }

    public MicroserviceException(final ApplicationResponse applicationResponse, final Throwable cause) {
        this(applicationResponse, 500, cause);
    }

    public MicroserviceException(final ApplicationResponse applicationResponse, final int httpStatus) {
        this(applicationResponse, httpStatus, null);
    }

    public MicroserviceException(final ApplicationResponse applicationResponse, final int httpStatus, final Throwable cause) {
        super(applicationResponse.getMessage(), cause);
        errorEnum = applicationResponse;
        status = httpStatus;
        code = applicationResponse.getCode();
    }
}
