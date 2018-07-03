package com.shane.internetvideo

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.trackselection.*
import com.google.android.exoplayer2.ui.SimpleExoPlayerView
import com.google.android.exoplayer2.upstream.BandwidthMeter
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class Player: AppCompatActivity(), Player.EventListener {

    companion object {
        private val TAG = Player::class.java.simpleName
    }
    private lateinit var simpleExoPlayerView: SimpleExoPlayerView
    private var player: SimpleExoPlayer? = null
    private lateinit var mediaDataSourceFactory: DataSource.Factory
    private lateinit var extractorsFactory: DefaultExtractorsFactory
    private var contentPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player)

        val videoUrl = Uri.parse(intent.extras.getString("videoUrl"))

        //Create a default TrackSelector
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)

        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, getString(R.string.app_name)), DefaultBandwidthMeter())

        extractorsFactory = DefaultExtractorsFactory()

        //Create the player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, DefaultLoadControl())
        simpleExoPlayerView = SimpleExoPlayerView(this)
        simpleExoPlayerView = findViewById(R.id.player_view)

        //Set media controller
        simpleExoPlayerView.useController = false
        simpleExoPlayerView.requestFocus()

        // Bind the player to the view.
        simpleExoPlayerView.player = player

        val contentMediaSource = buildMediaSource(videoUrl)

        player?.prepare(contentMediaSource)
        player?.seekTo(contentPosition)

        player?.addListener(this)

        player?.playWhenReady = true
    }

    private fun buildMediaSource(uri: Uri): MediaSource{
        val type = Util.inferContentType(uri)
        if (type == 2) {
            return HlsMediaSource(uri, mediaDataSourceFactory, 1, null, null)
        }else if (type == 3) {
            return ExtractorMediaSource(uri, mediaDataSourceFactory, extractorsFactory, null, null)
        }

        throw IllegalStateException("Unsupported type: " + type.toString())
    }

    private fun reset(){
        contentPosition = if (player != null ) player!!.contentPosition else 0
        player?.release()
        player = null
    }

    private fun release(){
        player?.release()
        player = null
        finish()
    }

    override fun onPause() {
        super.onPause()
        reset()
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if(playWhenReady && playbackState == Player.STATE_ENDED)
        {
            Log.e(TAG, "ended")
            release()
        }
    }

    override fun onLoadingChanged(isLoading: Boolean) {

    }

    override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {

    }

    override fun onPlayerError(error: ExoPlaybackException?) {
        error?.printStackTrace()
        Log.e(TAG, "error")
        release()
    }

    override fun onPositionDiscontinuity(reason: Int) {

    }

    override fun onRepeatModeChanged(repeatMode: Int) {

    }

    override fun onSeekProcessed() {

    }

    override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {

    }

    override fun onTimelineChanged(timeline: Timeline?, manifest: Any?) {

    }

    override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {

    }
}