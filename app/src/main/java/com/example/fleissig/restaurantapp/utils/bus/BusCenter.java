package com.example.fleissig.restaurantapp.utils.bus;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by PhucTV on 5/27/15.
 */
public class BusCenter {
    private static MainThreadBus _INSTANCE;

    public static MainThreadBus getInstance() {
        if (_INSTANCE == null) {
            _INSTANCE = new MainThreadBus();
        }
        return _INSTANCE;
    }

}
