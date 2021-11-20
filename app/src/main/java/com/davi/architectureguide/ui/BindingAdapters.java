
package com.davi.architectureguide.ui;

import android.view.View;

import androidx.databinding.BindingAdapter;

/**
 *
 * https://blog.csdn.net/lixpjita39/article/details/79054052
 *
 * https://developer.android.com/topic/libraries/data-binding/binding-adapters
 *
 * 自定义 绑定适配器
 * 【作用】
 * 使用databinding的时候，会发现有几个场景：
 * （1）属性在类中没有对应的setter，如ImageView的android:src，ImageView中没有setSrc()方法，
 * （2）属性在类中有setter，但是接收的参数不是自己想要的，如android:background属性，对应的setter是setBackgound(drawable)，
 * 但是我想传一个int类型的id进去，这时候android:background = “@{imageId}”就不行。
 * （3）没有对应的属性，但是却要实现相应的功能
 * 《@BindingAdapter》 来定义方法，解决上面的问题。
 *
 * 【使用注意】
 * （1）作用于方法
 * （2）它定义了xml的属性赋值的java实现
 * （3）方法必须为公共静（public static）方法，可以有一到多个参数。
 *
 * */
public class BindingAdapters {
    @BindingAdapter("visibleGone")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}