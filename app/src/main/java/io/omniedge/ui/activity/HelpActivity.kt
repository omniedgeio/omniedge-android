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

private enum class MenuItem(val text: String, val title: Boolean = false) {
    Help(getString(R.string.help), true),
    HomePage(getString(R.string.home_page)),
    OpenSource(getString(R.string.open_source)),
    Licenses(getString(R.string.licenses)),
    Legal(getString(R.string.legal), true),
    TermsOfService(getString(R.string.terms_of_service)),
    PrivacyPolicy(getString(R.string.privacy_policy)),
    About(getString(R.string.about), true),
    Omniedge(getString(R.string.omniedge)),
}

private val items = listOf(
    MenuItem.Help,
    MenuItem.HomePage,
    MenuItem.OpenSource,
//    MenuItem.Licenses,
    MenuItem.Legal,
    MenuItem.TermsOfService,
    MenuItem.PrivacyPolicy,
    MenuItem.About,
    MenuItem.Omniedge,
)

private const val HOME_PAGE_LINK = "https://connect.omniedge.io"
private const val TERMS_OF_SERVICE_LINK = "https://connect.omniedge.io/terms"
private const val PRIVACY_POLICY_LINK = "https://connect.omniedge.io/privacy"
private const val GITHUB_REPO = "https://github.com/omniedgeio/omniedge"

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
    fun bind(item: MenuItem) {
        if (item.title) {
            (binding as ItemHelpTitleBinding).title.text = item.name
            return
        }
        val binding = binding as ItemHelpContentBinding
        binding.title.text = item.text
        when (item) {
            MenuItem.HomePage -> { // home page
                binding.container.setOnClickListener {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(HOME_PAGE_LINK)
                        )
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                    }
                }
            }
            MenuItem.OpenSource -> { // OpenSource
                binding.container.setOnClickListener {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(GITHUB_REPO)
                        )
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                    }
                }
            }
            MenuItem.Licenses -> { // licenses
            }
            MenuItem.TermsOfService -> { // terms of service
                binding.container.setOnClickListener {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(TERMS_OF_SERVICE_LINK)
                        )
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                    }
                }
            }
            MenuItem.PrivacyPolicy -> { // privacy policy
                binding.container.setOnClickListener {
                    try {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(PRIVACY_POLICY_LINK)
                        )
                        itemView.context.startActivity(intent)
                    } catch (e: Exception) {
                    }
                }
            }
            MenuItem.Omniedge -> { // omniedge
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
            else -> {}
        }
    }

}

fun getString(@StringRes resId: Int) = App.instance.getString(resId)