package com.davi.architectureguide;


import com.davi.architectureguide.db.AppDatabase;
import com.davi.architectureguide.db.entity.CommentEntity;
import com.davi.architectureguide.db.entity.ProductEntity;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

/**
 * Repository handling the work with products and comments.
 */
public class DataRepository {

    private static DataRepository sInstance;

    private final AppDatabase mDatabase;
    /***
     * ----------------
     * MediatorLiveData
     * ----------------
     * 地址：https://juejin.cn/post/6945419430176227359（树的模型监听）
     *（1）通过addSource方法，可以监听另一个LiveData的数据变化
     *（2）本身就是一个LiveData，可以被其他Observer观察
     *
     * */
    private MediatorLiveData<List<ProductEntity>> mObservableProducts;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();

        //监听 productDao的数据变化
        mObservableProducts.addSource(mDatabase.productDao().loadAllProducts(),
                productEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableProducts.postValue(productEntities);
                    }
                });
    }

    public static DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<ProductEntity>> getProducts() {
        return mObservableProducts;
    }

    public LiveData<ProductEntity> loadProduct(final int productId) {
        return mDatabase.productDao().loadProduct(productId);
    }

    public LiveData<List<CommentEntity>> loadComments(final int productId) {
        return mDatabase.commentDao().loadComments(productId);
    }

    public LiveData<List<ProductEntity>> searchProducts(String query) {
        return mDatabase.productDao().searchAllProducts(query);
    }
}
