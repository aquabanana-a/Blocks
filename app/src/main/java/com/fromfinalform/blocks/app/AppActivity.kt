package com.fromfinalform.blocks.app

import android.os.Bundle
import com.fromfinalform.blocks.R
import android.content.Context
import android.content.Intent
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.fromfinalform.blocks.core.locale.LocaleUtils

class AppActivity : AppCompatActivity() {

    private var toolbar: Toolbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        val navController = supportFragmentManager.findFragmentById(R.id.host_global)?.findNavController()
            ?: throw IllegalStateException("Global navigation host controller is not set!")



        subscribe()
        processIntent(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        processIntent(intent)
    }

    private fun processIntent(intent: Intent?) {
    }

    private fun subscribe() {
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleUtils.wrapActivityContext(newBase))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //menuInflater.inflate(R.menu.main, menu)
        LocaleUtils.translateToolbar(resources, toolbar)
        return true
    }
}