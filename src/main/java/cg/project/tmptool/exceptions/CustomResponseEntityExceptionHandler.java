package cg.project.tmptool.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


// Allowing the Dev to address exception handling across the whole application, taking care of all exceptions thrown from any place in the application
// Centralize exception handling logic into a global component
// Control over the body response, as well as the HTTP status code.
// A mechanism to help our server break away from having exception handlers that are controller specific
// Gives our server a global exception handler for all controllers (nutshell)

//该文件是为了结合exception和其对应的response
@ControllerAdvice
//上面是controllerAdvice，加上下面这一句，意味着管理所有的restController
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public final ResponseEntity<Object> handleProjectIdException(ProjectIdException exception, WebRequest request) {
        ProjectIdExceptionResponse exceptionResponse = new ProjectIdExceptionResponse(exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler // 同理
    public final ResponseEntity<Object> handleProjectNotFoundException(ProjectNotFoundException exception, WebRequest request) {
        ProjectNotFoundExceptionResponse exceptionResponse = new ProjectNotFoundExceptionResponse(exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler // 同理
    public final ResponseEntity<Object> handleUserNameExistsException(UserNameExistsException exception, WebRequest request) {
        UserNameExistsResponse exceptionResponse = new UserNameExistsResponse(exception.getMessage());
        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}