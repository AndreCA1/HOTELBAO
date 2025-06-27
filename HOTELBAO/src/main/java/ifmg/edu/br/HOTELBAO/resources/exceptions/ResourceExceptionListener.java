package ifmg.edu.br.HOTELBAO.resources.exceptions;

import ifmg.edu.br.HOTELBAO.services.exceptions.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

//Essa classe é responsável por tratar todos os seus erros, basta add os metódos para cada erro escutado

//Indica para o Spring boot que essa classe vai ficar escutando excessões
@ControllerAdvice
public class ResourceExceptionListener {

    //Indica que quando o erro "ResourceNotFound.class" essa é a função que deve ser chamada
    @ExceptionHandler(ResourceNotFound.class)
    public ResponseEntity<StandartError> resourceNotFound(ResourceNotFound ex, HttpServletRequest request) {
        StandartError error = new StandartError();

        error.setTimestamp(Instant.now());
        HttpStatus status = HttpStatus.NOT_FOUND;
        error.setStatus(status.value());
        error.setMessage(ex.getMessage());
        error.setError("Resource not found");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<StandartError> clientException (ClientException ex, HttpServletRequest request) {
        StandartError error = new StandartError();

        error.setTimestamp(Instant.now());
        HttpStatus status = HttpStatus.NOT_FOUND;
        error.setStatus(status.value());
        error.setMessage(ex.getMessage());
        error.setError("Resource not found");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(RoomException.class)
    public ResponseEntity<StandartError> roomException (RoomException ex, HttpServletRequest request) {
        StandartError error = new StandartError();

        error.setTimestamp(Instant.now());
        HttpStatus status = HttpStatus.NOT_FOUND;
        error.setStatus(status.value());
        error.setMessage(ex.getMessage());
        error.setError("Resource not found");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StandartError> databaseException(DataBaseException ex, HttpServletRequest request) {
        StandartError error = new StandartError();

        error.setTimestamp(Instant.now());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        error.setStatus(status.value());
        error.setMessage(ex.getMessage());
        error.setError("Database exception");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> MethodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        ValidationError error = new ValidationError();

        error.setTimestamp(Instant.now());
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        error.setStatus(status.value());
        error.setMessage(ex.getMessage());
        error.setError("Validation Exception");
        error.setPath(request.getRequestURI());

        for(FieldError f : ex.getBindingResult().getFieldErrors()){
            error.addFieldMessage(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<StandartError> EmailException(EmailException ex, HttpServletRequest request) {
        StandartError error = new StandartError();
        error.setTimestamp(Instant.now());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        error.setStatus(status.value());
        error.setMessage(ex.getMessage());
        error.setError("Email failed!");
        error.setPath(request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(DailyException.class)
    public ResponseEntity<StandartError> DailyException(DailyException ex, HttpServletRequest request) {
        StandartError error = new StandartError();
        error.setTimestamp(Instant.now());
        HttpStatus status = HttpStatus.BAD_REQUEST;
        error.setStatus(status.value());
        error.setMessage(ex.getMessage());
        error.setError("Daily Exception");

        return ResponseEntity.status(status).body(error);
    }
}
