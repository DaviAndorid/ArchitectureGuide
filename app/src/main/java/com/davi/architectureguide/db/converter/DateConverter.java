
package com.davi.architectureguide.db.converter;

import java.util.Date;

import androidx.room.TypeConverter;

/***
 * --------------
 * TypeConverter
 * --------------
 * 1）Room 提供了在基元类型和盒装类型之间进行转换的功能，但不允许在实体之间进行对象引用
 * 2）有时，您的应用需要使用自定义数据类型，其中包含您想要存储到单个数据库列中的值
 * 2.1）如需保留 Date 的实例
 *  - 编写以下 TypeConverter 将等效的 Unix 时间戳存储在数据库中：
 * 3）接下来，将 @TypeConverters 注释添加到 AppDatabase 类中，以便 Room 可以使用您为该 AppDatabase 中的每个实体和 DAO 定义的转换器
 *   - 具体见《AppDatabase类》
 * */
public class DateConverter {

    //由于 Room 已经知道如何保留 Long 对象，因此可以使用此转换器保留 Date 类型的值

    //另一个用于执行从 Long 到 Date 的反向转换
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }


    //一个用于将 Date 对象转换为 Long 对象
    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
