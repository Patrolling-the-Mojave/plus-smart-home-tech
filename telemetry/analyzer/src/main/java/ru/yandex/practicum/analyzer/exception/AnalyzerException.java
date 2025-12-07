package ru.yandex.practicum.analyzer.exception;

public class AnalyzerException extends RuntimeException {
    public AnalyzerException (String message, Throwable cause){
        super(message,cause);
    }
}
