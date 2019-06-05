package com.template.drugsreminder.main

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.BottomNavigationView
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.template.drugsreminder.R
import com.template.drugsreminder.base.NavHost
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity() : AppCompatActivity(), NavHost {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(this, R.id.content)

        bottomNavigation.setupWithNavController(navController)
    }

    override fun getNavController(): NavController {
        return navController
    }

    override fun setBottomBarVisibility(isVisible: Boolean) {
        bottomNavigation.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

}
