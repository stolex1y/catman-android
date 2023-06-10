package ru.stolexiy.catman.ui.timer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import ru.stolexiy.catman.ui.util.ToolbarProviders

class TimerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopToolbar()
    }

    private fun onClickSettingsMenuItem() {
        Toast.makeText(requireContext(), "Clicked on item settings...", Toast.LENGTH_SHORT).show()
    }

    private fun setupTopToolbar() {
        val menuHost: MenuHost = requireActivity()
        val menuProvider: MenuProvider =
            ToolbarProviders.toolbarWithSettingsProvider(::onClickSettingsMenuItem)
        menuHost.addMenuProvider(menuProvider, viewLifecycleOwner)
        menuHost.invalidateMenu()
    }
}
