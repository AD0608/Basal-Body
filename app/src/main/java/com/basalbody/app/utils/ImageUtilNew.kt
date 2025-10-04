package com.basalbody.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream


object ImageUtilNew {
    private const val EOF = -1
    private const val DEFAULT_BUFFER_SIZE = 1024 * 4

    @Throws(IOException::class)
    fun from(context: Context, uri: Uri): File? {
        val correctedUri = if (uri.scheme == "file") {
            // Fix invalid "file:/" URIs (strip scheme)
            Uri.fromFile(File(uri.path ?: return null))
        } else {
            uri
        }

        val inputStream = context.contentResolver.openInputStream(correctedUri)
            ?: throw IOException("Unable to open input stream from $correctedUri")

        val fileName = getFileName(context, correctedUri)
        val splitName = splitFileName(fileName)
        var tempFile = splitName[0]?.let { File.createTempFile(it, splitName[1]) }
        tempFile = tempFile?.let { rename(it, fileName) }
        tempFile?.deleteOnExit()

        FileOutputStream(tempFile).use { out ->
            copy(inputStream, out)
        }

        inputStream.close()
        return tempFile
    }


    private fun splitFileName(fileName: String?): Array<String?> {
        var name = fileName
        var extension: String? = ""
        val i = fileName!!.lastIndexOf(".")
        if (i != -1) {
            name = fileName.substring(0, i)
            extension = fileName.substring(i)
        }
        return arrayOf(name, extension)
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result!!.lastIndexOf(File.separator)
            if (cut != -1) {
                result = result.substring(cut + 1)
            }
        }
        return result
    }

    private fun rename(file: File, newName: String?): File? {
        val newFile = newName?.let { File(file.parent, it) }
        if (newFile != file) {
            if (newFile?.exists() == true && newFile.delete()) {
                Log.d("FileUtil", "Delete old $newName file")
            }
            if (newFile?.let { file.renameTo(it) } == true) {
                Log.d("FileUtil", "Rename file to $newName")
            }
        }
        return newFile
    }

    @Throws(IOException::class)
    private fun copy(input: InputStream, output: OutputStream?): Long {
        var count: Long = 0
        var n: Int
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        while (EOF != input.read(buffer).also { n = it }) {
            output!!.write(buffer, 0, n)
            count += n.toLong()
        }
        return count
    }


    fun saveBitmapToFile(file: File): File? {
        return try {
            // Decode bounds first to determine size
            val boundsOptions = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }

            FileInputStream(file).use { input ->
                BitmapFactory.decodeStream(input, null, boundsOptions)
            }

            // Determine inSampleSize for downscaling
            val REQUIRED_SIZE = 500
            var scale = 1
            while (
                boundsOptions.outWidth / scale / 2 >= REQUIRED_SIZE &&
                boundsOptions.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }

            val decodeOptions = BitmapFactory.Options().apply {
                inSampleSize = scale
            }

            // Decode actual bitmap
            val bitmap = FileInputStream(file).use { input ->
                BitmapFactory.decodeStream(input, null, decodeOptions)
            } ?: return null

            // Read EXIF orientation
            val exif = ExifInterface(file.absolutePath)
            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            // Apply rotation if needed
            val rotatedBitmap = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> bitmap.rotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> bitmap.rotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> bitmap.rotate(270f)
                else -> bitmap
            }

            // Save to original file
            FileOutputStream(file).use { output ->
                rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)
            }

            // Recycle unused bitmaps if needed
            if (rotatedBitmap != bitmap) {
                bitmap.recycle()
            }

            file

        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }
}


