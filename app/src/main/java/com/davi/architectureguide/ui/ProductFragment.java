package com.davi.architectureguide.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.davi.architectureguide.viewmodel.ProductViewModel;
import com.davi.architectureguide.R;
import com.davi.architectureguide.databinding.ProductFragmentBinding;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/***
 *
 * ************
 * DataBinding
 * ************
 * 1）数据绑定库是一种支持库
 * 2）将布局中的界面组件绑定到应用中的数据源。
 * 3）例子：<TextView android:text="@{viewmodel.userName}" />， @{} 语法
 *
 * */
public class ProductFragment extends Fragment {

    private ProductFragmentBinding mBinding;

    private final CommentClickCallback mCommentClickCallback = comment -> {
        // no-op
    };

    private CommentAdapter mCommentAdapter;

    private static final String KEY_PRODUCT_ID = "product_id";

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater,
                             @Nullable @org.jetbrains.annotations.Nullable ViewGroup container,
                             @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.product_fragment, container, false);
        mCommentAdapter = new CommentAdapter(mCommentClickCallback);
        mBinding.commentList.setAdapter(mCommentAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view,
                              @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ProductViewModel.Factory factory = new ProductViewModel.Factory(requireActivity().getApplication(), requireArguments().getInt(KEY_PRODUCT_ID));
        //ViewModelProvider
        //在获取ViewModel的时候绝对不能直接使用new关键字去创建，需要使用 ViewModelProviders 去使用系统提供的反射方法去创建我们想要的ViewModel
        final ProductViewModel model = new ViewModelProvider(this, factory).get(ProductViewModel.class);

        //生命周期监听
        mBinding.setLifecycleOwner(getViewLifecycleOwner());
        //ViewModel设置
        mBinding.setProductViewModel(model);

        subscribeToModel(model);
    }

    //观察 LiveData 对象，LiveData<List<CommentEntity>>
    private void subscribeToModel(final ProductViewModel model) {
        // Observe comments
        model.getComments().observe(getViewLifecycleOwner(), commentEntities -> {
            if (commentEntities != null) {
                mBinding.setIsLoading(false);
                mCommentAdapter.submitList(commentEntities);
            } else {
                mBinding.setIsLoading(true);
            }
        });
    }

    @Override
    public void onDestroyView() {
        mBinding = null;
        mCommentAdapter = null;
        super.onDestroyView();
    }

    public static ProductFragment forProduct(int productId) {
        ProductFragment fragment = new ProductFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_PRODUCT_ID, productId);
        fragment.setArguments(args);
        return fragment;
    }

}
