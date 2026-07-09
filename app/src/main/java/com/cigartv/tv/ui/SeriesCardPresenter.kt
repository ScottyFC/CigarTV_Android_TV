package com.cigartv.tv.ui

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import coil.load
import com.cigartv.tv.R
import com.cigartv.tv.model.Series

/**
 * Renders a Series as a wide 16:9 card using its branded key art. Leanback handles
 * D-pad focus; we add the ember focus frame + a subtle scale via the card's own
 * selection state, matching the Roku ShowCard treatment.
 */
class SeriesCardPresenter : Presenter() {

    companion object {
        private const val CARD_W = 480
        private const val CARD_H = 270
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                findViewById<ViewGroup>(androidx.leanback.R.id.info_field)?.visibility =
                    if (selected) android.view.View.VISIBLE else android.view.View.GONE
                updateBackground(selected)
                super.setSelected(selected)
            }

            fun updateBackground(selected: Boolean) {
                val res = resources
                setInfoAreaBackgroundColor(
                    if (selected) res.getColor(R.color.leather_deep, null)
                    else res.getColor(R.color.smoke_800, null)
                )
            }
        }
        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        cardView.setMainImageDimensions(CARD_W, CARD_H)
        return ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {
        val series = item as Series
        val card = viewHolder.view as ImageCardView
        card.titleText = series.title
        card.contentText = series.category()
        val art = series.thumbnailUrl.ifEmpty {
            series.episodes.firstOrNull()?.thumbnailUrl ?: ""
        }
        card.mainImageView.load(art) {
            crossfade(true)
            placeholder(R.drawable.card_placeholder)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as ImageCardView).mainImage = null
    }
}

/** Category label derived from genres/rating, mirroring CategoryForSeries on Roku. */
fun Series.category(): String {
    val key = seriesKey.uppercase()
    return when {
        key.contains("BEHIND") -> "Talk Show"
        key.contains("LOUNGE") -> "Documentary"
        key.contains("BURN") -> "Reviews"
        key.contains("UNROLLED") -> "Talk Show"
        key.contains("ESSENTIAL") -> "Reviews"
        key.contains("DOC") -> "Documentary"
        else -> "Series"
    }
}
