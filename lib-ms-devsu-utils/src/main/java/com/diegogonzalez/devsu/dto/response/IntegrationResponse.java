package com.diegogonzalez.devsu.dto.response;

import com.diegogonzalez.devsu.exception.ApplicationResponse;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

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
@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IntegrationResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 793384895148748674L;
    Object data;
    private String code;
    private String message;
    private List<String> errors;

    public static IntegrationResponse success(Object data) {
        return IntegrationResponse.builder()
                .code(ApplicationResponse.SUCCESS.getCode())
                .message(ApplicationResponse.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    public static IntegrationResponse success() {
        return IntegrationResponse.builder()
                .code(ApplicationResponse.SUCCESS.getCode())
                .message(ApplicationResponse.SUCCESS.getMessage())
                .build();
    }

    public static IntegrationResponse error(String code, String message, List<String> errors) {
        return IntegrationResponse.builder()
                .code(code)
                .message(message)
                .errors(errors)
                .build();
    }
}