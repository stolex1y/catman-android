package ru.stolexiy.catman.ui.timer

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import ru.stolexiy.catman.R
import ru.stolexiy.catman.databinding.FragmentTimerBinding
import ru.stolexiy.catman.ui.util.ToolbarProviders
import ru.stolexiy.catman.ui.util.binding.BindingDelegate.Companion.bindingDelegate
import ru.stolexiy.catman.ui.util.viewmodel.CustomAbstractSavedStateViewModelFactory.Companion.assistedViewModels
import javax.inject.Inject

@AndroidEntryPoint
class TimerFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: TimerViewModel.Factory

    private val viewModel: TimerViewModel by assistedViewModels({ viewModelFactory })

    private val binding: FragmentTimerBinding by bindingDelegate()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireContext().startService(Intent(requireContext(), TimerService::class.java))
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopToolbar()
    }

    override fun onStop() {
        super.onStop()
        requireContext().stopService(Intent(requireContext(), TimerService::class.java))
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
