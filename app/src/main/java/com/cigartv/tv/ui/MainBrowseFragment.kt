package com.cigartv.tv.ui

import android.content.Intent
import android.os.Bundle
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
 * The "Originals" browse screen. Uses Leanback's BrowseSupportFragment, which gives
 * D-pad navigation, focus scaling, and the row/grid layout out of the box. Series
 * render as branded key-art cards; clicking one opens the episode guide.
 */
class MainBrowseFragment : BrowseSupportFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        title = getString(R.string.browse_title)
        headersState = HEADERS_DISABLED          // single grid, no side headers
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
            val catalog = repo.loadCatalog()
            val rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
            val cardAdapter = ArrayObjectAdapter(SeriesCardPresenter())
            catalog.series.forEach { cardAdapter.add(it) }
            val header = HeaderItem(0, getString(R.string.browse_title))
            rowsAdapter.add(ListRow(header, cardAdapter))
            adapter = rowsAdapter
        }
    }
}
