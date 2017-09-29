package com.example.fleissig.restaurantapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phuctran on 6/17/17.
 */

public class MinhaContaAdapter extends RecyclerView.Adapter<MinhaContaAdapter.ViewHolder> {
    private static final String TAG = MinhaContaAdapter.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private List<CartItem> pedidoItems = MyApplication.getInstance().CART.getOrderedItems();

    public MinhaContaAdapter(Context context) {
        this.layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MinhaContaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_minha_conta, parent, false);
        return new MinhaContaAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MinhaContaAdapter.ViewHolder holder, final int position) {
        final CartItem model = this.pedidoItems.get(position);
        holder.tvPedidoItemName.setText(model.getDish().text);
        holder.tvQuantidade.setText("Quantidade: " + model.getOrderedQuantity());

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

        holder.tvTotalAdjustment.setText(StringUtils.formatMoney(dPrice * model.getOrderedQuantity()));
    }

    @Override
    public int getItemCount() {
        return (this.pedidoItems != null) ? this.pedidoItems.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvPedidoItemName)
        TextView tvPedidoItemName;
        @BindView(R.id.tvQuantidade)
        TextView tvQuantidade;
        @BindView(R.id.tvTotalAdjustment)
        TextView tvTotalAdjustment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
