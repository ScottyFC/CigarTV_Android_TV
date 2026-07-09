package com.cigartv.tv.data

import com.google.gson.annotations.SerializedName

/**
 * Mirrors the Roku Freecast.brs stream resolver against the confirmed /streams
 * response shape (apicall.ts). Picks the best Android-playable stream and exposes
 * what PlaybackActivity needs to build a Media3 MediaItem.
 *
 * Android supports HLS, DASH, and Widevine DRM (via ExoPlayer/Media3). FairPlay
 * (the example's HLS DRM) is Apple-only and never selected here.
 */
data class FreecastStream(
    val type: String = "",
    @SerializedName("stream_format") val streamFormat: String = "",
    @SerializedName("is_drm") val isDrm: Boolean = false,
    val data: StreamData = StreamData()
)

data class StreamData(
    @SerializedName("media_url") val mediaUrl: String = "",
    @SerializedName("drm_type") val drmType: String = "",
    @SerializedName("drm_details") val drmDetails: DrmDetails? = null
)

data class DrmDetails(
    @SerializedName("server_url") val serverUrl: String = "",
    @SerializedName("cert_url") val certUrl: String? = null
)

/** A resolved, ready-to-play selection. */
data class ResolvedStream(
    val url: String,
    val format: String,          // "hls" | "dash"
    val widevineLicenseUrl: String? // non-null => wrap with Widevine DRM
)

object StreamResolver {

    /**
     * Selection preference for Android/ExoPlayer: HLS-clear, then DASH+Widevine,
     * then DASH-clear. FairPlay HLS is intentionally excluded (Apple-only).
     */
    fun pickBest(streams: List<FreecastStream>): ResolvedStream? {
        // 1. HLS clear
        streams.firstOrNull { it.streamFormat == "hls" && !it.isDrm }?.let {
            return ResolvedStream(it.data.mediaUrl, "hls", null)
        }
        // 2. DASH + Widevine
        streams.firstOrNull {
            it.streamFormat == "dash" && it.isDrm && it.data.drmType == "widevine"
        }?.let {
            return ResolvedStream(it.data.mediaUrl, "dash", it.data.drmDetails?.serverUrl)
        }
        // 3. DASH clear
        streams.firstOrNull { it.streamFormat == "dash" && !it.isDrm }?.let {
            return ResolvedStream(it.data.mediaUrl, "dash", null)
        }
        return null
    }
}
