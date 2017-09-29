package com.example.fleissig.restaurantapp.subcategory;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by carlosnetto on 06/04/17.
 */

public class SubCategory implements Serializable{
    public HashMap<String, Boolean> dishes;
    public String text;

    public SubCategory() {
    }

    public HashMap<String, Boolean> getDishes() {
        return dishes;
    }

    public String getText() {
        return text;
    }

}
