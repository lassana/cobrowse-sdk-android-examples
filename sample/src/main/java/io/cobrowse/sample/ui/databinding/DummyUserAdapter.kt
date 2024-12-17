package io.cobrowse.sample.ui.databinding

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.cobrowse.CobrowseIO
import io.cobrowse.sample.R
import io.cobrowse.sample.data.DummyUser
import io.cobrowse.sample.databinding.CellDummyUserBinding
import io.cobrowse.sample.ui.databinding.DummyUserAdapter.DummyUserHolder

/**
 * Recycler View adapter which also tracks redacted elements in cells.
 */
class DummyUserAdapter : RecyclerView.Adapter<DummyUserHolder>(),
    BindableAdapter<DummyUser>,
    CobrowseIO.Redacted {

    private var users = emptyList<DummyUser>()

    private val displayedCells = HashSet<DummyUserHolder>()

    @SuppressLint("NotifyDataSetChanged")
    override fun setData(items: List<DummyUser>) {
        users = items
        notifyDataSetChanged()
    }

    override fun onViewAttachedToWindow(holder: DummyUserHolder) {
        super.onViewAttachedToWindow(holder)
        displayedCells.add(holder)
    }

    override fun onViewDetachedFromWindow(holder: DummyUserHolder) {
        super.onViewDetachedFromWindow(holder)
        displayedCells.remove(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DummyUserHolder {
        return DummyUserHolder(
            CellDummyUserBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: DummyUserHolder, position: Int) {
        holder.rebind(users[position])
    }

    class DummyUserHolder(val binding: CellDummyUserBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun rebind(item: DummyUser) {
            binding.layoutDataBindingSampleListText.text =
                binding.root.context.getString(R.string.textview_dummyuser_detailed,
                    item.lastName, item.firstName)
        }
    }

    override fun redactedViews(): MutableList<View> = displayedCells
        .map { it.binding.layoutDataBindingSampleListText }
        .toMutableList()
}