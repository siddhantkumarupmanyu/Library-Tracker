package sku.app.lib_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import sku.app.lib_tracker.R
import sku.app.lib_tracker.databinding.TrackerFragmentBinding
import sku.app.lib_tracker.vo.Artifact
import sku.app.lib_tracker.vo.EventObserver
import sku.app.lib_tracker.vo.Library

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

        // TODO: if I read the docs right, we should not be calling this if we are using custom Toolbar
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        setupMenu()

        adapter = LibraryAdapter()
        binding.listView.adapter = adapter


        // will remove this when I am actually done with UI
        // setFakeValuesOnAdapter()

        // since now i am working on ui i would be using static values for now
        viewModel.libraries.observe(viewLifecycleOwner) {
            it?.let { libraries ->
                adapter.submitList(libraries)
            }
        }

        setupEventObserver()

    }

    // will remove this when I am actually done with UI
    fun setFakeValuesOnAdapter() {
        val artifacts1 = listOf(
            Artifact(
                "activity",
                Artifact.Version("1.0.1", "1.1.1-beta02"),
                "androidx.activity"
            ),
            Artifact(
                "activity-compose",
                Artifact.Version("1.0.1", "1.1.1-beta02"),
                "androidx.activity"
            )
        )
        val artifacts2 = listOf(
            Artifact(
                "work",
                Artifact.Version("1.0.1", "1.1.1-beta02"),
                "androidx.work"
            ),
            Artifact(
                "work-test",
                Artifact.Version("1.0.1", "1.1.1-beta02"),
                "androidx.work"
            )
        )

        val libraries = listOf(
            Library("androidx.activity", artifacts1),
            Library("androidx.work", artifacts2)
        )

        adapter.submitList(libraries)
    }

    private fun setupEventObserver() {
        viewModel.events.observe(viewLifecycleOwner, EventObserver {
            when (it) {
                is WorkerState -> showWorkerStatus(it)
            }
        })
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
        viewModel.refresh()
    }

    private fun showWorkerStatus(state: WorkerState) {
        if (state == WorkerState.SUCCEEDED) {
            Snackbar.make(binding.root, R.string.updated_libs, Snackbar.LENGTH_SHORT)
                .show()
        } else if (state == WorkerState.FAILED) {
            Snackbar.make(binding.root, R.string.update_failed, Snackbar.LENGTH_SHORT)
                .setAction(getString(R.string.retry)) {
                    refresh()
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}