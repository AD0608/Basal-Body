package com.basalbody.app.ui.home.activity

import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.basalbody.app.R
import com.basalbody.app.base.BaseActivity
import com.basalbody.app.databinding.ActivityNotificationsBinding
import com.basalbody.app.extensions.changeText
import com.basalbody.app.extensions.onSafeClick
import com.basalbody.app.ui.home.adapter.NotificationListAdapter
import com.basalbody.app.ui.home.viewmodel.HomeViewModel
import com.basalbody.app.utils.Logger
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.max

@AndroidEntryPoint
class NotificationsActivity : BaseActivity<HomeViewModel, ActivityNotificationsBinding>() {
    override fun getViewBinding(): ActivityNotificationsBinding =
        ActivityNotificationsBinding.inflate(layoutInflater)

    private val notificationsAdapter by lazy {
        NotificationListAdapter(
            context = this,
            arrayListOf(
                "", "", "", "", "", "",
                "", "", "", "", "", "",
                "", "", "", "", "", "",
            ),
            onItemClick = { item ->

            }
        )
    }

    override fun initSetup() {
        binding.apply {
            ViewCompat.setOnApplyWindowInsetsListener(main) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPaddingRelative(0,systemBars.top,0,0)
                rvNotifications.updatePadding(bottom = systemBars.bottom)
                insets
            }
            toolBar.tvTitle.changeText(getString(R.string.label_notification))
            rvNotifications.adapter = notificationsAdapter
            //setupSwipeToDelete()
        }
    }

    private fun setupSwipeToDelete() {
        binding.apply {
            val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                // Keep bounds per position
                private val itemDeleteIconBounds = mutableMapOf<Int, RectF>()

                private val swipeWidth =
                    60f * Resources.getSystem().displayMetrics.density

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    // ❌ Do nothing
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX < 0) {
                        val itemView = viewHolder.itemView
                        val paint = Paint().apply { color = "#FF0000".toColorInt() }

                        val radius = 12f * recyclerView.context.resources.displayMetrics.density
                        val clampedDx = max(dX, -swipeWidth)

                        // The revealed background area
                        val background = RectF(
                            itemView.right.toFloat() + clampedDx,
                            itemView.top.toFloat(),
                            itemView.right.toFloat(),
                            itemView.bottom.toFloat()
                        )

                        c.save()
                        c.clipRect(background)

                        // Draw rounded background (left corners only)
                        val path = Path().apply {
                            addRoundRect(
                                background,
                                floatArrayOf(
                                    0f, 0f,
                                    radius, radius,
                                    radius, radius,
                                    0f, 0f,
                                ),
                                Path.Direction.CW
                            )
                        }
                        c.drawPath(path, paint)

                        // Draw delete icon centered in revealed area
                        val icon = ContextCompat.getDrawable(
                            recyclerView.context,
                            R.drawable.ic_delete_notification
                        )!!

                        val iconWidth = icon.intrinsicWidth
                        val iconHeight = icon.intrinsicHeight

                        val areaCenterX = (background.left + background.right) / 2
                        val areaCenterY = (background.top + background.bottom) / 2

                        val iconLeft = (areaCenterX - iconWidth / 2).toInt()
                        val iconTop = (areaCenterY - iconHeight / 2).toInt()
                        val iconRight = iconLeft + iconWidth
                        val iconBottom = iconTop + iconHeight

                        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        icon.draw(c)

                        // Save bounds per item
                        itemDeleteIconBounds[viewHolder.absoluteAdapterPosition] =
                            RectF(iconLeft.toFloat(), iconTop.toFloat(), iconRight.toFloat(), iconBottom.toFloat())

                        c.restore()

                        // Apply clamped translation
                        super.onChildDraw(
                            c,
                            recyclerView,
                            viewHolder,
                            clampedDx,
                            dY,
                            actionState,
                            isCurrentlyActive
                        )
                    } else {
                        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    }
                }

                fun getDeleteIconBounds(position: Int): RectF? = itemDeleteIconBounds[position]
            }

            val itemTouchHelper = ItemTouchHelper(swipeCallback)
            itemTouchHelper.attachToRecyclerView(rvNotifications)

            rvNotifications.addOnItemTouchListener(object : RecyclerView.SimpleOnItemTouchListener() {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    if (e.action == MotionEvent.ACTION_UP) {
                        val child = rv.findChildViewUnder(e.x, e.y)
                        if (child != null) {
                            val viewHolder = rv.getChildViewHolder(child)
                            val position = viewHolder.absoluteAdapterPosition
                            val iconBounds = swipeCallback.getDeleteIconBounds(position)
                            if (iconBounds != null && iconBounds.contains(e.x, e.y)) {
                                if (position != RecyclerView.NO_POSITION) {
                                    // ✅ Delete clicked
                                    Logger.e("Delete icon clicked at position: $position")
                                    notificationsAdapter.removeAt(position)
                                    return true
                                }
                            }
                        }
                    }
                    return false
                }
            })

        }
    }

    override fun listeners() {
        binding.apply {
            toolBar.ivBack onSafeClick { onBackPressedDispatcher.onBackPressed() }
        }
    }

}