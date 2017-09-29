package com.example.fleissig.restaurantapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fleissig.mylibrary.Dish;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.order.CartItem;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.utils.StringUtils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phuctran on 6/17/17.
 */

public class MeusPedidosAdapter extends RecyclerView.Adapter<MeusPedidosAdapter.ViewHolder> {
    private static final String TAG = MeusPedidosAdapter.class.getSimpleName();

    private final LayoutInflater layoutInflater;
    private final Context context;
    private List<CartItem> pedidoItems = MyApplication.getInstance().CART.getItems();
    private OnRemoveItemClickListener onItemClickListener;
    private int lastPosition;

    public interface OnRemoveItemClickListener {
        void onRemoveItemClicked(CartItem cardItem, int position);
    }

    public MeusPedidosAdapter(Context context) {
        this.context = context;
        this.layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MeusPedidosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_meus_pedidos, parent, false);
        return new MeusPedidosAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MeusPedidosAdapter.ViewHolder holder, final int position) {
        final CartItem model = this.pedidoItems.get(position);
        holder.tvPedidoItemName.setText(model.getDish().text);
        holder.tvQuantidade.setText("Quantidade: " + model.getDishOrder().getQuantity());

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

        if (model.getInfomationExtra() != null && !model.getInfomationExtra().equals("")) {
            holder.checkComment.setVisibility(View.VISIBLE);
        } else {
            holder.checkComment.setVisibility(View.GONE);
        }

        holder.tvTotalAdjustment.setText(StringUtils.formatMoney(dPrice * model.getDishOrder().getQuantity()));
        holder.btnXClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MeusPedidosAdapter.this.onItemClickListener != null) {
                    MeusPedidosAdapter.this.onItemClickListener.onRemoveItemClicked(model,position);
                }
                lastPosition--;
            }
        });

        setAnimation(holder.itemView, position);
    }

    public void setOnRemoveItemClickListener(OnRemoveItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return (this.pedidoItems != null) ? this.pedidoItems.size() : 0;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.push_left_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvPedidoItemName)
        TextView tvPedidoItemName;
        @BindView(R.id.tvQuantidade)
        TextView tvQuantidade;
        @BindView(R.id.tvTotalAdjustment)
        TextView tvTotalAdjustment;
        @BindView(R.id.btnXClose)
        Button btnXClose;
        @BindView(R.id.checkComment)
        View checkComment;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
