package com.example.fleissig.restaurantapp.ui.fragments.bases;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fleissig.restaurantapp.ui.widgets.Toaster;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by phuctran on 5/13/17.
 */

public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();

    private Unbinder unbinder;
    private FragmentTransaction fragmentTransaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(getSubclassName(), (new StringBuilder()).append("onCreateView:").append(getClass().getName()).toString());

        final View fragmentView = inflater.inflate(getLayoutResource(), container, false);
        unbinder = ButterKnife.bind(this, fragmentView);
        updateFollowingViewBinding();
        return fragmentView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getSubclassName(), (new StringBuilder()).append("onCreate:").append(getClass().getName()).toString());

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(getSubclassName(), (new StringBuilder()).append("onPause:").append(getClass().getName()).toString());

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(getSubclassName(), (new StringBuilder()).append("onResume:").append(getClass().getName()).toString());

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(getSubclassName(), (new StringBuilder()).append("onViewCreated:").append(getClass().getName()).toString());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(getSubclassName(), (new StringBuilder()).append("onAttach:").append(getClass().getName()).toString());

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(getSubclassName(), (new StringBuilder()).append("onDetach:").append(getClass().getName()).toString());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(getSubclassName(), (new StringBuilder()).append("onDestroy:").append(getClass().getName()).toString());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Log.d(getSubclassName(), (new StringBuilder()).append("onDestroyView:").append(getClass().getName()).toString());

    }

    protected abstract int getLayoutResource();

    protected abstract void updateFollowingViewBinding();

    protected ActionBar getActionBar() {
        Activity localActivity = getActivity();
        if ((localActivity instanceof AppCompatActivity)) {
            return ((AppCompatActivity) localActivity).getSupportActionBar();
        }
        return null;
    }

    public void showFragment(BaseFragment fragment, int fragmentContainer) {
        Log.d(getSubclassName(), (new StringBuilder()).append("showFragment called with tag: ").append(getSubclassName()).toString());
        ((BaseFragmentResponder) getActivity()).showFragment(fragment, fragment.getSubclassName(), fragmentContainer);
    }

    protected void popFragment(BaseFragment fragment) {
        Log.d(getSubclassName(), (new StringBuilder()).append("popFragment called with tag: ").append(getSubclassName()).toString());
        BaseFragmentResponder basefragmentresponder = (BaseFragmentResponder) getActivity();
        String subclassName = getSubclassName();
        StringBuilder stringbuilder = (new StringBuilder()).append("popFragment -- baseFramentResponder == null? ");
        boolean isBaseFragmentNull;
        if (basefragmentresponder == null) {
            isBaseFragmentNull = true;
        } else {
            isBaseFragmentNull = false;
        }
        Log.d(subclassName, stringbuilder.append(isBaseFragmentNull).toString());
        if (basefragmentresponder != null) {
            basefragmentresponder.popFragment(fragment);
        }
    }

    protected void popBackStackByFragmentTag(String fragmentTag) {
        Log.d(getSubclassName(), "popNowPlayingFragment called");
        ((BaseFragmentResponder) getActivity()).popBackStackByFragmentTag(fragmentTag);
    }

    protected abstract String getSubclassName();

    public void showToast(int i) {
        if (!isAdded()) {
            Log.w(TAG, "showToast - Fragment is not added; aborting showing Toast");
        } else {
            showToast(getResources().getString(i));
        }
    }

    public void showToast(String text) {
        showToast(text, Toaster.Duration.LONG);
    }

    protected void showToast(String text, Toaster.Duration duration) {
        if (getActivity() == null) {
            Log.e(TAG, "showToast - getActivity() is null; aborting showing Toast");
        } else {
            Toaster.showToast(getActivity(), text, duration);
        }
    }

    protected void setActionBarTitle(int i) {
        setActionBarTitle(getResources().getString(i));
    }

    protected void setActionBarTitle(String actionBarTitle) {
        ((BaseFragmentResponder) getActivity()).setActionBarTitle(actionBarTitle);
    }

    protected void setActionBarTitle(CharSequence actionBarTitle) {
        ((BaseFragmentResponder) getActivity()).setActionBarTitle(actionBarTitle);
    }

    public boolean onFragmentBackPressed() {
        return false;
    }

    public static abstract interface BaseFragmentResponder {
        public abstract void showFragment(BaseFragment fragment, String fragmentTag, int fragmentContainer);

        public abstract void popFragment(BaseFragment fragment);

        public abstract void popBackStackByFragmentTag(String fragmentTag);

        public abstract void setActionBarTitle(int i);

        public abstract void setActionBarTitle(String actionBarTitle);

        public abstract void setActionBarTitle(CharSequence actionBarTitle);

    }
}
