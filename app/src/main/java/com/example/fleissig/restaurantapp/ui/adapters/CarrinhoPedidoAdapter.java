package com.example.fleissig.restaurantapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.fragments.CarrinhoPedidoFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phuctran on 5/26/17.
 */

public class CarrinhoPedidoAdapter extends RecyclerView.Adapter<CarrinhoPedidoAdapter.ViewHolder> {
    private static final String TAG = CarrinhoPedidoAdapter.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private final CarrinhoPedidoFragment carrinhoPedidoFragment;
    private List<CartItem> pedidoItems;
    private Context mContext;

    public CarrinhoPedidoAdapter(Context context, CarrinhoPedidoFragment carrinhoPedidoFragment) {
        this.mContext = context;
        this.carrinhoPedidoFragment = carrinhoPedidoFragment;
        this.layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.pedidoItems = Collections.emptyList();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_carrinho_pedido, parent, false);
        return new CarrinhoPedidoAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CartItem model = this.pedidoItems.get(position);
        holder.tvPedidoItemName.setText(model.getDish().text);
        holder.tvQuantityAdjustable.setText(model.getDishOrder().getQuantity() + "");
        if (model.getDish().price == null || model.getDish().price == 0) {
            if (model.getDish().price_bottle != null) {
                holder.tvPedidoPrice.setText(model.getDish().price_bottle + "");
            }
        } else {
            holder.tvPedidoPrice.setText(model.getDish().price + "");
        }

        final int iQuantity = model.getDishOrder().getQuantity();
        final double dPrice;
        if (model.getDish().price == null || model.getDish().price == 0) {
            if (model.getDish().price_bottle != null) {
                dPrice = model.getDish().price_bottle;
            } else {
                dPrice = 0.0;
            }
        } else {
            dPrice = model.getDish().price;
        }

        holder.tvPedidoPrice.setText(dPrice + "");
        holder.tvTotalAdjustment.setText("Total: " + iQuantity * dPrice);
        holder.etInfomationExtras.setText(model.getInfomationExtra() == null ? "" : model.getInfomationExtra());
        holder.etInfomationExtras.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                model.setInfomationExtra(editable.toString());
            }
        });
        holder.btnMinusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int iQuantity = model.getDishOrder().getQuantity() - 1;

                model.getDishOrder().setQuantity(iQuantity);
                holder.tvTotalAdjustment.setText("Total: " + iQuantity * dPrice);
                holder.tvQuantityAdjustable.setText(iQuantity + "");
                if (iQuantity < 1) {
                    for (int i = 0; i < MyApplication.getInstance().CART.getItems().size(); i++) {
                        CartItem ci = MyApplication.getInstance().CART.getItems().get(i);
                        if (ci.getDishId().equals(model.getDishId())) {
                            if (ci.getOrderedQuantity() < 1) {
                                MyApplication.getInstance().CART.getItems().remove(i);
                            }

                            List<CartItem> originalItems = MyApplication.getInstance().CART.getItems();
                            List<CartItem> itemToShow = new ArrayList<>();
                            for (CartItem ciorig : originalItems) {
                                if (ciorig.getDishOrder().getQuantity() > 0) {
                                    itemToShow.add(ciorig);
                                }
                            }

                            CarrinhoPedidoAdapter.this.setRecycleViewCollection(itemToShow);
                            return;
                        }
                    }
                }
                carrinhoPedidoFragment.renderTotalPedido();
            }
        });

        holder.btnPlusItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int iQuantity = model.getDishOrder().getQuantity();
                iQuantity++;

                model.getDishOrder().setQuantity(iQuantity);
                holder.tvTotalAdjustment.setText("Total: " + iQuantity * dPrice);
                holder.tvQuantityAdjustable.setText(iQuantity + "");
                carrinhoPedidoFragment.renderTotalPedido();
            }
        });
    }

    public void setRecycleViewCollection(List<CartItem> fleetCollection) {
        this.pedidoItems = (List<CartItem>) fleetCollection;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (this.pedidoItems != null) ? this.pedidoItems.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvPedidoItemName)
        TextView tvPedidoItemName;
        @BindView(R.id.tvPedidoPrice)
        TextView tvPedidoPrice;
        @BindView(R.id.tvQuantityAdjustable)
        TextView tvQuantityAdjustable;
        @BindView(R.id.tvTotalAdjustment)
        TextView tvTotalAdjustment;
        @BindView(R.id.btnPlusItem)
        Button btnPlusItem;
        @BindView(R.id.btnMinusItem)
        Button btnMinusItem;
        @BindView(R.id.etInfomationExtras)
        EditText etInfomationExtras;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}