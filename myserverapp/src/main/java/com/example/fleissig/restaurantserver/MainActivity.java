package com.example.fleissig.restaurantserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();
    private static final int SETTINGS_REQUEST = 1000;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private View mRootView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_main);

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            startActivity(ShareFunctions.makeIntent(getApplicationContext(),
                    WelcomeServerActivity.class));
            finish();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        if(bar != null) bar.setDisplayShowTitleEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mRootView = findViewById(R.id.main_content);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });


//        fillProductsFromCsv();

    }

    @Override
    protected void onResume() {
        super.onResume();

        writeDeferredRatings();
    }

    private void writeDeferredRatings() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseConstants.DEFERRED_RATING_KEY);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    HashMap<String, Long> map = (HashMap<String, Long>) d.getValue();
                    d.getRef().removeValue();
                    for (String dishKey : map.keySet()) {
                        // 1 element
                        ShareFunctions.writeRegularRating(TAG, map.get(dishKey), dishKey);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "writeDeferredRatings(): " + databaseError.getMessage());
            }
        });
    }

//    private void fillProductsFromCsv() {
//        final String restaurantId = "restaurant_id0";
//        HashMap<String, ArrayList<String>> categoryDishes = new HashMap<>();
//
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//
//        CSVReader reader = null;
//        try {
//            reader = new CSVReader(new InputStreamReader(
//                    getResources().openRawResource(R.raw.products)));
//            reader.readNext();
//            String [] nextLine;
//            while ((nextLine = reader.readNext()) != null) {
//                String category = nextLine[0];
//                String name = nextLine[1];
//                String url = nextLine[2];
//                String price = nextLine[3];
//                String priceBottle = nextLine[4];
//
//                Dish dish = new Dish(name, price, priceBottle, url);
//                DatabaseReference dishRef = ref.child(FirebaseConstants.DISHES_KEY).push();
//                dishRef.setValue(dish);
//
//                if(categoryDishes.containsKey(category)) {
//                    ArrayList<String> dishesKeys = categoryDishes.get(category);
//                    dishesKeys.add(dishRef.getKey());
//                }
//                else {
//                    ArrayList<String> dishesKeys = new ArrayList<>();
//                    dishesKeys.add(dishRef.getKey());
//                    categoryDishes.put(category, dishesKeys);
//                }
//            }
//
//            for(String category : categoryDishes.keySet()) {
//                ArrayList<String> dishes = categoryDishes.get(category);
//
//                DatabaseReference restaurantIdRef = ref.child(FirebaseConstants.CATEGORIES_KEY)
//                        .child(restaurantId).push();
//                restaurantIdRef.child(FirebaseConstants.DISH_TEXT_KEY).setValue(category);
//                for (String dishKey : dishes) {
//                    restaurantIdRef.child(FirebaseConstants.DISHES_KEY)
//                            .child(dishKey).setValue(true);
//                }
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getApplicationContext(),
                                    WelcomeServerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                                    | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            ShareFunctions.showSnackbar(mRootView, R.string.sign_out_failed);
                        }
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivityForResult(i, SETTINGS_REQUEST);
                break;
            case R.id.menu_signout:
                signOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SETTINGS_REQUEST:
                Log.d(TAG, "onActivityResult(): SETTINGS_REQUEST");
                break;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) return OrdersFragment.newInstance();
            else return RankPageFragment.newInstance();
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Order Page";
                case 1:
                    return "Rank Page";
            }
            return null;
        }
    }
}
