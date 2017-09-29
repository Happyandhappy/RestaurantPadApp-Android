package com.example.fleissig.restaurantapp.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.fleissig.restaurantapp.MyApplication;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.models.RvDish;
import com.example.fleissig.restaurantapp.utils.PicassoUtils;
import com.example.fleissig.restaurantapp.utils.StringUtils;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by PhucTV on 6/25/16.
 */
public class DishAdapter extends RecyclerView.Adapter<DishAdapter.ViewHolder> {
    private static final String TAG = DishAdapter.class.getSimpleName();

    public interface OnItemClickListener {
        void onDishItemClicked(RvDish fleetModel);
        void onAddDishItemClicked(RvDish fleetModel);
    }

    private OnItemClickListener onItemClickListener;

    private final LayoutInflater layoutInflater;
    private List<RvDish> vehicleCollection;
    private Context mContext;

    public DishAdapter(Context context) {
        this.mContext = context;
        this.layoutInflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.vehicleCollection = Collections.emptyList();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = layoutInflater.inflate(R.layout.item_dish, parent, false);
        return new DishAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final RvDish model = this.vehicleCollection.get(position);
        holder.tvDishName.setText(model.text);
        holder.tvDishDescription.setText("");
        if (model.image_url != null && !model.image_url.equals("")) {
            holder.ivDishIcon.setVisibility(View.VISIBLE);

            FirebaseStorage mStorage = FirebaseStorage.getInstance();
            StorageReference mImageRef = null;

            String url = model.image_url;
            if (url != null) mImageRef = mStorage.getReferenceFromUrl(url);
            Glide.with(MyApplication.getInstance())
                    .using(new FirebaseImageLoader())
                    .load(mImageRef)
                    .error(R.drawable.no_image)
                    .into(holder.ivDishIcon);
        } else {
            holder.ivDishIcon.setVisibility(View.GONE);
        }

        holder.tvPrice.setText(StringUtils.formatMoney(model.price));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DishAdapter.this.onItemClickListener != null) {
                    DishAdapter.this.onItemClickListener.onDishItemClicked(model);
                }
            }
        });
        holder.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DishAdapter.this.onItemClickListener != null) {
                    DishAdapter.this.onItemClickListener.onAddDishItemClicked(model);
                }
            }
        });
    }

    public void setRecycleViewCollection(Collection<RvDish> fleetCollection) {
        this.vehicleCollection = (List<RvDish>) fleetCollection;
        this.notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemCount() {
        return (this.vehicleCollection != null) ? this.vehicleCollection.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btnAdd)
        Button btnAdd;
        @BindView(R.id.dish_name)
        TextView tvDishName;
        @BindView(R.id.ivDishIcon)
        ImageView ivDishIcon;
        @BindView(R.id.tvDishDescription)
        TextView tvDishDescription;
        @BindView(R.id.tvPrice)
        TextView tvPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
