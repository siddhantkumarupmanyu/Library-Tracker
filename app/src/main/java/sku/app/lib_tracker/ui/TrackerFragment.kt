package sku.app.lib_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.work.WorkInfo
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import sku.app.lib_tracker.R
import sku.app.lib_tracker.databinding.TrackerFragmentBinding
import sku.app.lib_tracker.vo.Artifact
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        adapter = LibraryAdapter()
        binding.listView.adapter = adapter

        viewModel.libraries.observe(viewLifecycleOwner) {
            it?.let { libraries ->
                adapter.submitList(libraries)
            }
        }

        viewModel.fetchWorkInfo.observe(viewLifecycleOwner) { infos ->
            val info = infos[0]
            val state = info.state
            if (state == WorkInfo.State.SUCCEEDED) {
                Snackbar.make(binding.root, R.string.updated_libs, Snackbar.LENGTH_LONG).show()
            } else if (state == WorkInfo.State.CANCELLED || state == WorkInfo.State.FAILED) {
                Snackbar.make(binding.root, R.string.update_failed, Snackbar.LENGTH_SHORT)
                    .setAction("Retry") {
                        viewModel.loadLibraries()
                    }
                    .show()
            }
        }
    }

    private fun testList() = listOf(
        Library(
            "androidx.activity", listOf(
                Artifact(
                    "activity",
                    Artifact.Version("1.0.1", "1.1.1-beta01"),
                    "androidx.activity"
                ),
                Artifact(
                    "activity-compose",
                    Artifact.Version("1.0.1", "1.1.1-beta01"),
                    "androidx.activity"
                )
            )
        ),
        Library(
            "androidx.room", listOf(
                Artifact(
                    "room",
                    Artifact.Version("1.0.1", "1.1.1-beta01"),
                    "androidx.room"
                ),
                Artifact(
                    "room-ktx",
                    Artifact.Version("1.0.1", "1.1.1-beta01"),
                    "androidx.room"
                )
            )
        )
    )

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}