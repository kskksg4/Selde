package com.enb.selde.draw

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.databinding.ObservableField
import com.enb.selde.utils.BaseViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

class DrawingViewModel: BaseViewModel() {

    companion object {
        private const val PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 102
        private const val TAG = "MediaStore"
    }

    private val fileName = String.format("Selde_%d.jpg", System.currentTimeMillis())
    var imgUrl = ObservableField<String>("")
    var backgroundBitmap = ObservableField<Bitmap>()

    @RequiresApi(Build.VERSION_CODES.Q)
    fun addDrawToGalleryMediaStore(context: Context, draw: Bitmap): Boolean{
        val values = ContentValues().apply{
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Selde")
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val item = context.contentResolver.insert(collection, values)
        var result = false

        if (item != null) {
            context.contentResolver.openFileDescriptor(item, "w", null).use {
                FileOutputStream(it!!.fileDescriptor).use { outputStream ->
                    try {
                        val resizeBitmap = resizeBitmap(draw.width, backgroundBitmap.get()!!)
//                        saveBitmapToJpgMediaStore(bitmapOverlay(resizeBitmap, draw), outputStream)

                        result = true
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    outputStream.close()
                }
            }
        }

        values.clear()
        values.put(MediaStore.Images.Media.IS_PENDING, 0)
        if (item != null) {
            context.contentResolver.update(item, values, null, null)
        }
        Log.d(TAG, "Done inserting a file to Content provider")

        return result
    }

    fun addDrawToGallery(context: Context, draw: Bitmap): Boolean {
        var result = false

        try {
            val imageDir = "${Environment.DIRECTORY_PICTURES}/Selde/"
            val path = Environment.getExternalStoragePublicDirectory(imageDir)
            Log.e("path",path.toString())
            val file = File(path, fileName)
            path.mkdirs()
            file.createNewFile()

            saveBitmapToJPG(draw, file)
            scanMediaFile(context, file)

            result = true
        }catch (e: IOException){
            e.printStackTrace()
        }

        return result
    }

    private fun saveBitmapToJpgMediaStore(draw: Bitmap, stream: FileOutputStream) {
        draw.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        stream.close()
    }



    private fun saveBitmapToJPG(draw: Bitmap, file: File) {
        val stream = FileOutputStream(file)
        draw.compress(Bitmap.CompressFormat.JPEG, 80, stream)

        stream.flush()
        stream.close()
    }

    private fun scanMediaFile(context: Context, photo: File){
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri: Uri = Uri.fromFile(photo)

        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    private fun bitmapOverlay(bgBitmap: Bitmap, drawBitmap: Bitmap): Bitmap{
        val bmOverlay = Bitmap.createBitmap(bgBitmap.width, bgBitmap.height, bgBitmap.config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(bgBitmap, 0f, 0f, null)
        canvas.drawBitmap(drawBitmap, 0f, 0f, null)

        return bmOverlay
    }

    private fun resizeBitmap(scaleSize: Int, bitmap: Bitmap): Bitmap{
        // https://codeexa.com/overlay-two-bitmaps-in-android/
        val resizedBitmap: Bitmap
        val originalWidth: Int = bitmap.width
        val originalHeight: Int = bitmap.height
        val newWidth: Int
        val newheight: Int
        val multFactor: Float

        when {
            originalHeight > originalWidth -> {
                newheight = scaleSize
                multFactor = originalWidth.toFloat() / originalHeight.toFloat()
                newWidth = (newheight * multFactor).toInt()
            }
            originalHeight < originalWidth -> {
                newWidth = scaleSize
                multFactor = originalHeight.toFloat() / originalWidth.toFloat() // 0.62543832
                newheight = (newWidth * multFactor).toInt()
            }
            else -> {
                newWidth = scaleSize
                newheight = scaleSize
            }
        }

        resizedBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newheight, false)

        return resizedBitmap
    }

    fun verifyStoragePermissions(activity: Activity){
        val permission = ActivityCompat.checkSelfPermission(
            activity,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
            )
        }
    }
}