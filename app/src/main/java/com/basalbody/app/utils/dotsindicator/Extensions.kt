package com.basalbody.app.utils.dotsindicator

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.core.graphics.createBitmap
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

internal fun View.setPaddingHorizontal(padding: Int) {
    setPadding(padding, paddingTop, padding, paddingBottom)
}

internal fun View.setPaddingVertical(padding: Int) {
    setPadding(paddingLeft, padding, paddingRight, padding)
}

internal fun View.setWidth(width: Int) {
    layoutParams.apply {
        this.width = width
        requestLayout()
    }
}

internal fun <T> ArrayList<T>.isInBounds(index: Int) = index in 0 until size

internal fun Context.getThemePrimaryColor(): Int {
    val value = TypedValue()
    this.theme.resolveAttribute(androidx.appcompat.R.attr.colorPrimary, value, true)
    return value.data
}

internal val ViewPager.isNotEmpty: Boolean get() = (adapter?.count ?: 0) > 0
internal val ViewPager2.isNotEmpty: Boolean get() = (adapter?.itemCount ?: 0) > 0
internal val ViewPager?.isEmpty: Boolean get() = this?.adapter?.count == 0
internal val ViewPager2?.isEmpty: Boolean get() = this?.adapter?.itemCount == 0

fun View.setBackgroundCompat(background: Drawable?) {
    this.background = background
}

@Composable
fun Dp.dpToPx() = with(LocalDensity.current) { this@dpToPx.toPx() }

@Composable
fun Int.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

@Composable
fun Float.pxToDp() = with(LocalDensity.current) { this@pxToDp.toDp() }

inline fun <reified T> String.toObjectTypeToken(): T? {
    return try {
        val type = object : TypeToken<T>() {}.type
        Gson().fromJson<T>(this, type)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun ImageView.loadPdfPreview(
    context: Context,
    pdfSource: Any, // Accepts Uri or String (URL)
    onError: ((Throwable) -> Unit)? = null
) {
    CoroutineScope(Dispatchers.Main).launch {
        try {
            val file = when (pdfSource) {
                is Uri -> saveUriToFile(context, pdfSource)
                is String -> downloadPdfFile(context, pdfSource)
                else -> throw IllegalArgumentException("Invalid PDF source type")
            }

            file?.let {
                val bitmap = renderPdfFirstPage(context, it)
                this@loadPdfPreview.setImageBitmap(bitmap)
            } ?: throw Exception("File creation failed")
        } catch (e: Exception) {
            e.printStackTrace()
            onError?.invoke(e)
        }
    }
}

suspend fun saveUriToFile(context: Context, uri: Uri): File? = withContext(Dispatchers.IO) {
    return@withContext try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File.createTempFile("temp_pdf_uri", ".pdf", context.cacheDir)
        inputStream?.use { input ->
            FileOutputStream(file).use { output ->
                input.copyTo(output)
            }
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun downloadPdfFile(context: Context, url: String): File? = withContext(Dispatchers.IO) {
    return@withContext try {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        val input = connection.inputStream
        val file = File.createTempFile("temp_pdf_url", ".pdf", context.cacheDir)
        input.use { inp ->
            FileOutputStream(file).use { out ->
                inp.copyTo(out)
            }
        }
        file
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

suspend fun renderPdfFirstPage(context: Context, file: File): Bitmap = withContext(Dispatchers.IO) {
    val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    val renderer = PdfRenderer(fileDescriptor)
    val page = renderer.openPage(0)

    val bitmap = createBitmap(page.width, page.height)
    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

    page.close()
    renderer.close()
    fileDescriptor.close()
    bitmap
}