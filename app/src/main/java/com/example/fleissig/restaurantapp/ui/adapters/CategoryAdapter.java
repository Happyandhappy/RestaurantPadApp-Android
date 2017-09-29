package com.example.fleissig.restaurantapp.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ChildViewHolder;
import com.bignerdranch.expandablerecyclerview.ExpandableRecyclerAdapter;
import com.bignerdranch.expandablerecyclerview.ParentViewHolder;
import com.example.fleissig.restaurantapp.R;
import com.example.fleissig.restaurantapp.ui.models.RvCategory;
import com.example.fleissig.restaurantapp.ui.models.RvSubCategory;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by phuctran on 5/19/17.
 */

public class CategoryAdapter extends ExpandableRecyclerAdapter<RvCategory, RvSubCategory, CategoryAdapter.CategoryViewHolder, CategoryAdapter.SubCategoryViewHolder> {

    private final List<RvCategory> mCategory;
    private final Context mContext;
    private LayoutInflater mInflater;
    private CategoryAdapter.OnItemClickListener onItemClickListener;
    private int selectedItem = 0;

    public interface OnItemClickListener {
        void onSubCategoryItemClicked(RvSubCategory dishModel);

        void onCategoryItemClicked(RvCategory categoryModel);
    }

    public CategoryAdapter(Context context, List<RvCategory> subcategory) {
        super(subcategory);
        this.mCategory = subcategory;
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
    }

    @Override
    public CategoryViewHolder onCreateParentViewHolder(@NonNull ViewGroup parentViewGroup, int viewType) {
        View recipeView = mInflater.inflate(R.layout.item_category, parentViewGroup, false);
        return new CategoryViewHolder(recipeView);
    }

    @Override
    public SubCategoryViewHolder onCreateChildViewHolder(@NonNull ViewGroup childViewGroup, int viewType) {
        View ingredientView = mInflater.inflate(R.layout.item_subcategory, childViewGroup, false);
        return new SubCategoryViewHolder(ingredientView);
    }

    @Override
    public void onBindParentViewHolder(@NonNull CategoryViewHolder recipeViewHolder, final int parentPosition, @NonNull final RvCategory recipe) {
        recipeViewHolder.bind(recipe, parentPosition);
        if (recipe.subcategories == null || recipe.subcategories.size() == 0) {
            recipeViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (CategoryAdapter.this.onItemClickListener != null && recipe.dishes.size() > 0) {
                        CategoryAdapter.this.onItemClickListener.onCategoryItemClicked(recipe);
                        notifySelectedItemChanged(parentPosition);
                    }
                }
            });
        }
    }

    @Override
    public void onBindChildViewHolder(@NonNull SubCategoryViewHolder ingredientViewHolder, int parentPosition, int childPosition, @NonNull final RvSubCategory ingredient) {
        ingredientViewHolder.bind(ingredient);
        ingredientViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CategoryAdapter.this.onItemClickListener != null) {
                    CategoryAdapter.this.onItemClickListener.onSubCategoryItemClicked(ingredient);
                }
            }
        });
    }

    public void notifySelectedItemChanged(int selectedItem) {
        notifyItemChanged(this.selectedItem);
        this.selectedItem = selectedItem;
        notifyItemChanged(this.selectedItem);
    }

    public void setOnItemClickListener(CategoryAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class CategoryViewHolder extends ParentViewHolder {

        @BindView(R.id.category_text)
        TextView tvCategory;
        @BindView(R.id.category_root)
        View vCategoryRoot;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(RvCategory recipe, int parentPosition) {
            tvCategory.setText(recipe.text);
            if(selectedItem==parentPosition){
                vCategoryRoot.setBackgroundColor(ContextCompat.getColor(mContext,R.color.prune_darker));
            }else{
                vCategoryRoot.setBackgroundColor(ContextCompat.getColor(mContext,R.color.prune));
            }

        }
    }

    public class SubCategoryViewHolder extends ChildViewHolder {

        @BindView(R.id.category_text)
        TextView tvSubCategory;

        public SubCategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

        }

        public void bind(RvSubCategory recipe) {
            tvSubCategory.setText(recipe.text);
        }
    }

}
