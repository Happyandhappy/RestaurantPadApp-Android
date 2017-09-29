package com.example.fleissig.restaurantserver;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.mylibrary.FirebaseConstants;
import com.firebase.ui.database.FirebaseIndexListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RankPageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RankPageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankPageFragment extends Fragment {
    private final static String TAG = RankPageFragment.class.getSimpleName();

    //    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private DatabaseReference mRef;
    private FirebaseIndexListAdapter<Dish> mAdapter;
    private ListView mRankListView;

    public RankPageFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment RankPageFragment.
//     */
    public static RankPageFragment newInstance() {
        RankPageFragment fragment = new RankPageFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
    }

    @Override
    public void onDestroy() {
        mAdapter.cleanup();
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflate = inflater.inflate(R.layout.fragment_rank_page, container, false);
        mRankListView = (ListView) inflate.findViewById(R.id.rank_listview);

        mRef = FirebaseDatabase.getInstance().getReference();

        setAdapter(false);
        Button sortByNameButton = (Button) inflate.findViewById(R.id.sort_by_name_button);
        sortByNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAdapter(false);
            }
        });
        Button sortByRatioButton = (Button) inflate.findViewById(R.id.sort_by_ratio_button);
        sortByRatioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAdapter(true);
            }
        });
        return inflate;
    }

    private void setAdapter(boolean byRatio) {
        if(mAdapter != null) mAdapter.notifyDataSetInvalidated();
        final DatabaseReference ratingRef = mRef.child(FirebaseConstants.RATING_KEY);
        DatabaseReference dishesRef = mRef.child(FirebaseConstants.DISHES_KEY);
        mAdapter = new FirebaseIndexListAdapter<Dish>(getActivity(), Dish.class,
                android.R.layout.two_line_list_item,
                byRatio? ratingRef.orderByChild(FirebaseConstants.AVERAGE_KEY) : ratingRef,
                byRatio? dishesRef : dishesRef.orderByChild(FirebaseConstants.DISH_TEXT_KEY)) {

            @Override
            protected void populateView(final View v, final Dish dish, int position) {
                ((TextView) v.findViewById(android.R.id.text1)).setText(dish.text);
                String dishKey = getRef(position).getKey();
                ratingRef.child(dishKey)
                        .child(FirebaseConstants.AVERAGE_KEY)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot != null) {
                                    ((TextView) v.findViewById(android.R.id.text2))
                                            .setText(String.valueOf(dataSnapshot.getValue()));
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.getMessage());
                            }
                        });
            }
        };
        mRankListView.setAdapter(mAdapter);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
