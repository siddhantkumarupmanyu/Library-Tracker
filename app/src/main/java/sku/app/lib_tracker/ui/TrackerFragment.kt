package sku.app.lib_tracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import sku.app.lib_tracker.LibraryAdapter
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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        adapter = LibraryAdapter()
        binding.listView.adapter = adapter

        viewModel.testData.observe(viewLifecycleOwner) { libs ->
            adapter.submitList(libs)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}