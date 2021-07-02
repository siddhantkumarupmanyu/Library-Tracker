package sku.app.lib_tracker.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import sku.app.lib_tracker.databinding.ArtifactItemBinding
import sku.app.lib_tracker.databinding.ListItemBinding
import sku.app.lib_tracker.vo.Library

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
        fun bind(item: Library) {
            addArtifacts(item)

            binding.apply {
                library = item
                executePendingBindings()
            }
        }

        private fun addArtifacts(item: Library) {
            binding.linearLayout.removeAllViews()

            for (artifact in item.artifacts) {
                val artifactBinding = ArtifactItemBinding.inflate(
                    LayoutInflater.from(binding.linearLayout.context),
                    binding.linearLayout,
                    false
                )
                binding.linearLayout.addView(artifactBinding.root)

                artifactBinding.artifact = artifact
                artifactBinding.executePendingBindings()
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
