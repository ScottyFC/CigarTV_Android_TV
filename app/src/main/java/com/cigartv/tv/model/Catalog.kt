package com.cigartv.tv.model

import java.io.Serializable

/**
 * Catalog data model. Mirrors the structure used by the Roku app's catalog.json
 * (generated from the production episode CSV) and the freecast API, so the same
 * data source drives all platforms.
 */

data class Catalog(
    val series: List<Series> = emptyList()
)

data class Series(
    val seriesKey: String = "",
    val title: String = "",
    val description: String = "",
    val rating: String = "",
    val genres: String = "",
    val thumbnailUrl: String = "",
    val episodes: List<Episode> = emptyList()
) : Serializable {

    /** Distinct season numbers present, sorted ascending. */
    val seasons: List<String>
        get() = episodes.map { it.season }
            .distinct()
            .sortedBy { it.toIntOrNull() ?: 0 }

    fun episodesForSeason(season: String): List<Episode> =
        episodes.filter { it.season == season }
            .sortedBy { it.episode.toIntOrNull() ?: 0 }
}

data class Episode(
    val title: String = "",
    val description: String = "",
    val longDescription: String = "",
    val season: String = "1",
    val episode: String = "1",
    val rating: String = "",
    val durationMinutes: Int = 0,
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val streamSlug: String = "",
    val subtitles: Subtitles = Subtitles()
) : Serializable {

    /** Metadata line for the guide, e.g. "EPISODE 3  -  22 MIN  -  TV-MA". */
    fun metaLine(): String {
        val parts = mutableListOf("EPISODE $episode")
        if (durationMinutes > 0) parts.add("$durationMinutes MIN")
        if (rating.isNotEmpty()) parts.add(rating)
        return parts.joinToString("  -  ")
    }
}

data class Subtitles(
    val en: String = "",
    val es: String = "",
    val de: String = "",
    val pt: String = ""
) : Serializable
