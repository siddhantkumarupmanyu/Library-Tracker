package sku.app.lib_tracker.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sku.app.lib_tracker.databinding.ArtifactItemBinding
import sku.app.lib_tracker.databinding.ListItemBinding
import sku.app.lib_tracker.vo.Library
import kotlin.math.min

class LibraryAdapter : ListAdapter<Library, RecyclerView.ViewHolder>(DiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LibraryHolder(
            ListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lib = getItem(position)
        (holder as LibraryHolder).bind(lib)
    }

    class LibraryHolder(private val binding: ListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            // data-binding hack
            binding.artifactsContainer.removeViewAt(0)
        }

        fun bind(item: Library) {
            addArtifacts(item)

            binding.apply {
                library = item
                executePendingBindings()
            }
        }

        private fun addArtifacts(item: Library) {
            val childCount = binding.artifactsContainer.childCount
            val artifactsSize = item.artifacts.size

            val minSize = min(childCount, artifactsSize)

            var i = 0

            // populate views
            while (i < minSize) {
                val view = binding.artifactsContainer.getChildAt(i)
                view.visibility = View.VISIBLE

                val binding = view.tag as ArtifactItemBinding
                binding.artifact = item.artifacts[i]
                binding.executePendingBindings()

                i++
            }

            // create new views
            while (i < artifactsSize) {
                val artifactBinding = ArtifactItemBinding.inflate(
                    LayoutInflater.from(binding.artifactsContainer.context),
                    binding.artifactsContainer,
                    false
                )
                binding.artifactsContainer.addView(artifactBinding.root)

                artifactBinding.root.tag = artifactBinding

                artifactBinding.artifact = item.artifacts[i]
                artifactBinding.executePendingBindings()

                i++
            }

            // hide remaining views if any
            while (i < childCount) {
                val view = binding.artifactsContainer.getChildAt(i)
                view.visibility = View.GONE

                i++
            }

        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Library>() {
        override fun areItemsTheSame(oldItem: Library, newItem: Library): Boolean {
            return oldItem.packageName == newItem.packageName
        }

        override fun areContentsTheSame(oldItem: Library, newItem: Library): Boolean {
            return oldItem == newItem
        }

    }
}
