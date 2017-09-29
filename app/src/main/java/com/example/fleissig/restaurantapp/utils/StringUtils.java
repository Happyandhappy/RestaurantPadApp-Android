package com.example.fleissig.restaurantapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by phuctran on 7/11/17.
 */

public class StringUtils {
    private static final String CARD_INPUT_FORMAT = "yymm";
    private static final String PAYMENT_OUTPUT_FORMAT = "mm/yyyy";

    public static String formatDateForPayment(String date) {
        try {
            SimpleDateFormat dt = new SimpleDateFormat(CARD_INPUT_FORMAT);
            Date inputDate = dt.parse(date);
            SimpleDateFormat dt1 = new SimpleDateFormat(PAYMENT_OUTPUT_FORMAT);
            return (dt1.format(inputDate));
        } catch (Exception Ex) {

        }
        return "";
    }

    public static String standarizeCardNameForPayment(String cardName) {
        StringBuilder returnResult = new StringBuilder();

        if (cardName == null) return returnResult.toString();

        String[] splitName = cardName.split("/");
        if (splitName.length != 2) return cardName;
        returnResult.append(splitName[1].trim()).append(" ").append(splitName[0].trim());
        return returnResult.toString();
    }


    public static String formatMoney(double myNumber){
        return "R$" + String.format("%.2f", myNumber);
    }
}
