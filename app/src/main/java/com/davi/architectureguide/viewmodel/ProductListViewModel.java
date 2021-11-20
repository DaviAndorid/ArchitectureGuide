package com.davi.architectureguide.viewmodel;

import android.app.Application;
import android.text.TextUtils;

import com.davi.architectureguide.BasicApp;
import com.davi.architectureguide.DataRepository;
import com.davi.architectureguide.db.entity.ProductEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;

public class ProductListViewModel extends AndroidViewModel {

    private static final String QUERY_KEY = "QUERY";

    private final SavedStateHandle mSavedStateHandler;

    private final DataRepository mRepository;

    private final LiveData<List<ProductEntity>> mProducts;

    public ProductListViewModel(@NonNull Application application,
                                @NonNull SavedStateHandle savedStateHandle) {
        super(application);

        mSavedStateHandler = savedStateHandle;

        mRepository = ((BasicApp) application).getRepository();
        /**
         *（1）先查存储的 数据
         *（2）如果存在，那么searchProducts
         *（3）如果不存在，那么getProducts
         * */
        mProducts = Transformations.switchMap(
                savedStateHandle.getLiveData("QUERY", null),
                (Function<CharSequence, LiveData<List<ProductEntity>>>) query -> {
                    if (TextUtils.isEmpty(query)) {
                        return mRepository.getProducts();
                    }
                    return mRepository.searchProducts("*" + query + "*");
                });
    }

    /***
     * 持久化
     * */
    public void setQuery(CharSequence query) {
        // Save the user's query into the SavedStateHandle.
        // This ensures that we retain the value across process death
        // and is used as the input into the Transformations.switchMap above
        mSavedStateHandler.set(QUERY_KEY, query);
    }

    /**
     * Expose the LiveData Products query so the UI can observe it.
     */
    public LiveData<List<ProductEntity>> getProducts() {
        return mProducts;
    }

}
