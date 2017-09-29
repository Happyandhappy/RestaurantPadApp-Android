package com.example.fleissig.restaurantserver;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fleissig.mylibrary.FirebaseConstants;
import com.example.fleissig.mylibrary.Order;
import com.example.fleissig.mylibrary.ShareFunctions;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.Date;

public class OrdersFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    public static final String ORDER_KEY_EXTRA = "order_key_extra";
    private DatabaseReference mRef;
    private FirebaseListAdapter<Order> mAdapter;
    private ListView mOrdersListView;
    private String mRestaurantId;
    private EditText mTableNumberEditText;

    private void setAdapter() {
        if(mAdapter != null) mAdapter.notifyDataSetInvalidated();
        String text = mTableNumberEditText.getText().toString();
        try {
            int i = Integer.parseInt(text);
            setAdapterRoutine(i, true);
        }
        catch (NumberFormatException e) {
            setAdapterRoutine(-1, false);
        }
    }

    private void setAdapterRoutine(int tableNumber, boolean toFilter) {
        Query query = mRef.child(FirebaseConstants.ORDERS_KEY)
                .child(ShareFunctions.readRestaurantId(getContext()));
        mAdapter = new FirebaseListAdapter<Order>(getActivity(), Order.class,
                android.R.layout.two_line_list_item,
                toFilter? query.orderByChild(FirebaseConstants.TABLE_NUMBER_KEY)
                        .equalTo(tableNumber) : query
        )
        {
            @Override
            protected void populateView(View view, Order order, int position) {
                ((TextView) view.findViewById(android.R.id.text1))
                        .setText(SDF.format(new Date(order.getTimestamp())));
                ((TextView) view.findViewById(android.R.id.text2))
                        .setText(String.valueOf(order.table_number));
            }
        };
        mOrdersListView.setAdapter(mAdapter);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRestaurantId = ShareFunctions.readRestaurantId(getContext());;

        mRef = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public void onDestroy() {
        mAdapter.cleanup();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        String restaurantId = ShareFunctions.readRestaurantId(getContext());
        if (!restaurantId.equals(mRestaurantId)) {
            mRestaurantId = restaurantId;
            setAdapter();
        }
    }

    public OrdersFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static OrdersFragment newInstance() {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
//        args.putInt(ARG_SECTION_NUMBER, 1);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mTableNumberEditText = (EditText) rootView.findViewById(R.id.table_number_edittext);
        mOrdersListView = (ListView) rootView.findViewById(R.id.orders_list);

        setAdapter();

        mTableNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                setAdapter();
            }
        });

        mOrdersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Order order = (Order) adapterView.getItemAtPosition(position);
                Intent intent = new Intent(getContext(), OrderActivity.class);
                String orderKey = mAdapter.getRef(position).getKey();
                intent.putExtra(ORDER_KEY_EXTRA, orderKey);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
