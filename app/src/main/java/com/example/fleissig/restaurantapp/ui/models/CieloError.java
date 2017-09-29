package com.example.fleissig.restaurantapp.ui.models;

/**
 * Just represent an error sent by Cielo; most of time, client errors. 
 * 
 * @see <a href="https://developercielo.github.io/Webservice-3.0/english.html#error-codes">Error Codes</a> 
 */ 
public class CieloError { 
    private final Integer ReturnCode;
    private final String ReturnMessage;

    public CieloError(Integer returnCode, String returnMessage) {
        ReturnCode = returnCode;
        ReturnMessage = returnMessage;
    }

    public Integer getReturnCode() {
        return ReturnCode;
    }

    public String getReturnMessage() {
        return ReturnMessage;
    }
}