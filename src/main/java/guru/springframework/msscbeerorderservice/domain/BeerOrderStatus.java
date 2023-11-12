package guru.springframework.msscbeerorderservice.domain;

public enum BeerOrderStatus {
    NEW, VALIDATED, VALIDATION_PENDING,VALIDATION_EXCEPTION,
    ALLOCATION_PENDING, ALLOCATION_EXCEPTION, CANCELLED,
    PENDING_INVENTORY, PICKED_UP, DELIVERED, ALLOCATED, DELIVERY_EXCEPTION
}
