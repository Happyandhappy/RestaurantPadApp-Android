package com.example.fleissig.restaurantapp.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.fleissig.restaurantapp.ui.fragments.CartaoPagerFragment;
import com.example.fleissig.restaurantapp.ui.fragments.GarcomPagerFragment;

/**
 * Created by phuctran on 6/7/17.
 */

public class PagamentoPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = PagamentoPagerAdapter.class.getSimpleName();
    public static final int NUMBER_OF_TAB = 2;

    public PagamentoPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return CartaoPagerFragment.newInstance();
        }else{
            return GarcomPagerFragment.newInstance();
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_TAB;
    }
}
