package com.example.fitness.ui.fragment

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitness.R
import com.example.fitness.databinding.FragmentRunBinding
import com.example.fitness.ui.adapters.RunAdapter
import com.example.fitness.ui.viewModel.MainViewModel
import com.example.fitness.util.Constant.REQUEST_CODE_LOCATION_PERMISSION
import com.example.fitness.util.SortType
import com.example.fitness.util.TrackingUtility
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class RunFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentRunBinding

    //inject viewModel factory using by viewModels()
    private val viewModel: MainViewModel by viewModels()

    private lateinit var runAdapter: RunAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermission()
        setupRecyclerview()

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })

        with(binding) {
            fab.setOnClickListener {
                findNavController().navigate(R.id.trackingFragment)
            }

            //sort
            when(viewModel.sortType){
                SortType.DATE-> spFilter.setSelection(0)
                SortType.RUNNING_TIME-> spFilter.setSelection(1)
                SortType.DISTANCE-> spFilter.setSelection(2)
                SortType.AVG_SPEED-> spFilter.setSelection(3)
                SortType.CALORIES_BURNT-> spFilter.setSelection(4)
            }
            spFilter.onItemSelectedListener=object :AdapterView.OnItemSelectedListener{
                override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                   when(pos){
                       0->viewModel.sortRuns(SortType.DATE)
                       1->viewModel.sortRuns(SortType.RUNNING_TIME)
                       2->viewModel.sortRuns(SortType.DISTANCE)
                       3->viewModel.sortRuns(SortType.AVG_SPEED)
                       4->viewModel.sortRuns(SortType.CALORIES_BURNT)
                   }
                }
                override fun onNothingSelected(p0: AdapterView<*>?) {
                }

            }

        }
    }

    /**
1. fun requestPermission() check that permission granted or not if grant return no show dialog and show permission again
  2.after that we implement EasyPermissions.PermissionCallbacks interface and override two fun
     3.IN Permission Denied if user deny permission two time we show dialog to him to active it from setting
     if first time only we use requestPermission() that we create to use here
     */
    private fun requestPermission() {
        if (TrackingUtility.hasLocationPermission(requireContext())) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You Need to accept location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {

            EasyPermissions.requestPermissions(
                this,
                "You Need to accept location permissions to use this app",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
    }

    override fun onPermissionsDenied(requestCode: Int,
                                     perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }
/*
TODO
Deprecated  onRequestPermissionsResult in fragment find replacement
*/
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this)
    }
    /////////////////////////////////////////////
    private fun setupRecyclerview() = binding.rvRuns.apply {
        runAdapter= RunAdapter()
        adapter=runAdapter
        layoutManager=LinearLayoutManager(requireContext())

    }
}