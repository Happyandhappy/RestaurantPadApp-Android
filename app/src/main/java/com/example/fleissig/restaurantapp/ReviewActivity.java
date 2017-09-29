package com.example.fleissig.restaurantapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

public class ReviewActivity extends AppCompatActivity {
    public static final String COMMENT_EXTRA = "COMMENT_EXTRA";
    public static final String RATING_EXTRA = "RATING_EXTRA";
    private View mRootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        mRootView = findViewById(R.id.activity_review);

//        setUpToolbar("");

        final RatingBar ratingBar = (RatingBar) findViewById(R.id.rating_ratingbar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f) ratingBar.setRating(1.0f);
            }
        });
        final EditText commentEditText = (EditText) findViewById(R.id.editText);

        Button postButton = (Button) findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String comment = commentEditText.getText().toString();
                Intent data = new Intent();
                if (!comment.isEmpty()) data.putExtra(COMMENT_EXTRA, comment);
                float rating = ratingBar.getRating();
                if (rating > 0) data.putExtra(RATING_EXTRA, rating);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

//    @Override
//    protected View getRootView() {
//        return mRootView;
//    }
}
