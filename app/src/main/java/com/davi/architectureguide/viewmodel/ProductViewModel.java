package com.davi.architectureguide.viewmodel;

import android.app.Application;

import com.davi.architectureguide.BasicApp;
import com.davi.architectureguide.DataRepository;
import com.davi.architectureguide.db.entity.CommentEntity;
import com.davi.architectureguide.db.entity.ProductEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


/**
 * **********
 * 【概念介绍】
 * **********
 * 1）一般的经验法则：不要在 ViewModels 中出现 《android.* imports》
 * A general rule of thumb is to make sure there are no android.* imports in your ViewModels
 * (with exceptions like android.arch.*).
 * 这样提高了可测试性、泄漏安全性和模块化。
 * <p>
 * 2）业务（如：条件语句、循环等）应该在ViewModel或者其他层，不应该在Activities/Fragments等，
 * 保证Activities/Fragments代码越少越好，只负责如何显示数据并将用户事件发送到 ViewModel等
 * 该模式叫Passive View pattern（被动视图模式）
 * <p>
 * 3）ViewModel 处于活动状态并正在运行时，Activity 可能处于其任何生命周期状态。
 * 所以，ViewModel不能引用Activities/Fragments等试图，如果这么做了会导致严重后果，
 * 比如：ViewModel 从网络请求数据，并且数据在一段时间后返回，在那一刻，View 引用可能会被破坏，
 * 或者可能是一个不再可见的旧活动，从而产生内存泄漏，并可能导致崩溃
 * <p>
 * 推荐的在 ViewModel 和 Views 之间进行通信的方式是观察者模式，使用 LiveData 或来自其他库的 observables。
 * <p>
 * <p>
 * **********
 * 【观察者模式】
 * **********
 * 1）View（活动或片段）观察（订阅）ViewModel 中的更改
 * 2）ViewModel 会在配置更改时持久化，因此在发生轮换时无需重新查询外部数据源（例如数据库或网络）。
 * 3）与其将数据推送到 UI，不如让 UI 观察它的变化。
 * <p>
 * <p>
 * ********************
 * 【AndroidViewModel】
 * ********************
 * 使用ViewModel的时候，不能将任何含有Context引用的对象传入ViewModel，
 * 因为这可能会导致内存泄露。但如果你希望在 ViewModel 中使用Context怎么办呢？
 * 我们可以使用AndroidViewModel类，它继承自ViewModel，
 * 并且接收Application作为Context，既然是Application作为Context，也就意味着，
 * 我们能够明确它的生命周期和Application是一样的，这就不算是一个内存泄露了。
 *
 *
 * ******************************
 * 【Presenter 和 ViewModel 区别】
 * ******************************
 * 1）Presenter
 * 通过接口(interface)持有对View的引用
 * 计算新数据时，由他负责在 View /接口(interface)上调用正确的方法来更新UI。
 *
 * 2）ViewModel
 * ViewModel“仅公开”数据(通常通过LiveData或Rx)，因此可以对其进行观察；
 * 它不负责由谁观察数据以及如何处理数据；
 * View 会在ViewModel中观察到所述数据，并在数据更改时更新其UI。
 *
 *
 * ******************************
 * LiveData
 * ******************************
 * 【概念】
 * 1）一种可观察的数据存储器类
 * 2）具有生命周期感知能力，意指它遵循其他应用组件（如 Activity、Fragment 或 Service）的生命周期
 * 3）这种感知能力可确保 LiveData "仅更新" 处于活跃生命周期状态的应用组件观察者。
 * 【好处】
 * 1）确保界面符合数据状态
 * LiveData 遵循观察者模式。当底层数据发生变化时，LiveData 会通知 Observer 对象
 * 2）不会发生内存泄漏
 * 观察者会绑定到 Lifecycle 对象，并在其关联的生命周期遭到销毁后进行自我清理。
 * 3）不会因 Activity 停止而导致崩溃
 * 如果观察者的生命周期处于非活跃状态（如返回栈中的 Activity），则它不会接收任何 LiveData 事件。
 * 4）不再需要手动处理生命周期
 * 界面组件只是观察相关数据，不会停止或恢复观察。LiveData 将自动管理所有这些操作，因为它在观察时可以感知相关的生命周期状态变化。
 * 5）数据始终保持最新状态
 * 如果生命周期变为非活跃状态，它会在再次变为活跃状态时接收最新的数据。例如，曾经在后台的 Activity 会在返回前台后立即接收最新的数据。
 * 6）适当的配置更改
 * 如果由于配置更改（如设备旋转）而重新创建了 Activity 或 Fragment，它会立即接收最新的可用数据。
 * 7）共享资源
 * 您可以使用单例模式扩展 LiveData 对象以封装系统服务，以便在应用中共享它们。
 *
 */
public class ProductViewModel extends AndroidViewModel {

    /**
     * 1）LiveData 是一种可用于任何数据的封装容器，其中包括可实现 Collections 的对象，如 List
     * */
    private final LiveData<ProductEntity> mObservableProduct;

    private final LiveData<List<CommentEntity>> mObservableComments;

    private final int mProductId;

    public ProductViewModel(@NonNull Application application, DataRepository repository, final int productId) {
        super(application);

        mProductId = productId;
        mObservableComments = repository.loadComments(mProductId);
        mObservableProduct = repository.loadProduct(mProductId);
    }

    public LiveData<ProductEntity> getProduct() {
        return mObservableProduct;
    }

    /**
     * 构造 ViewModel 的工厂
     * */
    public static class Factory extends ViewModelProvider.NewInstanceFactory {

        @NonNull
        private final Application mApplication;

        private final int mProductId;

        private final DataRepository mRepository;

        public Factory(@NonNull Application application, int productId) {
            mApplication = application;
            mProductId = productId;
            mRepository = ((BasicApp) application).getRepository();
        }

        @SuppressWarnings("unchecked")
        @Override
        @NonNull
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ProductViewModel(mApplication, mRepository, mProductId);
        }
    }

    /**
     * Expose the LiveData Comments query so the UI can observe it.
     * 暴露LiveData，UI层可以监听
     */
    public LiveData<List<CommentEntity>> getComments() {
        return mObservableComments;
    }

}
