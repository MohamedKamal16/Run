package com.example.fitness.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.fitness.R
import com.example.fitness.databinding.FragmentSetupBinding
import com.example.fitness.ui.activity.MainActivity
import com.example.fitness.util.Constant.KEY_FIRST_TIME_TOGGLE
import com.example.fitness.util.Constant.KEY_NAME
import com.example.fitness.util.Constant.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment : Fragment() {

    private lateinit var binding: FragmentSetupBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    @set:Inject
    var isFirstAppOpen = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            // to not open setpUpFragment again just first time
            if (!isFirstAppOpen) {
                val navOption = NavOptions.Builder()
                    .setPopUpTo(R.id.setupFragment, true)
                    .build()
                findNavController().navigate(R.id.runFragment, savedInstanceState, navOption)
            }
            ///////////////////////////////////////////////////////////////////////////////////
            tvContinue.setOnClickListener {
                val success = writePersonalDataToSharedPref()
                if (success) {
                    findNavController().navigate(R.id.runFragment)
                } else {
                    Snackbar.make(
                        requireView(),
                        "please enter all the fields",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        with(binding) {
            val name = etName.text.toString()
            val weight = etWeight.text.toString()
            if (name.isEmpty() || weight.isEmpty()) {
                return false
            }
            sharedPreferences.edit()
                .putString(KEY_NAME, name)
                .putFloat(KEY_WEIGHT, weight.toFloat())
                .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
                .apply()
            val toolbarText = getString(R.string.toolbarText) + name
            (requireActivity() as MainActivity).binding.tvToolbarTitle.text = toolbarText
        }
        return true
    }
}