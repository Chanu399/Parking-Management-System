package com.nibm.parking.exception;
//pop up msg for payment fail
public class PaymentFailedException extends Exception {
    public PaymentFailedException(String message) {
        super(message);
    }
}