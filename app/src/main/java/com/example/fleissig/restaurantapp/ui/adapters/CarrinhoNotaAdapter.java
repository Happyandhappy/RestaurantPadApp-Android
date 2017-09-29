package com.example.fleissig.restaurantapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.models.RvNota;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phuctran on 5/26/17.
 */

public class CarrinhoNotaAdapter extends RecyclerView.Adapter<CarrinhoNotaAdapter.ViewHolder> {
    private static final String TAG = CarrinhoNotaAdapter.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private List<CartItem> vehicleCollection;
    private Context mContext;

    public CarrinhoNotaAdapter(Context context) {
        this.mContext = context;
        this.layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vehicleCollection = Collections.emptyList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_carrinho_nota, parent, false);
        return new CarrinhoNotaAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CartItem model = this.vehicleCollection.get(position);
        holder.tvPedidoItemName.setText(model.getDish().text);
        holder.tvQuantityAdjustable.setText(model.getOrderedQuantity() + "");
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
        holder.rbRatingStar.setRating(model.getRatingStar());
        holder.rbRatingStar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                model.setRatingStar(Math.round(model.getRatingStar()));
            }
        });
    }

    public void setRecycleViewCollection(List<CartItem> fleetCollection) {
        this.vehicleCollection = (List<CartItem>) fleetCollection;
        this.notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return (this.vehicleCollection != null) ? this.vehicleCollection.size() : 0;
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
        @BindView(R.id.rating_ratingbar)
        RatingBar rbRatingStar;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}