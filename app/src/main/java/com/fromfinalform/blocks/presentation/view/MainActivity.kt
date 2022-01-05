package com.fromfinalform.blocks.presentation.view

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.NavHostFragment
import com.fromfinalform.blocks.R
import com.fromfinalform.blocks.presentation.presenter.MainPresenter
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class MainActivity : MvpAppCompatActivity(), MainPresenter.MainView {

    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fg_nav_host) as NavHostFragment
        val navController = navHostFragment.navController


    }
}