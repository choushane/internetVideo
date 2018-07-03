package com.shane.internetvideo

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class VideoAdapter(paramContext: Context, paramArrayList: ArrayList<VideoItem>): BaseAdapter(){
    private val mContext: Context = paramContext
    private val mInflater: LayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private val mVideoList: ArrayList<VideoItem> = paramArrayList

    override fun getView(paramInt : Int, paramView : View?, paramViewGroup: ViewGroup?): View {
        val view: View
        val holder: ContentHolder

        if (paramView == null) {
            view = mInflater.inflate(R.layout.video_item, paramViewGroup, false)
            holder = ContentHolder(view)
            view.tag = holder
        } else {
            view = paramView
            holder = view.tag as ContentHolder
        }
        holder.videoName.text = mVideoList[paramInt].videoName
        holder.videoUrl = mVideoList[paramInt].videoUrl

        var bitmap = ImageUtils.getBitMapForMemory(mVideoList[paramInt].videoThumbnail)
        if(bitmap == null) {
            bitmap = ImageUtils.getBitMapForDisk(mContext, mVideoList[paramInt].videoThumbnail)
            if(bitmap != null){
                ImageUtils.addBitmapToMemory(mVideoList[paramInt].videoThumbnail, bitmap)
            }
        }

        if (bitmap != null){
            holder.videoImage.setImageBitmap(bitmap)
        }


        return view
    }

    override fun getItemId(paramInt: Int): Long {
        return paramInt.toLong()
    }

    override fun getCount(): Int {
        return mVideoList.size
    }

    override fun getItem(paramInt: Int): Any {
        return mVideoList[paramInt]
    }

    private class ContentHolder(viewItem: View){
        var videoName: TextView = viewItem.findViewById(R.id.videoName)
        var videoImage: ImageView = viewItem.findViewById(R.id.thumbnail)
        var videoUrl: String = ""
    }

}