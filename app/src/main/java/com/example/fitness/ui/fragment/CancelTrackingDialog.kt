package com.example.fitness.ui.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.fitness.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CancelTrackingDialog:DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
  return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(R.string.cancel_run)
            .setMessage(R.string.warning)
            .setIcon(R.drawable.ic_delete)
            .setPositiveButton(R.string.Yes) { _, _ ->
                yesListener?.let{yes->
                    yes()
                }
            }
            .setNegativeButton(R.string.NO) { dialogInterface, _ ->
                dialogInterface.cancel()
            }
            .create()
    }

    private var yesListener:(()->Unit)?=null
    fun setYesListener(listener:()->Unit){
        yesListener=listener

    }
}