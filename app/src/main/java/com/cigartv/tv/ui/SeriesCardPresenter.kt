package com.cigartv.tv.ui

import android.view.ViewGroup
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import coil.load
import com.cigartv.tv.R
import com.cigartv.tv.model.Series

/**
 * Renders a Series as a wide 16:9 card using its branded key art. Leanback's
 * ImageCardView handles D-pad focus scaling and the selected-state highlight, so we
 * keep this stock (no fragile subclassing) to minimize failure surface.
 */
class SeriesCardPresenter : Presenter() {

    companion object {
        private const val CARD_W = 480
        private const val CARD_H = 270
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        val cardView = ImageCardView(parent.context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
            setMainImageDimensions(CARD_W, CARD_H)
        }
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
        card.mainImageView?.load(art) {
            crossfade(true)
            placeholder(R.drawable.card_placeholder)
            error(R.drawable.card_placeholder)
        }
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder) {
        (viewHolder.view as ImageCardView).mainImage = null
    }
}

/** Category label derived from the series key, mirroring CategoryForSeries on Roku. */
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
