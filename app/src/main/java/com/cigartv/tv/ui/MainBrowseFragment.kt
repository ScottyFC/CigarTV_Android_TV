package com.cigartv.tv.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.lifecycle.lifecycleScope
import com.cigartv.tv.R
import com.cigartv.tv.data.CatalogRepository
import com.cigartv.tv.model.Series
import kotlinx.coroutines.launch

/**
 * The "Originals" browse screen (Leanback BrowseSupportFragment).
 *
 * Setup is done in onViewCreated (onActivityCreated is deprecated and its timing is
 * unreliable in current Fragment versions). Catalog loading logs its result and any
 * exception so an empty grid is diagnosable rather than silent.
 */
class MainBrowseFragment : BrowseSupportFragment() {

    companion object { private const val TAG = "CigarTV" }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        title = getString(R.string.browse_title)
        headersState = HEADERS_DISABLED
        isHeadersTransitionOnBackEnabled = false
        brandColor = resources.getColor(R.color.charcoal, null)
        searchAffordanceColor = resources.getColor(R.color.ember, null)

        onItemViewClickedListener = OnItemViewClickedListener { _, item, _, _ ->
            if (item is Series) {
                val intent = Intent(requireContext(), SeriesDetailActivity::class.java)
                intent.putExtra(SeriesDetailActivity.EXTRA_SERIES, item)
                startActivity(intent)
            }
        }

        loadCatalog()
    }

    private fun loadCatalog() {
        val repo = CatalogRepository(requireContext())
        lifecycleScope.launch {
            try {
                val catalog = repo.loadCatalog()
                Log.d(TAG, "Catalog loaded: ${catalog.series.size} series")

                val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
                val cardAdapter = ArrayObjectAdapter(SeriesCardPresenter())
                catalog.series.forEach { cardAdapter.add(it) }

                val header = HeaderItem(0, getString(R.string.browse_title))
                rowsAdapter.add(ListRow(header, cardAdapter))
                adapter = rowsAdapter

                if (catalog.series.isEmpty()) {
                    Log.e(TAG, "Catalog parsed but series list is EMPTY")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Catalog load failed", e)
            }
        }
    }
}
