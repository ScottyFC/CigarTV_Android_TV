package com.cigartv.tv.data

/**
 * Freecast API configuration - the Android mirror of the Roku app's FreecastConfig().
 * Integration is a two-value change: set [apiKey] and flip [enabled].
 *
 * SECURITY NOTE: a client app cannot hold a secret that can't be extracted. For
 * production, point [baseUrl] at a backend proxy that injects the real key
 * server-side and leave [apiKey] empty. See the repo README security section.
 */
object FreecastConfig {

    const val baseUrl = "https://api-services.freecast.com/guide/api/v5/watch-freecast-com/web/vod"

    // TODO(plug-in-at-test): auth token / API key (sent as Bearer). Empty => the app
    // loads the bundled assets/catalog.json instead.
    const val apiKey = ""

    // Master switch. false => bundled catalog + direct MP4 playback (test mode).
    // true (with apiKey set) => live catalog + stream resolution via freecast.
    const val enabled = false

    // Live linear channel (Amagi HLS) + EPG.
    const val liveStreamUrl =
        "https://amg30862-amg30862c1-amgplt0065.playout.now3.amagi.tv/ts-us-e2-n2/playlist/amg30862-amg30862c1-amgplt0065/playlist.m3u8"
    const val epgUrl =
        "https://d31l2nn7dlh4li.cloudfront.net/amg30862/epg_deliveries/amgplt0065/amg30862c1/amg30862c1.xml"

    fun showUrl(slug: String) = "$baseUrl/shows/$slug"
    fun episodesUrl(slug: String, seasonId: String) =
        "$baseUrl/shows/$slug/episodes/?season_id=$seasonId"
    fun streamsUrl(streamSlug: String) =
        "$baseUrl/episodes/$streamSlug/streams?stream_format=all"
}
