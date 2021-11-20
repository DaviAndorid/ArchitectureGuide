package com.davi.architectureguide.db;

import android.content.Context;

import com.davi.architectureguide.AppExecutors;
import com.davi.architectureguide.DataGenerator;
import com.davi.architectureguide.db.converter.DateConverter;
import com.davi.architectureguide.db.dao.CommentDao;
import com.davi.architectureguide.db.dao.ProductDao;
import com.davi.architectureguide.db.entity.CommentEntity;
import com.davi.architectureguide.db.entity.ProductEntity;
import com.davi.architectureguide.db.entity.ProductFtsEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;


@Database(entities = {ProductEntity.class, ProductFtsEntity.class, CommentEntity.class}, version = 2)

@TypeConverters(DateConverter.class)
//将 @TypeConverters 注释添加到 AppDatabase 类中，以便 Room 可以使用您为该 AppDatabase 中的每个实体和 DAO 定义的转换器
//通过使用这些转换器，您就可以在其他查询中使用自定义类型，就像使用基元类型一样
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase sInstance;

    @VisibleForTesting
    public static final String DATABASE_NAME = "basic-sample-db";

    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    public abstract ProductDao productDao();

    public abstract CommentDao commentDao();

    /***
     * 地址：
     * https://developer.android.google.cn/training/data-storage/room/migrating-db-versions?hl=zh-cn
     *
     * 内容：
     * 1）如果应用更新更改了数据库架构，那么保留设备内置数据库中已有的用户数据就非常重要
     * 2）Room 持久性库支持通过 Migration 类进行增量迁移以满足此需求
     * 3）Migration.migrate() 方法定义 startVersion 和 endVersion 之间的迁移路径。
     * 当应用更新需要升级数据库版本时，Room 会从一个或多个 Migration 子类运行 migrate() 方法，以在运行时将数据库迁移到最新版本
     *
     * 地址-迁移demo：
     * https://github.com/android/architecture-components-samples/tree/master/PersistenceMigrationsSample
     * */
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE VIRTUAL TABLE IF NOT EXISTS `productsFts` USING FTS4("
                    + "`name` TEXT, `description` TEXT, content=`products`)");
            database.execSQL("INSERT INTO productsFts (`rowid`, `name`, `description`) "
                    + "SELECT `id`, `name`, `description` FROM products");

        }
    };

    public static AppDatabase getInstance(final Context context, final AppExecutors executors) {
        if (sInstance == null) {
            synchronized (AppDatabase.class) {
                if (sInstance == null) {
                    //每次创建AppDatabase实例都会产生比较大的开销，所以应该将AppDatabase设计成单例的
                    sInstance = buildDatabase(context.getApplicationContext(), executors);
                    sInstance.updateDatabaseCreated(context.getApplicationContext());
                }
            }
        }
        return sInstance;
    }

    private static AppDatabase buildDatabase(final Context appContext, final AppExecutors executors) {
        return Room.databaseBuilder(appContext, AppDatabase.class, DATABASE_NAME)
                .addCallback(new Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);

                        executors.diskIO().execute(() -> {
                            //添加延迟以模拟长时间运行的操作
                            addDelay();

                            //生成预填充数据
                            AppDatabase database = AppDatabase.getInstance(appContext, executors);
                            List<ProductEntity> products = DataGenerator.generateProducts();
                            List<CommentEntity> comments = DataGenerator.generateCommentsForProducts(products);

                            insertData(database, products, comments);

                            // notify that the database was created and it's ready to be used
                            database.setDatabaseCreated();
                        });
                    }
                })
                .addMigrations(MIGRATION_1_2)//数据库迁移的时候用
                .build();
    }

    private static void addDelay() {
        try {
            Thread.sleep(4000);
        } catch (InterruptedException ignored) {
        }
    }

    private static void insertData(final AppDatabase database, final List<ProductEntity> products,
                                   final List<CommentEntity> comments) {
        database.runInTransaction(() -> {
            database.productDao().insertAll(products);
            database.commentDao().insertAll(comments);
        });
    }

    /**
     * 检查数据库是否已经存在并通过
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated(){
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }

}
