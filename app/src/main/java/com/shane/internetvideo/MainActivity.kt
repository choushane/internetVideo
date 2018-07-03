package com.shane.internetvideo

import android.content.Intent
import android.content.res.AssetManager
import android.media.MediaMetadataRetriever
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.GridView
import org.w3c.dom.NodeList
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
    private var mVideoList: ArrayList<VideoItem> = ArrayList()
    private lateinit var mContentAdapter: VideoAdapter
    private lateinit var viewer: GridView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewer = findViewById(R.id.viwer)

        val assetsManager: AssetManager = this.assets
        val data = assetsManager.open("tutorial.xml")

        mVideoList = parserXML(data)

        getThumb()

        mContentAdapter = VideoAdapter(this, mVideoList)
        viewer.numColumns = 2
        viewer.adapter = mContentAdapter
        viewer.onItemClickListener = adapterItemClickListener
    }

    private fun parserXML(inputStream: InputStream): ArrayList<VideoItem> {
        val dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
        val nodList: NodeList = dom.documentElement.getElementsByTagName("en").item(0).childNodes
        val contentList: ArrayList<VideoItem> = ArrayList()
        if (nodList.length > 0) {
            for (itemCount in 0..(nodList.length - 1)) {
                val nodeItem = nodList.item(itemCount)
                if (nodeItem.nodeName != "toturial") continue
                val videoName = nodeItem.attributes.getNamedItem("name").nodeValue
                val videoUrl = nodeItem.attributes.getNamedItem("url").nodeValue
                val key = Utils.stringToHash(videoUrl)
                contentList.add(VideoItem(videoName, videoUrl, key))
            }
        }
        return contentList
    }

    private fun getThumb(){
        Thread({
            val mediaMetadataRetriever = MediaMetadataRetriever()
            for (item in mVideoList){
                if(item.videoUrl.isEmpty()) continue
                val key = Utils.stringToHash(item.videoUrl)
                val diskImage = ImageUtils.getBitMapForDisk(this, key)
                if(diskImage != null){
                    ImageUtils.addBitmapToMemory(key, diskImage)
                    continue
                }
                try {
                    mediaMetadataRetriever.setDataSource(item.videoUrl, HashMap<String, String>())
                    ImageUtils.addBitmapToDisk(this, key, mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST))
                }catch (e: Exception){
                    e.printStackTrace()
                    Log.e(TAG, item.videoUrl)
                }
            }
            mediaMetadataRetriever.release()
        }).start()
    }

    private var adapterItemClickListener: AdapterView.OnItemClickListener = AdapterView.OnItemClickListener {
        _, _, position, _ ->
        val mIntent = Intent(this, Player::class.java)
        mIntent.putExtra("videoUrl", mVideoList[position].videoUrl)
        startActivity(mIntent)
    }
}
