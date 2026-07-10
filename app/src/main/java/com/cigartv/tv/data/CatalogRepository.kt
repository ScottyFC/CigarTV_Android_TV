package com.cigartv.tv.data

import android.content.Context
import android.util.Log
import com.cigartv.tv.model.Catalog
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Single source of truth for the VOD catalog.
 *
 * Test mode (FreecastConfig.enabled == false): loads bundled assets/catalog.json.
 * Live mode: freecast API chain (stubbed pending confirmed response shapes + token).
 *
 * Logs the outcome (series count) and any exception so an empty grid is diagnosable.
 */
class CatalogRepository(private val context: Context) {

    companion object { private const val TAG = "CigarTV" }

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
            Log.d(TAG, "catalog.json read: ${json.length} chars")
            val catalog = gson.fromJson(json, Catalog::class.java)
            if (catalog == null) {
                Log.e(TAG, "Gson returned null for catalog.json")
                Catalog()
            } else {
                Log.d(TAG, "Parsed ${catalog.series.size} series")
                catalog
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load bundled catalog", e)
            Catalog()
        }
    }

    private fun loadFromApi(): Catalog = loadBundled()
}
