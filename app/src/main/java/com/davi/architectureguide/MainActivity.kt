package com.davi.architectureguide

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.davi.architectureguide.model.Product
import com.davi.architectureguide.ui.ProductFragment
import com.davi.architectureguide.ui.ProductListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        // Add product list fragment if this is first creation
        if (savedInstanceState == null) {
            val fragment = ProductListFragment()
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, fragment, ProductListFragment.TAG).commit()
        }
    }

    /** Shows the product detail fragment  */
    fun show(product: Product) {
        val productFragment: ProductFragment = ProductFragment.forProduct(product.getId())
        supportFragmentManager
            .beginTransaction()
            .addToBackStack("product")
            .replace(
                R.id.fragment_container,
                productFragment, null
            ).commit()
    }
}