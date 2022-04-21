package hexlet.code.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ResponseStatus(value = UNPROCESSABLE_ENTITY)
public class UnprocessableEntityException extends Exception {
    public UnprocessableEntityException(String message) {
        super(message);
    }
}
