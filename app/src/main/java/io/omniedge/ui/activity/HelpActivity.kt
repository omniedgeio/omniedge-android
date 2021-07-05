package io.omniedge.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.TraceCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import io.omniedge.App
import io.omniedge.R
import io.omniedge.databinding.ActivityHelpBinding
import io.omniedge.databinding.ItemHelpContentBinding
import io.omniedge.databinding.ItemHelpTitleBinding

private val items = listOf(
    Item(getString(R.string.help), true),
    Item(getString(R.string.home_page)),
    Item(getString(R.string.licenses)),
    Item(getString(R.string.legal), true),
    Item(getString(R.string.terms_of_service)),
    Item(getString(R.string.privacy_policy)),
    Item(getString(R.string.about), true),
    Item(getString(R.string.omniedge)),
)

class HelpActivity : BaseActivity() {

    private lateinit var binding: ActivityHelpBinding
    override fun getPageTitle() = R.string.help

    override fun getLayoutView(): View? {
        binding = ActivityHelpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun init() {
        super.init()
        TraceCompat.beginSection("HelpActivity#initView")
        binding.textTitle.text = getString(R.string.help)
        binding.recyclerView.adapter = HelpListAdapter()
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        TraceCompat.endSection()
    }
}

private class HelpListAdapter : RecyclerView.Adapter<ItemViewHolder>() {
    override fun getItemViewType(position: Int) = if (items[position].title) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                ItemViewHolder(ItemHelpTitleBinding.inflate(inflater, parent, false))
            }
            1 -> {
                ItemViewHolder(ItemHelpContentBinding.inflate(inflater, parent, false))
            }
            else -> {
                throw RuntimeException("Unknown item type:$viewType")
            }
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

}

private class ItemViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: Item) {
        if (item.title) {
            (binding as ItemHelpTitleBinding).title.text = item.name
        } else {
            val binding = binding as ItemHelpContentBinding
            binding.title.text = item.name
            when (items.indexOf(item)) {
                1 -> { // home page
                    binding.container.setOnClickListener {
                        try {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(getString(R.string.home_page_link))
                            )
                            itemView.context.startActivity(intent)
                        } catch (e: Exception) {
                        }
                    }
                }
                2 -> { // licenses
                }
                4 -> { // terms of service
                }
                5 -> { // privacy policy
                }
                7 -> { // omniedge
                    try {
                        val packageInfo = itemView.context.packageManager.getPackageInfo(
                            itemView.context.packageName,
                            0
                        )
                        binding.subTitle.visibility = View.VISIBLE
                        binding.subTitle.text = packageInfo.versionName
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

}

private data class Item(val name: String, val title: Boolean = false)

fun getString(@StringRes resId: Int) = App.instance.getString(resId)