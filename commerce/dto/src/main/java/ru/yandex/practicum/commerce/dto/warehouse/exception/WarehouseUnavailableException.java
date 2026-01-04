package ru.yandex.practicum.commerce.dto.warehouse.exception;

public class WarehouseUnavailableException extends RuntimeException {
  public WarehouseUnavailableException(String message) {
    super(message);
  }
}
