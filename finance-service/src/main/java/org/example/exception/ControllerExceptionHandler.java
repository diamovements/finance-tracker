package org.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<>("Произошла ошибка: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handleCategoryNotFoundException(CategoryNotFoundException ex) {
        return new ResponseEntity<>("Ошибка: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LimitNotFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handleLimitNotFoundException(LimitNotFoundException ex) {
        return new ResponseEntity<>("Ошибка: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(GoalNotFoundException.class)
    @ResponseBody
    public ResponseEntity<String> handleGoalNotFoundException(GoalNotFoundException ex) {
        return new ResponseEntity<>("Ошибка: " + ex.getMessage(), HttpStatus.NOT_FOUND);
    }

}
