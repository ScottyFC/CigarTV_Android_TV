package com.cigartv.tv.data

import android.content.Context
import com.cigartv.tv.model.Catalog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Single source of truth for the VOD catalog.
 *
 * Test mode (FreecastConfig.enabled == false): loads the bundled
 * assets/catalog.json - the same file the Roku app ships, generated from the
 * production episode CSV. Fully functional offline for UI/UX testing.
 *
 * Live mode (enabled + apiKey): fetches the catalog from the freecast API
 * (shows -> seasons -> episodes). Left as a clearly-marked TODO because the
 * show/episodes JSON response shapes aren't confirmed yet - the streams shape is.
 */
class CatalogRepository(private val context: Context) {

    private val gson = Gson()

    suspend fun loadCatalog(): Catalog = withContext(Dispatchers.IO) {
        if (FreecastConfig.enabled && FreecastConfig.apiKey.isNotEmpty()) {
            loadFromApi()
        } else {
            loadBundled()
        }
    }

    private fun loadBundled(): Catalog {
        return try {
            val json = context.assets.open("catalog.json")
                .bufferedReader()
                .use { it.readText() }
            gson.fromJson(json, Catalog::class.java) ?: Catalog()
        } catch (e: Exception) {
            Catalog()
        }
    }

    /**
     * TODO(plug-in-at-test): implement the freecast catalog chain once the show and
     * episodes endpoint JSON shapes are confirmed and a token is available:
     *   1. for each show slug -> GET FreecastConfig.showUrl(slug)  => season ids
     *   2. for each season   -> GET FreecastConfig.episodesUrl(...)  => episodes
     * Assemble into the same Catalog/Series/Episode model. Falls back to bundled
     * for now so the app always has content.
     */
    private fun loadFromApi(): Catalog {
        return loadBundled()
    }
}
