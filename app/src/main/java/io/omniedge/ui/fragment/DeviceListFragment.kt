package io.omniedge.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.core.graphics.withTranslation
import androidx.core.os.TraceCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import io.omniedge.*
import io.omniedge.data.bean.DeviceData
import io.omniedge.data.bean.NetworkData
import io.omniedge.viewmodel.DeviceListVm
import kotlinx.android.synthetic.main.fragment_device_list.*


class DeviceListFragment : BaseFragment() {

    override fun getLayoutRes(): Int {
        return R.layout.fragment_device_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TraceCompat.beginSection("DeviceList#onViewCreated")
        refresh.setOnRefreshListener {
            refresh.isRefreshing = false
            DeviceListVm.registerDevice()
        }
        val deviceAdapter = DeviceAdapter()
        listview.adapter = deviceAdapter
        listview.addItemDecoration(DividerItemDecoration(view.context, RecyclerView.VERTICAL))
        listview.addItemDecoration(deviceAdapter.getPinnedHeaderItemDecoration(listview))
        val fragmentActivity = activity as FragmentActivity
        fragmentActivity.findViewById<View>(R.id.btn_ping)
            ?.setOnClickListener { deviceAdapter.updatePing() }

        DeviceListVm.networks().observe(fragmentActivity) { networks ->
            deviceAdapter.updateData(networks)
        }

        TraceCompat.endSection()
    }
}


class DeviceVh(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val rbNetwork: AppCompatRadioButton? = itemView.findViewById(R.id.network_radio_button)

    val tvDeviceName: TextView? = itemView.findViewById(R.id.tv_name)
    val tvIp: TextView? = itemView.findViewById(R.id.tv_ip)
    val tvPing: TextView? = itemView.findViewById(R.id.tv_ping)
}

class DeviceAdapter : RecyclerView.Adapter<DeviceVh>() {
    private data class ListDeviceData(
        val device: DeviceData?,
        val network: NetworkData?,
        val isNetworkData: Boolean,
        val index: Int,
        var joiningStatus: JoiningStatus = JoiningStatus.Unknown,
        var ping: String = "? ms",
    )

    private enum class JoiningStatus { Joined, Joining, Unknown }

    companion object {
        const val TYPE_NETWORK = 0
        const val TYPE_DEVICE = 1

    }

    private val networkData = mutableListOf<ListDeviceData>()

