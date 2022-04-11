package com.example.fitness.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fitness.R
import com.example.fitness.databinding.FragmentTrackingBinding
import com.example.fitness.model.db.Run
import com.example.fitness.ui.services.Polyline
import com.example.fitness.ui.services.TrackServices
import com.example.fitness.ui.viewModel.MainViewModel
import com.example.fitness.util.Constant.ACTION_PAUSE_SERVICE
import com.example.fitness.util.Constant.ACTION_START_SERVICE
import com.example.fitness.util.Constant.ACTION_STOP_SERVICE
import com.example.fitness.util.Constant.CANCEL_TRACKING_FRAGMENT
import com.example.fitness.util.Constant.MAP_ZOOM
import com.example.fitness.util.Constant.POLYLINE_COLOR
import com.example.fitness.util.Constant.POLYLINE_WIDTH
import com.example.fitness.util.TrackingUtility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject
import kotlin.math.round

@AndroidEntryPoint
class TrackingFragment : Fragment() {

    private lateinit var binding: FragmentTrackingBinding
    private val viewModel: MainViewModel by viewModels()
    private var map: GoogleMap? = null

    private var pathpoints = mutableListOf<Polyline>()
    private var isTracking = true

    private var curTimeInMillis = 0L

    private var menu: Menu? = null

    @set:Inject
    var weight = 80f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        binding = FragmentTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            mapView.onCreate(savedInstanceState)

            btnToggleRun.setOnClickListener {
                //start and stop service
                toggleRun()
            }

            btnFinishRun.setOnClickListener {
                zoomToSeeWholeTrack()
                endRunAndSaveToDataBase()
            }

            mapView.getMapAsync {
                map = it
                //polyline rotate solve
                addAllPolylines()
            }
            subscribeToObserve()
        }
        if (savedInstanceState!=null){
            val cancelTrackingDialog=parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_FRAGMENT)as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener {
                stopRun()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        with(binding) {
            mapView?.onStart()
        }
    }

    override fun onResume() {
        super.onResume()
        with(binding) {
            mapView?.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        with(binding) {
            mapView?.onPause()
        }
    }

    override fun onStop() {
        super.onStop()
        with(binding) {
            mapView?.onStop()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        with(binding) {
            mapView?.onLowMemory()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        with(binding) {
            mapView?.onDestroy()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        with(binding) {
            mapView?.onSaveInstanceState(outState)
        }
    }

    /**
    function to send action to service these action is string that i define in util.Constant
    and service do something different depend on the string i send
     */

    private fun sendCommandToService(action: String) {
        Intent(requireContext(), TrackServices::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }

    //Draw polyline
    private fun addLatestPolyLine() {
        if (pathpoints.isNotEmpty() && pathpoints.last().size > 1) {
            val preLastLong = pathpoints.last()[pathpoints.last().size - 2]
            val lastLatLong = pathpoints.last().last()
            val polyLineOption = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLong)
                .add(lastLatLong)
            map?.addPolyline(polyLineOption)
        }
    }


    private fun moveCameraToUser() {
        if (pathpoints.isNotEmpty() && pathpoints.last().isNotEmpty()) {
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    pathpoints.last().last(),
                    MAP_ZOOM
                )
            )
        }
    }

    //update ui
    private fun updateTracking(isTracking: Boolean) {
        val start: String = getString(R.string.Start)
        val stop: String = getString(R.string.Stop)

        this.isTracking = isTracking
        if (!isTracking && curTimeInMillis >0L) {
            with(binding) {
                //TODO databinding two way live data
                btnToggleRun.text = start
                btnFinishRun.visibility = View.VISIBLE
            }
        } else if (isTracking){
            with(binding) {
                btnToggleRun.text = stop
                menu?.getItem(0)?.isVisible = true
                btnFinishRun.visibility = View.GONE
            }
        }
    }


    //to solve polyline disappear when rotate
    private fun addAllPolylines() {
        for (polyline in pathpoints) {
            val polyLineOption = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyline)
            map?.addPolyline(polyLineOption)
        }
    }

    //start and end the service
    private fun toggleRun() {
        if (isTracking) {
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        } else {
            sendCommandToService(ACTION_START_SERVICE)
        }
    }

    //to take screen shot to all track
    private fun zoomToSeeWholeTrack() {
        val bounds = LatLngBounds.Builder()
        for (polyline in pathpoints) {
            for (pos in polyline) {
                bounds.include(pos)
            }
        }
        map?.moveCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds.build(),
                binding.mapView.width,
                binding.mapView.height,
                (binding.mapView.height * 0.05f).toInt()

            )
        )
    }

    //save data in database
    private fun endRunAndSaveToDataBase() {
        map?.snapshot { bmb ->
            //calc avg speed **
            var distanceInMeters = 0
            for (polyline in pathpoints) {
                distanceInMeters += TrackingUtility.calculatePolylineLength(polyline).toInt()
            }
            val avgSpeed =
                round(distanceInMeters / 1000f) / (curTimeInMillis / 1000f / 60 / 60) / 10f
            val dateTimeStamp = Calendar.getInstance().timeInMillis
            val caloriesBurned = ((distanceInMeters / 1000f) * weight).toInt()

            //object from dp entity
            val run =
                Run(bmb, dateTimeStamp, curTimeInMillis, avgSpeed, distanceInMeters, caloriesBurned)
            viewModel.insertRun(run)
            Snackbar.make(
                requireActivity().findViewById(R.id.rootView),
                "Run saved successfully",
                Snackbar.LENGTH_LONG
            ).show()
            stopRun()
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // here we observe on livedata that we create on service to update fragment ui
    // the function that use above function
    private fun subscribeToObserve() {
        TrackServices.isTracking.observe(viewLifecycleOwner, Observer {
            updateTracking(it)
        })
        TrackServices.pathPoints.observe(viewLifecycleOwner, Observer {
            pathpoints = it
            addLatestPolyLine()
            moveCameraToUser()
        })
        TrackServices.timeRunInMills.observe(viewLifecycleOwner, Observer {
            curTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(curTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        })

    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //toolbar menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu, menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (curTimeInMillis > 0L) {
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    private fun cancelDialog() {
    CancelTrackingDialog().apply {
        setYesListener {
            stopRun()
        }
    }.show(parentFragmentManager,CANCEL_TRACKING_FRAGMENT)

    }

    private fun stopRun() {
        binding.tvTimer.text="00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        findNavController().navigate(R.id.action_trackingFragment_to_runFragment)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cancelTracking -> {
                cancelDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }


}