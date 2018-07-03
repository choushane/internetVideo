package com.shane.internetvideo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.os.Environment.isExternalStorageRemovable
import android.text.TextUtils
import android.util.Log
import android.util.LruCache
import java.io.File
import java.io.FileOutputStream

class ImageUtils {
    companion object {
        private val TAG = ImageUtils::class.java.simpleName
        /* --- Memory Cache --- */
        private val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        private val cacheSize = maxMemory / 10
        private var mMemoryCache: LruCache<String, Bitmap> = LruCache(cacheSize)

        /* --- disk Cache --- */
        private const val DISK_CACHE_DIR = "/thumbnails/.thumb"
        private var filePath: String = ""

        fun addBitmapToMemory(key: String, bitmap: Bitmap?){
            if (bitmap == null) return
            mMemoryCache.put(key, bitmap)
        }

        fun addBitmapToDisk(context: Context, key: String, bitmap: Bitmap?){
            if(filePath.isEmpty()) filePath = getDiskCacheDir(context, DISK_CACHE_DIR)
            if (bitmap != null || TextUtils.isEmpty(filePath)){
                val format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG
                val file = File(filePath + File.separator + key)
                val outPut = FileOutputStream(file)
                Log.e(TAG, key)
                if(bitmap!!.compress(format, 70, outPut)){
                    outPut.flush()
                    outPut.close()
                    Log.e(TAG, "finish")
                }
            }
        }

        fun getBitMapForMemory(key: String): Bitmap?{
            return mMemoryCache.get(key)
        }

        fun getBitMapForDisk(context: Context, key: String): Bitmap?{
            if(filePath.isEmpty()) filePath = getDiskCacheDir(context, DISK_CACHE_DIR)
            val file = File(filePath + File.separator + key)
            if(file.exists()) {
                return BitmapFactory.decodeFile(filePath + File.separator + key)
            }
            return null
        }

        private fun getDiskCacheDir(context: Context, uniqueName: String): String {
            val cachePath: String = if(Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
                    || !isExternalStorageRemovable()) context.externalCacheDir.path
                    else context.cacheDir.path
            val path: String = cachePath + File.separator + uniqueName
            val dir = File(path)
            if(!dir.exists()) dir.mkdirs()
            return path
        }
    }
}