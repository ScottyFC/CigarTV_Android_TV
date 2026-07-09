package com.cigartv.tv.player

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.cigartv.tv.R
import com.cigartv.tv.data.FreecastConfig
import com.cigartv.tv.model.Episode

/**
 * Fullscreen playback (Media3/ExoPlayer). No overlay chrome beyond the default
 * transport controls, matching the Roku fullscreen player.
 *
 * Test mode: plays the episode's direct MP4 (from the bundled catalog).
 * Live mode: would resolve via the freecast /streams endpoint using StreamResolver
 * and wrap Widevine DRM when required (wired in the data layer; hook here once keyed).
 */
class PlaybackActivity : FragmentActivity() {

    companion object {
        const val EXTRA_EPISODE = "extra_episode"
    }

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var episode: Episode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playback)
        playerView = findViewById(R.id.player_view)

        @Suppress("DEPRECATION")
        episode = (intent.getSerializableExtra(EXTRA_EPISODE) as? Episode) ?: run {
            finish(); return
        }
    }

    override fun onStart() {
        super.onStart()
        val exo = ExoPlayer.Builder(this).build()
        player = exo
        playerView.player = exo

        val item = buildMediaItem()
        exo.setMediaItem(item)
        exo.playWhenReady = true
        exo.prepare()
    }

    /**
     * Test mode uses the direct MP4. When FreecastConfig.enabled, this is where the
     * StreamResolver result would be turned into a DASH/HLS MediaItem (+ Widevine
     * DrmConfiguration when widevineLicenseUrl is present).
     */
    private fun buildMediaItem(): MediaItem {
        val url = episode.videoUrl
        return MediaItem.Builder()
            .setUri(Uri.parse(url))
            .setMimeType(MimeTypes.VIDEO_MP4)
            .build()
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }
}
