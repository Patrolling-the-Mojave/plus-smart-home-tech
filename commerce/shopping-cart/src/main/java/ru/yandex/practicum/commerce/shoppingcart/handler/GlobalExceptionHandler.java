package ru.yandex.practicum.commerce.shoppingcart.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.commerce.dto.error.ErrorResponse;
import ru.yandex.practicum.commerce.shoppingcart.exception.CartDeactivatedException;
import ru.yandex.practicum.commerce.shoppingcart.exception.NoProductsInShoppingCartException;
import ru.yandex.practicum.commerce.shoppingcart.exception.NotFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CartDeactivatedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<ErrorResponse> handleDeactivatedCart(final CartDeactivatedException exception) {
        log.error("deactivated cart", exception);
        ErrorResponse errorResponse = new ErrorResponse("deactivated cart", exception.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    @ExceptionHandler(NoProductsInShoppingCartException.class)
    @ResponseStatus(HttpStatus.PRECONDITION_FAILED)
    public ResponseEntity<ErrorResponse> handleCartNotAvailable(final NoProductsInShoppingCartException exception) {
        log.error("cart not available", exception);
        ErrorResponse errorResponse = new ErrorResponse("cart not available", exception.getMessage());
        return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleNotFound(final NotFoundException e){
        log.error("not found", e);
        ErrorResponse errorResponse = new ErrorResponse("nout found", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleException(final Exception exception){
        log.error("error", exception);
        ErrorResponse errorResponse = new ErrorResponse("error", exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
