package com.edlplan.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.preference.PreferenceFragmentCompat
import com.edlplan.ui.ActivityOverlay

abstract class SettingsFragment : PreferenceFragmentCompat(), BackPressListener {
    var root: View? = null
        private set
    var isCreated = false
        private set

    @Suppress("UNCHECKED_CAST")
    fun <T : View?> findViewById(@IdRes id: Int): T? {
        val o: Any? = if (root != null) root!!.findViewById<View>(id) else null
        return if (o == null) {
            null
        } else {
            o as T
        }
    }

    protected abstract fun playOnLoadAnim()

    open fun show() {
        ActivityOverlay.addOverlay(this, this.javaClass.name + "@" + this.hashCode())
    }

    open fun dismiss() {
        ActivityOverlay.dismissOverlay(this)
    }

    override fun callDismissOnBackPress() {
        dismiss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        isCreated = true
        root = super<PreferenceFragmentCompat>.onCreateView(inflater, container, savedInstanceState)
        playOnLoadAnim()
        return root
    }
}