package com.app.mediaplayer.utils

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object FileUtils {

    fun getFileNameFromUri(context: Context, uri: Uri): String {
        var result = ""
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result.isEmpty()) {
            result = uri.path?.let { path ->
                path.substring(path.lastIndexOf('/') + 1)
            } ?: "unknown"
        }
        return result
    }

    fun getFileSizeFromUri(context: Context, uri: Uri): Long {
        var size: Long = 0
        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    size = it.getLong(it.getColumnIndexOrThrow(OpenableColumns.SIZE))
                }
            }
        }
        return size
    }

    fun createPrivateFolder(context: Context, folderName: String): File? {
        return try {
            val baseDir = context.getExternalFilesDir(null)
            val folder = File(baseDir, folderName)
            if (!folder.exists()) {
                folder.mkdirs()
            }
            folder
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun copyFileToPrivateFolder(context: Context, uri: Uri, destinationFolder: String): File? {
        return try {
            val folder = createPrivateFolder(context, destinationFolder)
            if (folder != null) {
                val fileName = getFileNameFromUri(context, uri)
                val destinationFile = File(folder, fileName)
                
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(destinationFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                destinationFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getMediaUriFromId(context: Context, id: Long, isVideo: Boolean): Uri {
        val contentUri = if (isVideo) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        return ContentUris.withAppendedId(contentUri, id)
    }

    fun formatFileSize(bytes: Long): String {
        val kb = bytes / 1024
        val mb = kb / 1024
        val gb = mb / 1024
        
        return when {
            gb >= 1 -> String.format("%.2f GB", gb)
            mb >= 1 -> String.format("%.2f MB", mb)
            kb >= 1 -> String.format("%.2f KB", kb)
            else -> "$bytes B"
        }
    }

    fun isPrivateFolderEnabled(context: Context): Boolean {
        val folder = File(context.getExternalFilesDir(null), Constants.PRIVACY_FOLDER_NAME)
        return folder.exists() && folder.isDirectory
    }

    fun scanPrivateFolder(context: Context): List<File> {
        val folder = File(context.getExternalFilesDir(null), Constants.PRIVACY_FOLDER_NAME)
        val files = mutableListOf<File>()
        
        if (folder.exists() && folder.isDirectory) {
            folder.listFiles()?.forEach { file ->
                if (file.isFile) {
                    files.add(file)
                }
            }
        }
        
        return files
    }
}
