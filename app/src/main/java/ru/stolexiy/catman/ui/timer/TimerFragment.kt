package ru.stolexiy.catman.ui.timer

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment

class TimerFragment : Fragment() {
    override fun onDestroy() {
        Toast.makeText(requireContext(), "Timer is destroying...", Toast.LENGTH_SHORT).show()
        super.onDestroy()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}