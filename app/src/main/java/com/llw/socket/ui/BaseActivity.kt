package com.llw.socket.ui

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.llw.socket.SocketApp
import com.llw.socket.adapter.EmojiAdapter
import com.llw.socket.databinding.DialogEmojiBinding
import java.net.Inet4Address
import java.net.NetworkInterface

open class BaseActivity : AppCompatActivity() {

    /**
     * 获取Ip地址
     */
    protected fun getIp() =
        intToIp((applicationContext.getSystemService(WIFI_SERVICE) as WifiManager).connectionInfo.ipAddress)

    /**
     * 获取当前IP地址
     *
     * @return
     */
    open fun getHotspotIp(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                // 检查接口是否是活动的
                if (intf.isUp && !intf.isLoopback) {
                    val addrs = intf.inetAddresses
                    while (addrs.hasMoreElements()) {
                        val addr = addrs.nextElement()
                        // 确保是IPv4地址
                        if (addr is Inet4Address) {
                            return addr.getHostAddress()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return null
    }

    /**
     * Ip地址转换
     */
    private fun intToIp(ip: Int) =
        "${(ip and 0xFF)}.${(ip shr 8 and 0xFF)}.${(ip shr 16 and 0xFF)}.${(ip shr 24 and 0xFF)}"

    /**
     * 显示Toast
     */
    protected fun showMsg(msg: CharSequence?) =
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()

    /**
     * 跳转页面
     */
    protected open fun jumpActivity(clazz: Class<*>?) = startActivity(Intent(this, clazz))

    /**
     * 显示Emoji弹窗
     */
    protected fun showEmojiDialog(context: Context, callback: EmojiCallback) {
        val emojiBinding = DialogEmojiBinding.inflate(LayoutInflater.from(context), null, false)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(emojiBinding.root)
        emojiBinding.rvEmoji.apply {
            layoutManager = GridLayoutManager(context, 6)
            adapter = EmojiAdapter(SocketApp.instance().emojiList).apply {
                setOnItemClickListener(object : EmojiAdapter.OnClickListener {
                    override fun onItemClick(position: Int) {
                        val charSequence = SocketApp.instance().emojiList[position]
                        callback.checkedEmoji(charSequence)
                        dialog.dismiss()
                    }
                })
            }
        }
        dialog.show()
    }
}