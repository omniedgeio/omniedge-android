package io.omniedge.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import io.omniedge.R
import io.omniedge.data.bean.DeviceBean

/**
 * Created on 2019-12-22 16:33
 * 
 */
class DeviceAdapter : RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder>() {

    private val devices = mutableListOf<DeviceBean>()

    fun addDevices(deviceBean: List<DeviceBean>) {
        devices.clear()
        devices.addAll(deviceBean)
        notifyDataSetChanged()
    }

    fun getDevices(): List<DeviceBean> {
        return devices
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        return DeviceViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_device, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        val bean = devices[position]
        if (bean.time == "0ms") {
            bean.time = "?ms"
        }
        val context = holder.itemView.context
        holder.textName.text = bean.name ?: context.getString(R.string.unkonwnName)
        holder.textIp.text = bean.ip ?: context.getString(R.string.unknownIP)
        holder.textNumber.text = "${position + 1}"
        holder.textDelay.text = bean.time ?: "?ms"
    }

    class DeviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val textName: TextView = itemView.findViewById(R.id.text_name)
        val textIp: TextView = itemView.findViewById(R.id.text_ip)
        val textNumber: TextView = itemView.findViewById(R.id.text_number)
        val textDelay: TextView = itemView.findViewById(R.id.text_delay)

    }
}