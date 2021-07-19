package sku.app.lib_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.work.ListenableWorker.Result.retry
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import sku.app.lib_tracker.R
import sku.app.lib_tracker.databinding.TrackerFragmentBinding

@AndroidEntryPoint
class TrackerFragment : Fragment() {

    private var _binding: TrackerFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val viewModel: TrackerViewModel by viewModels()

    private lateinit var adapter: LibraryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater,
            R.layout.tracker_fragment,
            container,
            false
        )

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        setupMenu()

        adapter = LibraryAdapter()
        binding.listView.adapter = adapter

        viewModel.libraries.observe(viewLifecycleOwner) {
            it?.let { libraries ->
                adapter.submitList(libraries)
            }
        }

    }

    private fun setupMenu() {
        binding.toolbar.inflateMenu(R.menu.menu_tracker)

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.refresh) {
                refresh()
                true
            } else {
                false
            }
        }
    }

    private fun refresh() {
        val refresh = viewModel.refresh()

        // todo: should observe snackbarEventLiveData

//        refresh.observe(viewLifecycleOwner, object : Observer<WorkerState> {
//            override fun onChanged(state: WorkerState) {
//                if (state.isFinished()) {
//                    refresh.removeObserver(this)
//                    if (state == WorkerState.SUCCEEDED) {
//                        Snackbar.make(binding.root, R.string.updated_libs, Snackbar.LENGTH_LONG)
//                            .show()
//                    } else if (state == WorkerState.FAILED) {
//                        Snackbar.make(binding.root, R.string.update_failed, Snackbar.LENGTH_SHORT)
//                            .setAction(getString(R.string.retry)) {
//                                refresh()
//                            }
//                            .show()
//                    }
//                }
//            }
//        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}