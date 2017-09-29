package com.example.fleissig.mylibrary;

import com.google.firebase.database.Exclude;

public class Rating {
    public int stars1;
    public int stars2;
    public int stars3;
    public int stars4;
    public int stars5;

    public Rating() {
    }

    public float getAverage() {
        float stars = stars1 + stars2 * 2 + stars3 * 3 + stars4 * 4 + stars5 * 5;
        float people = stars1 + stars2 + stars3 + stars4 + stars5;
        if (people <= 0) return .0f;
        return stars / people;
    }

    @Exclude
    public void inc(float rating) {
        if (rating == 1) {
            stars1++;
        } else if (rating == 2) {
            stars2++;
        } else if (rating == 3) {
            stars3++;
        } else if (rating == 4) {
            stars4++;
        } else if (rating == 5) {
            stars5++;
        }
    }
}
