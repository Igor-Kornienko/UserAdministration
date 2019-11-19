package kornienko.handler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Arrays;

@ControllerAdvice
public class JwtExceptionHandler extends ResponseEntityExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(JwtExceptionHandler.class);

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({SignatureException.class})
    public WebException handleSignatureException(final SignatureException ex){
        log(ex,"Invalid JWT signature");
        return new WebException(ex);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MalformedJwtException.class})
    public WebException handleMalformedJwtException(final MalformedJwtException ex){
        log(ex,"Invalid JWT token");
        return new WebException(ex);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ExpiredJwtException.class})
    public WebException handleExpiredJwtException(final ExpiredJwtException ex){
        log(ex,"Expired JWT token");
        return new WebException(ex);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({UnsupportedJwtException.class})
    public WebException handleUnsupportedJwtException(final UnsupportedJwtException ex){
        log(ex,"Unsupported JWT token");
        return new WebException(ex);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    public WebException handleIllegalArgumentException(final IllegalArgumentException ex){
        log(ex,"JWT claims string is empty");
        return new WebException(ex);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({Exception.class})
    public WebException handleException(final Exception ex){
        log(ex,"Unknown exception");
        return new WebException(ex);
    }

    private void log (Exception ex, String message){
        System.out.println();
        logger.warn("\n" + message + "\n" + ex.getMessage() + "\n" + Arrays.toString(ex.getStackTrace()));
    }

}
