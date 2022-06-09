package com.lukakordzaia.core.utils

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import okhttp3.*
import java.io.File
import java.io.IOException

object DownloadHelper : BroadcastReceiver() {
    private const val TAG = "DOWNLOAD HELPER"

    private val listeners = HashSet<OnDownloadFinishedListener>()

    @SuppressWarnings("LongParameterList")
    fun downloadFromUrl(
        context: Context,
        url: String,
        fileName: String,
        title: String?,
        description: String?,
        usePublic: Boolean = false,
        callback: ((String?) -> Unit)? = null
    ) {
        if (callback != null) {
            okHttpDownload(
                context = context,
                url = url,
                fileName = fileName,
                usePublic = usePublic,
                callback = callback
            )
            return
        }

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        try {
            manager.enqueue(DownloadManager.Request(Uri.parse(url)).apply {
                setDescription(description.orEmpty())
                setTitle(title.orEmpty())
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    allowScanningByMediaScanner()
                }
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                if (usePublic) {
                    setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                } else {
                    setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
                }
                Log.d(TAG, "Downloading content from: $url")
            })
        } catch (e: SecurityException) {
            Log.e(TAG, e.toString())
        }
    }

    private fun okHttpDownload(
        context: Context,
        url: String,
        fileName: String,
        usePublic: Boolean = false,
        callback: ((String?) -> Unit)
    ) {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback.invoke(null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    callback.invoke(null)
                    return
                }

                val file = File(
                    if (usePublic) {
                        context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                    } else {
                        context.filesDir
                    }, fileName)
                response.body?.let {
                    file.writeBytes(it.bytes())
                }
                callback.invoke(file.absolutePath.replace("file:/", ""))
            }
        })
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val downloadId = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId == null || downloadId < 0) {
            return
        }
        notifyListeners(downloadId, isSuccessful(downloadId, context))
    }

    private fun notifyListeners(downloadId: Long, success: Boolean) {
        listeners.forEach { it.onDownloadFinished(downloadId, success) }
    }

    private fun isSuccessful(downloadId: Long, context: Context): Boolean {
        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val cursor = manager.query(DownloadManager.Query().setFilterById(downloadId))
        val returnValue = (cursor.moveToFirst()
                && cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL)
        cursor.close()
        return returnValue
    }

    interface OnDownloadFinishedListener {
        fun onDownloadFinished(downloadId: Long, success: Boolean)
    }
}

fun startPackageActivity(fragment: Fragment, _file: File, catch: () -> Unit) {
    var file = _file
    if (file.path.startsWith("file:/")) {
        file = File(file.path.substring("file:/".length))
    }

    try {
        val providerUri = FileProvider.getUriForFile(fragment.requireContext(), "com.lukakordzaia.streamflow.provider", file)
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(getFileExtension(file.name))
        val intent = ShareCompat.IntentBuilder(fragment.requireActivity())
            .setStream(providerUri)
            .setType(mimeType)
            .intent.setAction(Intent.ACTION_VIEW)
            .setDataAndType(providerUri, mimeType)
            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        fragment.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        catch.invoke()
    }
}

private fun getFileExtension(fileName: String): String = fileName.substring(fileName.lastIndexOf(".") + 1)