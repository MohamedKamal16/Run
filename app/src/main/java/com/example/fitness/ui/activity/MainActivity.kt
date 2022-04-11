package com.example.fitness.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.fitness.R
import com.example.fitness.databinding.ActivityMainBinding
import com.example.fitness.util.Constant.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
     lateinit var binding:ActivityMainBinding
    private lateinit var  navHostFragment:NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navigateToTrackingFragmentIfNeeded(intent)
        layout()

    }

    private fun layout() {
        with(binding) {
            setSupportActionBar(toolbar)
            bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
            //todo solve
            bottomNavigationView.setOnNavigationItemReselectedListener { /*NO-OP*/ }
            navHostFragment.findNavController()
                .addOnDestinationChangedListener { _, destination, _ ->
                    when(destination.id) {
                        R.id.settingFragment, R.id.runFragment, R.id.statisticsFragment ->
                            bottomNavigationView.visibility = View.VISIBLE
                        else -> bottomNavigationView.visibility = View.GONE
                    }
                }
        }
    }
    private fun navigateToTrackingFragmentIfNeeded(intent: Intent?){
        if (intent?.action==ACTION_SHOW_TRACKING_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_trackingFragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToTrackingFragmentIfNeeded(intent)
    }
}
