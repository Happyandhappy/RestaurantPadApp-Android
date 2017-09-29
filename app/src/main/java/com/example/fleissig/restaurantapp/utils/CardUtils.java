package com.example.fleissig.restaurantapp.utils;

/**
 * Created by phuctran on 7/7/17.
 */

public class CardUtils {
    public static String getBrandFromCardNumber(String cardNumber) {
        if (cardNumber.startsWith("34") || cardNumber.startsWith("37")) {
            return "Amex";
        } else if (cardNumber.startsWith("60") || cardNumber.startsWith("62") || cardNumber.startsWith("64") || cardNumber.startsWith("65")) {
            return "Discover";
        } else if (cardNumber.startsWith("35")) {
            return "JCB";
        } else if (cardNumber.startsWith("30") || cardNumber.startsWith("36") || cardNumber.startsWith("38") || cardNumber.startsWith("39")) {
            return "Diners";
        } else if (cardNumber.startsWith("4")) {
            return "Visa";
        } else if (cardNumber.startsWith("5")) {
            return "Master";
        } else {
            return "Unknown";
        }
    }
}
