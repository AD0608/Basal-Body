package com.mxb.app.utils

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.google.android.material.tabs.TabLayout
import com.mxb.app.R
import com.mxb.app.extensions.changeColor
import com.mxb.app.extensions.changeFont

class MxbTabLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = com.google.android.material.R.attr.tabStyle
) : TabLayout(context, attrs, defStyleAttr) {

    private var tabTitles: List<String> = emptyList()
    private var onTabSelected: ((position: Int, title: String) -> Unit)? = null

    fun setTabs(
        titles: List<String>,
        onTabSelected: ((position: Int, title: String) -> Unit)? = null
    ) {
        this.tabTitles = titles
        this.onTabSelected = onTabSelected
        setupTabs()
    }

    private fun setupTabs() {
        removeAllTabs()

        val tabTitlesSize = tabTitles.size
        tabTitles.forEachIndexed { index, title ->
            val tab = newTab()
            val displayMetrics = resources.displayMetrics
            val tabWidth = displayMetrics.widthPixels / tabTitlesSize
            tab.customView = createCustomTab(title, index == tabTitles.lastIndex)
            // Set custom width
            tab.customView?.layoutParams = LinearLayout.LayoutParams(
                tabWidth,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            addTab(tab)
        }

        // Select first tab
        updateTabStyle(getTabAt(0), selected = true)

        addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                updateTabStyle(tab, selected = true)
            }

            override fun onTabUnselected(tab: Tab?) {
                updateTabStyle(tab, selected = false)
            }

            override fun onTabReselected(tab: Tab?) {}
        })

        val tabStrip = getChildAt(0) as? LinearLayout
        tabStrip?.apply {
            setPadding(
                0,
                0,
                0,
                0
            )        // Remove start/end/top/bottom padding from SlidingTabIndicator
            clipToPadding = false

            for (i in 0 until childCount) {
                val tabView = getChildAt(i)
                tabView.setPadding(0, 0, 0, 0)   // Remove padding from each tab
                tabView.minimumWidth = 0        // Optional: avoid default minWidth
            }
        }
    }

    private fun updateTabStyle(tab: Tab?, selected: Boolean) {
        val textView = tab?.customView?.findViewById<MxbTextView>(R.id.tvTabTitle)
        val position = selectedTabPosition

        if (selected) {
            textView?.changeFont(R.font.just_sans_bold)
            textView?.changeColor(R.color.colorSecondary)
            onTabSelected?.invoke(position, tabTitles[position])
        } else {
            textView?.changeFont(R.font.just_sans_medium)
            textView?.changeColor(R.color.colorSecondary)
        }
    }

    private fun createCustomTab(title: String, isLast: Boolean): View {
        val view = LayoutInflater.from(context).inflate(R.layout.custom_tab, this, false)
        view.findViewById<MxbTextView>(R.id.tvTabTitle).text = title
        if (isLast) {
            view.findViewById<View>(R.id.view).visibility = View.GONE
        }
        return view
    }
}
