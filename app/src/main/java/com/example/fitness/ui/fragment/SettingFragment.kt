package com.example.fitness.ui.fragment

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitness.R
import com.example.fitness.databinding.FragmentSettingsBinding
import com.example.fitness.ui.activity.MainActivity
import com.example.fitness.util.Constant
import com.example.fitness.util.Constant.KEY_FIRST_TIME_TOGGLE
import com.example.fitness.util.Constant.KEY_NAME
import com.example.fitness.util.Constant.KEY_WEIGHT
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment:Fragment() {

    private lateinit var binding:FragmentSettingsBinding
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPreference()
        with(binding){
            btnApplyChanges.setOnClickListener {
                val success=applyChangeToSharedPref()
                if (success){
                    Snackbar.make(requireView(),"Saved Changes",Snackbar.LENGTH_LONG).show()
                }else{
                    Snackbar.make(requireView(),"Please fill out all fields",Snackbar.LENGTH_LONG).show()
                }
            }

        }

    }
    private fun applyChangeToSharedPref():Boolean{
        with(binding){
            val nameTxt=etName.text.toString()
            val weightTxt=etWeight.text.toString()

            if (nameTxt.isEmpty()||weightTxt.isEmpty()){
                return false
            }

            sharedPreferences.edit()
                .putString(KEY_NAME, nameTxt)
                .putFloat(KEY_WEIGHT, weightTxt.toFloat())
                .apply()
            val toolbarText = getString(R.string.toolbarText) + nameTxt
            (requireActivity() as MainActivity).binding.tvToolbarTitle.text = toolbarText
        }
        return true
    }

    private fun loadFieldsFromSharedPreference() {
        with(binding) {
            val name = sharedPreferences.getString(KEY_NAME," ")
            val weight = sharedPreferences.getFloat(KEY_WEIGHT,80f)
            etName.setText(name)
            etWeight.setText(weight.toString())
        }
    }


}