    fun updatePing() {
        for ((index, data) in networkData.withIndex()) {
            if (data.isNetworkData) continue
            val privateIp = data.device?.virtualIp ?: continue
            object : Thread("updatePing-$index") {
                override fun run() {
                    super.run()
                    val ping: String = try {
                        ping(privateIp)
                    } catch (e: Throwable) { // 跨进程调用
                        OmniLog.e("error on ping $privateIp", e)
                        "? ms"
                    }
                    if (ping.isNotEmpty() && "? ms" != ping) {
                        data.ping = ping
                        ThreadUtils.runOnMainThread {
                            networkData.indexOf(data).let {
                                if (it > 0) {
                                    this@DeviceAdapter.notifyItemChanged(it)
                                }
                            }
                        }
                    }
                }
            }.start()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceVh {
        TraceCompat.beginSection("DeviceAdapter#onCreateViewHolder")
        val viewHolder: DeviceVh = when (viewType) {
            TYPE_NETWORK -> {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.item_list_network, parent, false)
                DeviceVh(itemView)
            }
            TYPE_DEVICE -> {
                val inflater = LayoutInflater.from(parent.context)
                val itemView = inflater.inflate(R.layout.item_list_device, parent, false)
                DeviceVh(itemView)
            }
            else -> {
                throw IllegalStateException()
            }
        }
        TraceCompat.endSection()
        return viewHolder
    }

    override fun getItemCount(): Int {
        return networkData.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (networkData[position].isNetworkData) {
            true -> TYPE_NETWORK
            false -> TYPE_DEVICE
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: DeviceVh, position: Int) {
        TraceCompat.beginSection("DeviceAdapter#onBindViewHolder")
        val data = networkData[position]
        if (data.isNetworkData) {
            holder.rbNetwork?.text = data.network?.name
//            holder.rbNetwork?.setOnCheckedChangeListener { _, isChecked ->
//                if (isChecked && data.joiningStatus == JoiningStatus.Unknown) {
//                    DeviceListVm.joinNetwork(data.network)
//                }
//            }
            holder.rbNetwork?.setOnClickListener {
                if (holder.rbNetwork.isChecked && data.joiningStatus == JoiningStatus.Unknown) {
                    DeviceListVm.joinNetwork(data.network)
                }
            }
            holder.rbNetwork?.isChecked = data.joiningStatus == JoiningStatus.Joined

        } else {
            holder.tvDeviceName?.text = data.device?.name
            holder.tvIp?.text = data.device?.virtualIp
            holder.tvPing?.text = data.ping
        }
        TraceCompat.endSection()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(networks: List<NetworkData>) {
        networkData.clear()
        var index = 0
        val uuid = App.repository.getLatestJoinedNetworkUUID()
        val deviceUUID = App.repository.getDeviceUUID()
        networks.forEach { data ->
            var deviceJoined = false
            var deviceData: DeviceData? = null
            val listData =
                ListDeviceData(null, data, true, index++)
            networkData.add(listData)
            data.devices?.forEach {
                if (it.uuid == deviceUUID) {
                    deviceJoined = true
                    deviceData = it
                }
                networkData.add(ListDeviceData(it, null, false, index))
            }
            listData.joiningStatus =
                if (uuid != null && uuid == data.uuid && deviceJoined) JoiningStatus.Joined else JoiningStatus.Unknown
            val finalDeviceData = deviceData ?: return@forEach
            if (listData.joiningStatus == JoiningStatus.Joined) {
                DeviceListVm.currentNetwork()
                    .postValue(Pair(data, finalDeviceData))
                // join network on the first time
                if (DeviceListVm.joinedNetwork().value == null) {
                    DeviceListVm.joinNetwork(data)
                }
            }
        }
        // TODO: 2021/8/22 use the efficient way to refresh
        notifyDataSetChanged()
    }

    fun getPinnedHeaderItemDecoration(recyclerView: RecyclerView): PinnedHeaderItemDecoration {
        return PinnedHeaderItemDecoration(recyclerView) { position ->
            position >= 0 && position < networkData.size && networkData[position].isNetworkData
        }
    }
}

class PinnedHeaderItemDecoration(
    recyclerView: RecyclerView,
    private val isHeader: (position: Int) -> Boolean,
) : RecyclerView.ItemDecoration() {
    private var currentHeaderPosition = RecyclerView.NO_POSITION
    private var currentHeader: RecyclerView.ViewHolder? = null

    init {
        recyclerView.adapter?.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                currentHeader = null
                currentHeaderPosition = RecyclerView.NO_POSITION
            }
        })

        recyclerView.doOnEachNextLayout {
            currentHeader = null
            currentHeaderPosition = RecyclerView.NO_POSITION
        }

        recyclerView.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
            var shouldClickHeader = false
            override fun onInterceptTouchEvent(rv: RecyclerView, event: MotionEvent): Boolean {
                if (event.y <= currentHeader?.itemView?.bottom ?: 0) {
                    when (event.action and MotionEvent.ACTION_MASK) {
                        MotionEvent.ACTION_DOWN -> {
                            shouldClickHeader = true
                        }
                        MotionEvent.ACTION_UP -> {
                            if (shouldClickHeader) {
                                currentHeader?.itemView?.performClick()
                            }
                            shouldClickHeader = false
                        }
                        MotionEvent.ACTION_CANCEL -> {
                            shouldClickHeader = false
                        }
                        MotionEvent.ACTION_OUTSIDE -> {
                            shouldClickHeader = false
                        }
                        else -> {
                        }
                    }
                }
                return event.action == MotionEvent.ACTION_UP &&
                        event.y <= currentHeader?.itemView?.bottom ?: 0
            }
        })
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)

        val topChild = parent.getChildAt(0)
        val topChildPosition = parent.getChildAdapterPosition(topChild)
        if (topChildPosition == RecyclerView.NO_POSITION) {
            return
        }

        val headerView = getHeaderViewForItem(topChildPosition, parent) ?: return

        val contactPoint = headerView.bottom + parent.paddingTop
        val childInContact = getChildInContact(parent, contactPoint)

        if (childInContact != null && isHeader(parent.getChildAdapterPosition(childInContact))) {
            moveHeader(c, headerView, childInContact, parent.paddingTop)
            return
        }
        c.withTranslation(0f, parent.paddingTop.toFloat()) { headerView.draw(this) }
    }

    private fun moveHeader(c: Canvas, headerView: View, nextView: View, paddingTop: Int) {
        c.save()
        c.clipRect(0, paddingTop, c.width, paddingTop + headerView.height)
        c.translate(0f, (nextView.top - headerView.height).toFloat())
        headerView.draw(c)
        c.restore()
    }

    private fun getChildInContact(parent: RecyclerView, recyclerViewOffset: Int): View? {
        var childView: View? = null
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val bounds = Rect()
            parent.getDecoratedBoundsWithMargins(child, bounds)
            if (bounds.bottom > recyclerViewOffset) {
                if (bounds.top <= recyclerViewOffset) {
                    childView = child
                    break
                }
            }
        }
        return childView
    }

    private fun getHeaderViewForItem(position: Int, parent: RecyclerView): View? {
        if (parent.adapter == null) {
            return null
        }
        val headerPosition = getHeaderPositionFromItemPosition(position)
        if (headerPosition == RecyclerView.NO_POSITION) {
            return null
        }
        val headerType = parent.adapter?.getItemViewType(headerPosition) ?: return null
        val currentHeader = currentHeader
        if (currentHeader?.itemViewType == headerType) {
            if (currentHeaderPosition != headerPosition) {
                parent.adapter?.onBindViewHolder(currentHeader, headerPosition)
                currentHeaderPosition = headerPosition
            }
            return currentHeader.itemView
        }
        val headerHolder = parent.adapter?.createViewHolder(parent, headerType)
        if (headerHolder != null) {
            parent.adapter?.onBindViewHolder(headerHolder, headerPosition)
            fixLayoutSize(parent, headerHolder.itemView)
            currentHeaderPosition = headerPosition
            this.currentHeader = headerHolder
        }

        return headerHolder?.itemView
    }

    private fun fixLayoutSize(parent: ViewGroup, itemView: View) {
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)

        val childWidthSpec = ViewGroup.getChildMeasureSpec(
            widthSpec,
            parent.paddingLeft + parent.paddingRight,
            itemView.layoutParams.width
        )
        val childHeightSpec = ViewGroup.getChildMeasureSpec(
            heightSpec,
            parent.paddingTop + parent.paddingBottom,
            itemView.layoutParams.height
        )
        itemView.measure(childWidthSpec, childHeightSpec)
        itemView.layout(0, 0, itemView.measuredWidth, itemView.measuredHeight)
    }

    private fun getHeaderPositionFromItemPosition(position: Int): Int {
        for (index in position downTo 0) {
            if (isHeader(index)) {
                return index
            }
        }
        return RecyclerView.NO_POSITION
    }
}

inline fun View.doOnEachNextLayout(crossinline action: (view: View) -> Unit) {
    addOnLayoutChangeListener { view, _, _, _, _, _, _, _, _ -> action(view) }
}
