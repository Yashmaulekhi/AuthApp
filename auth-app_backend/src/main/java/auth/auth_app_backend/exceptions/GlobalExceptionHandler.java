package auth.auth_app_backend.exceptions;

import auth.auth_app_backend.dtos.ApiError;
import auth.auth_app_backend.dtos.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

    @RestControllerAdvice
public class GlobalExceptionHandler extends RuntimeException {
        @ExceptionHandler({
                UsernameNotFoundException.class,
                BadCredentialsException.class,
                CredentialsExpiredException.class,
                ExpiredJwtException.class,
                JwtException.class,
                AuthenticationException.class
        })
        public ResponseEntity<ApiError> handleAuthException(Exception e, HttpServletRequest request){
        var apiError=ApiError.of(HttpStatus.BAD_REQUEST.value(),"Bad Request",e.getMessage(),request.getRequestURI());
        return ResponseEntity.badRequest().body(apiError);
        }









        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorResponse> handleError(ResourceNotFoundException exception) {

       ErrorResponse internalServerError=new ErrorResponse(exception.getMessage(),HttpStatus.NOT_FOUND,404);
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(internalServerError);
        }
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<ErrorResponse> handleErrors(IllegalArgumentException exception) {

       ErrorResponse internalServerError=new ErrorResponse(exception.getMessage(),HttpStatus.BAD_REQUEST,400);
       return ResponseEntity.status(HttpStatus.NOT_FOUND).body(internalServerError);
        }
}
