package com.enb.selde.draw

import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import android.view.View
import android.widget.SeekBar
import androidx.activity.viewModels
import com.enb.selde.R
import com.enb.selde.R.layout.activity_drawing
import com.enb.selde.databinding.ActivityDrawingBinding
import com.enb.selde.utils.BaseActivity
import com.enb.selde.utils.RxBus
import com.enb.selde.utils.RxEvent
import kotlinx.android.synthetic.main.activity_drawing.*
import kotlinx.android.synthetic.main.color_palette_view.*

class DrawingActivity : BaseActivity<ActivityDrawingBinding>() {

    companion object {
        private const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102
    }

    override val layoutResourceId = activity_drawing
    private val viewModel: DrawingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewDataBinding.viewModel = viewModel
        viewDataBinding.lifecycleOwner = this

        viewModel.verifyStoragePermissions(this)
        viewModel.imgUrl.set("https://stock-image-testserver.s3.ap-northeast-2.amazonaws.com/joboa1.jpg")

        addDisposable(RxBus.listen(RxEvent.EventIsLandscape::class.java)
            .subscribe {
                if (it.mode) {
                    requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }
            })

        addDisposable(RxBus.listen(RxEvent.EventBackgroundImg::class.java)
            .subscribe {
                viewModel.backgroundBitmap.set(it.img)
            })

        image_close_drawing.setOnClickListener {
            finish()
        }

        fab_send_drawing.setOnClickListener {
            val bitmap = draw_view.getBitmap(viewModel.backgroundBitmap.get() != null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (viewModel.addDrawToGalleryMediaStore(this, bitmap)) {
                    showToast("Signature saved into the Gallery")
                } else {
                    showToast("Unable to store the signature")
                }
            } else {
                showToast("low version")
                if (viewModel.addDrawToGallery(this, bitmap)) {
                    showToast("Signature saved into the Gallery")
                } else {
                    showToast("Unable to store the signature")
                }
            }
        }

        setUpDrawTools()
        colorSelector()
        setPaintAlpha()
        setPaintWidth()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isEmpty()
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED
                ) {
                    showToast("Cannot write images to external storage")
                    finish()
                }
            }
        }
    }

    private fun setUpDrawTools() {
        circle_view_opacity.setCircleRadius(100f)
        image_draw_eraser.setOnClickListener {
            draw_view.toggleEraser()
            image_draw_eraser.isSelected = draw_view.isEraserOn
            toggleDrawTools(draw_tools, false)
        }
        image_draw_eraser.setOnLongClickListener {
            draw_view.clearCanvas()
            toggleDrawTools(draw_tools, false)
            true
        }
        image_draw_width.setOnClickListener {
            if (draw_tools.translationY == (56).toPx) {
                toggleDrawTools(draw_tools, true)
            } else if (draw_tools.translationY == (0).toPx && seekBar_width.visibility == View.VISIBLE) {
                toggleDrawTools(draw_tools, false)
            }
            circle_view_width.visibility = View.VISIBLE
            circle_view_opacity.visibility = View.GONE
            seekBar_width.visibility = View.VISIBLE
            seekBar_opacity.visibility = View.GONE
            draw_color_palette.visibility = View.GONE
        }
        image_draw_opacity.setOnClickListener {
            if (draw_tools.translationY == (56).toPx) {
                toggleDrawTools(draw_tools, true)
            } else if (draw_tools.translationY == (0).toPx && seekBar_opacity.visibility == View.VISIBLE) {
                toggleDrawTools(draw_tools, false)
            }
            circle_view_width.visibility = View.GONE
            circle_view_opacity.visibility = View.VISIBLE
            seekBar_width.visibility = View.GONE
            seekBar_opacity.visibility = View.VISIBLE
            draw_color_palette.visibility = View.GONE
        }
        image_draw_color.setOnClickListener {
            if (draw_tools.translationY == (56).toPx) {
                toggleDrawTools(draw_tools, true)
            } else if (draw_tools.translationY == (0).toPx && draw_color_palette.visibility == View.VISIBLE) {
                toggleDrawTools(draw_tools, false)
            }
            circle_view_width.visibility = View.GONE
            circle_view_opacity.visibility = View.GONE
            seekBar_width.visibility = View.GONE
            seekBar_opacity.visibility = View.GONE
            draw_color_palette.visibility = View.VISIBLE
        }
        image_draw_undo.setOnClickListener {
            draw_view.undo()
            toggleDrawTools(draw_tools, false)
        }
        image_draw_redo.setOnClickListener {
            draw_view.redo()
            toggleDrawTools(draw_tools, false)
        }
    }

    private fun toggleDrawTools(view: View, showView: Boolean = true) {
        if (showView) {
            view.animate().translationY((0).toPx)
        } else {
            view.animate().translationY((56).toPx)
        }
    }

    private fun colorSelector() {
        image_color_black.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_black, null)
            draw_view.setColor(color)
            circle_view_opacity.setColor(color)
            circle_view_width.setColor(color)
            scaleColorView(image_color_black)
        }
        image_color_red.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_red, null)
            draw_view.setColor(color)
            circle_view_opacity.setColor(color)
            circle_view_width.setColor(color)
            scaleColorView(image_color_red)
        }
        image_color_yellow.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_yellow, null)
            draw_view.setColor(color)
            circle_view_opacity.setColor(color)
            circle_view_width.setColor(color)
            scaleColorView(image_color_yellow)
        }
        image_color_green.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_green, null)
            draw_view.setColor(color)
            circle_view_opacity.setColor(color)
            circle_view_width.setColor(color)
            scaleColorView(image_color_green)
        }
        image_color_blue.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_blue, null)
            draw_view.setColor(color)
            circle_view_opacity.setColor(color)
            circle_view_width.setColor(color)
            scaleColorView(image_color_blue)
        }
        image_color_pink.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_pink, null)
            draw_view.setColor(color)
            circle_view_opacity.setColor(color)
            circle_view_width.setColor(color)
            scaleColorView(image_color_pink)
        }
        image_color_brown.setOnClickListener {
            val color = ResourcesCompat.getColor(resources, R.color.color_brown, null)
            draw_view.setColor(color)
            circle_view_opacity.setColor(color)
            circle_view_width.setColor(color)
            scaleColorView(image_color_brown)
        }
    }

    private fun scaleColorView(view: View) {
        //reset scale of all views
        image_color_black.scaleX = 1f
        image_color_black.scaleY = 1f

        image_color_red.scaleX = 1f
        image_color_red.scaleY = 1f

        image_color_yellow.scaleX = 1f
        image_color_yellow.scaleY = 1f

        image_color_green.scaleX = 1f
        image_color_green.scaleY = 1f

        image_color_blue.scaleX = 1f
        image_color_blue.scaleY = 1f

        image_color_pink.scaleX = 1f
        image_color_pink.scaleY = 1f

        image_color_brown.scaleX = 1f
        image_color_brown.scaleY = 1f

        //set scale of selected view
        view.scaleX = 1.5f
        view.scaleY = 1.5f
    }

    private fun setPaintWidth() {
        seekBar_width.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                draw_view.setStrokeWidth(progress.toFloat())
                circle_view_width.setCircleRadius(progress.toFloat())
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setPaintAlpha() {
        seekBar_opacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                draw_view.setAlpha(progress)
                circle_view_opacity.setAlpha(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private val Int.toPx: Float
        get() = (this * Resources.getSystem().displayMetrics.density)
}
