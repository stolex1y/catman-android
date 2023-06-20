package ru.stolexiy.catman.ui.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
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

    private var bound: Boolean = false

    private val connection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            binding.timerView.attachTimer((service as TimerService.Binder).timer)
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            detachTimer()
            bound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopToolbar()
    }

    override fun onStart() {
        super.onStart()
        bindTimerService()
    }

    override fun onStop() {
        super.onStop()
        unbindTimerService()
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

    private fun bindTimerService() {
        val startServiceIntent = Intent(requireContext(), TimerService::class.java)
        requireContext().startService(startServiceIntent)
        requireContext().bindService(startServiceIntent, connection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindTimerService() {
        requireContext().unbindService(connection)
        detachTimer()
    }

    private fun detachTimer() {
        binding.timerView.detachTimer()
    }
}
