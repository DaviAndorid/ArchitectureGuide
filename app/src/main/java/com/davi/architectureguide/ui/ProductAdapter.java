package com.davi.architectureguide.ui;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.davi.architectureguide.R;
import com.davi.architectureguide.databinding.ProductItemBinding;
import com.davi.architectureguide.model.Product;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    List<? extends Product> mProductList;

    private final ProductClickCallback mProductClickCallback;

    public ProductAdapter(@Nullable ProductClickCallback clickCallback) {
        mProductClickCallback = clickCallback;
        setHasStableIds(true);
    }

    public void setProductList(final List<? extends Product> productList) {
        if (mProductList == null) {
            //数据为空
            mProductList = productList;
            notifyItemRangeInserted(0, productList.size());
        }else {
            /**
             * 基础知识
             * 1）DiffUtil是recyclerview support library v7 24.2.0版本中新增的类，
             * 根据Google官方文档的介绍，DiffUtil的作用是比较两个数据列表并能计算出一系列将旧数据表转换成新数据表的操作
             * 2）相比直接调用adapter.notifyDataSetChange()方法，它能在收到数据集后，提高UI更新的效率，而且你也不需要自己对新老数据集进行比较了。
             * */
            //数据发生变化
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mProductList.size();
                }

                @Override
                public int getNewListSize() {
                    return productList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mProductList.get(oldItemPosition).getId() ==
                            productList.get(newItemPosition).getId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Product newProduct = productList.get(newItemPosition);
                    Product oldProduct = mProductList.get(oldItemPosition);
                    return newProduct.getId() == oldProduct.getId()
                            && TextUtils.equals(newProduct.getDescription(), oldProduct.getDescription())
                            && TextUtils.equals(newProduct.getName(), oldProduct.getName())
                            && newProduct.getPrice() == oldProduct.getPrice();
                }
            });
            mProductList = productList;
            /**
             * 1）dispatchUpdatesTo()方法它会自动计算新老数据集的差异，并根据差异情况，自动调用以下四个方法
             * - adapter.notifyItemRangeInserted(position, count);
             * - adapter.notifyItemRangeRemoved(position, count);
             * - adapter.notifyItemMoved(fromPosition, toPosition);
             * - adapter.notifyItemRangeChanged(position, count, payload);
             * */
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @NotNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ProductItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.product_item,
                        parent, false);
        //设置数据
        binding.setCallback(mProductClickCallback);
        return new ProductViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductAdapter.ProductViewHolder holder, int position) {
        //设置数据
        holder.binding.setProduct(mProductList.get(position));
        //立即刷新UI
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mProductList == null ? 0 : mProductList.size();
    }

    @Override
    public long getItemId(int position) {
        return mProductList.get(position).getId();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {

        final ProductItemBinding binding;

        public ProductViewHolder(ProductItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
