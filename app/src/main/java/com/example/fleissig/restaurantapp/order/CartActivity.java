package com.example.fleissig.restaurantapp.order;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.DishOrder;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.Order;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.example.fleissig.restaurantapp.Functions;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.payment.PaymentActivity;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartActivity extends AppCompatActivity {
    private static final String TAG = CartActivity.class.getSimpleName();
    private DatabaseReference mOrdersRef;
    private Cart mCart;
    private boolean mPossibilityChangeCart;
    private ArrayAdapter mAdapter;
    private Button mPriceButton;

    @Override
    protected void onResume() {
        super.onResume();

        boolean possibilityChangeCart = Functions.readPossibilityChangeCartItem(this);
        if (possibilityChangeCart != mPossibilityChangeCart) {
            mPossibilityChangeCart = possibilityChangeCart;
            mAdapter.notifyDataSetChanged();
        }

        updatePrice();
    }

    private void updatePrice() {
        mPriceButton.setText("$ " + String.valueOf(MyApplication.getInstance().CART.getTotalPrice())
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mPossibilityChangeCart = Functions.readPossibilityChangeCartItem(this);

        final ListView dishesListView = (ListView) findViewById(R.id.dishes_listview);

        mOrdersRef = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.ORDERS_KEY);

        mCart = MyApplication.getInstance().CART;

        mAdapter = new MyDishArrayAdapter(getApplicationContext(),
                R.layout.cart_dish_item, mCart.getItems());
        dishesListView.setAdapter(mAdapter);

        mPriceButton = (Button) findViewById(R.id.price_button);

        Button nextButton = (Button) findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.getInstance().CART.getTotalCount() <= 0) {
                    Toast.makeText(getApplicationContext(), R.string.cart_empty_message,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                DatabaseReference orderId = writeOrder();

                writeCommens();

                writeRatings();

                FirebaseMessaging.getInstance().subscribeToTopic(
                        orderId.getKey()
//                        "news"
                );
                Intent intent = new Intent(getApplicationContext(), WaitingOrderActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                //finish();
            }
        });

        Button payButton = (Button) findViewById(R.id.pay_button);
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyApplication.getInstance().CART.getTotalCount() <= 0) {
                    Toast.makeText(getApplicationContext(), R.string.cart_empty_message,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(intent);
                finish();
            }
        });
    }

    private void writeRatings() {
        HashMap<DatabaseReference, Float> dishRatingMap = MyApplication.getInstance()
                .dishRatingMap;
        for (DatabaseReference dish : dishRatingMap.keySet()) {
            final float rating = dishRatingMap.get(dish);
            String dishId = dish.getKey();
            FirebaseAuth auth = FirebaseAuth.getInstance();
            FirebaseUser user = auth.getCurrentUser();
            if (user != null && user.isAnonymous()) {
                postponeAnonymousRating(dishId, rating);
            } else {
                ShareFunctions.writeRegularRating(TAG, rating, dishId);
            }
        }
    }

    private void postponeAnonymousRating(String dishId, float rating) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(FirebaseConstants.DEFERRED_RATING_KEY).push().child(dishId).setValue((long) rating);
    }

    @NonNull
    private DatabaseReference writeOrder() {
        String restaurantId = ShareFunctions.readRestaurantId(getApplicationContext());
        DatabaseReference orderId = mOrdersRef.child(restaurantId).push();
        Map<String, DishOrder> map = mCart.makeOrder();

        orderId.setValue(new Order(map, Functions.readTableNumber(this)));

        return orderId;
    }

    private void writeCommens() {
        DatabaseReference commentsRef = mOrdersRef.getRoot()
                .child(FirebaseConstants.COMMENTS_KEY);
        HashMap<DatabaseReference, String> dishCommentMap = MyApplication.getInstance()
                .dishCommentMap;
        for (DatabaseReference dish : dishCommentMap.keySet()) {
            commentsRef.child(dish.getKey()).push().setValue(dishCommentMap.get(dish));
        }
    }

    class MyDishArrayAdapter extends ArrayAdapter<CartItem> {
        public MyDishArrayAdapter(Context context, int resource, List<CartItem> items) {
            super(context, resource, items);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            // assign the view we are converting to a local variable
            View v = convertView;

            // first check to see if the view is null. if so, we have to inflate it.
            // to inflate it basically means to render, or show, the view.
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.cart_dish_item, null);
            }

		/*
         * Recall that the variable position is sent in as an argument to this method.
		 * The variable simply refers to the position of the current object in the list.
		 * (The ArrayAdapter
		 * iterates through the list we sent it)
		 *
		 * Therefore, i refers to the current Item object.
		 */
            final CartItem cartItem = getItem(position);

            if (cartItem != null) {
                final Dish dish = cartItem.getDish();
                ((TextView) v.findViewById(R.id.name_textview)).setText(dish.text);

                ((TextView) v.findViewById(R.id.desc_textview))
                        .setText(dish.desc != null ? dish.desc : "");


                ImageView imageView = (ImageView) v.findViewById(R.id.image);
                FirebaseStorage storage = FirebaseStorage.getInstance();
                String url = dish.image_url;
                StorageReference imageRef = null;
                if (url != null) imageRef = storage.getReferenceFromUrl(url);
                // Load the image using Glide
                Glide.with(getApplicationContext())
                        .using(new FirebaseImageLoader())
                        .load(imageRef)
                        .error(R.drawable.no_image)
                        .into(imageView);


                Double price = dish.price;
                if (price != null) {
                    ((TextView) v.findViewById(R.id.tvPrice))
                            .setText(String.valueOf(price));
                }
                Double priceBottle = dish.price_bottle;
                if (priceBottle != null) {
                    ((TextView) v.findViewById(R.id.tvPrice))
                            .setText(String.valueOf(priceBottle) + " por garrafa");
                }

                DishOrder dishOrder = cartItem.getDishOrder();

                final TextView quantityTextView = (TextView) v.findViewById(R.id.quantity_textview);
                quantityTextView.setText(String.valueOf(dishOrder.getQuantity()));

                Button plusButton = (Button) v.findViewById(R.id.plus_button);
                plusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCart.incrementDish(dish);
                        notifyDataSetChanged();
                        updatePrice();
                    }
                });

                Button minusButton = (Button) v.findViewById(R.id.minus_button);
                minusButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCart.decrementDish(dish);
                        notifyDataSetChanged();
                        updatePrice();
                    }
                });

                //plusButton.setEnabled(!dishOrder.getOrderedQuantity());
                //minusButton.setEnabled(!dishOrder.getOrderedQuantity());
            }

            // the view must be returned to our activity
            return v;
        }
    }
}

