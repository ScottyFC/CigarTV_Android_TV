package com.cigartv.tv.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.cigartv.tv.R
import com.cigartv.tv.model.Episode
import com.cigartv.tv.model.Series
import com.cigartv.tv.player.PlaybackActivity

/**
 * Episode guide for a single series. Pinned header (logo area + description), a
 * season selector, and a scrolling episode list (thumbnail + meta + title +
 * description) - the Android equivalent of the Roku episode-guide screen.
 */
class SeriesDetailActivity : FragmentActivity() {

    companion object {
        const val EXTRA_SERIES = "extra_series"
    }

    private lateinit var series: Series
    private var currentSeason: String = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_series_detail)

        @Suppress("DEPRECATION")
        series = (intent.getSerializableExtra(EXTRA_SERIES) as? Series) ?: run {
            finish(); return
        }

        findViewById<android.widget.TextView>(R.id.detail_title).text = series.title
        findViewById<android.widget.TextView>(R.id.detail_description).text = series.description

        val seasons = series.seasons.ifEmpty { listOf("1") }
        currentSeason = seasons.first()

        setupSeasonSelector(seasons)
        setupEpisodeList()
    }

    private fun setupSeasonSelector(seasons: List<String>) {
        val spinner = findViewById<android.widget.Spinner>(R.id.season_spinner)
        val labels = seasons.map { "Season $it" }
        spinner.adapter = ArrayAdapter(
            this, R.layout.item_season_spinner, labels
        ).apply { setDropDownViewResource(R.layout.item_season_dropdown) }

        spinner.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p: android.widget.AdapterView<*>?, v: android.view.View?, pos: Int, id: Long) {
                currentSeason = seasons[pos]
                refreshEpisodes()
            }
            override fun onNothingSelected(p: android.widget.AdapterView<*>?) {}
        }
    }

    private lateinit var episodeAdapter: EpisodeAdapter

    private fun setupEpisodeList() {
        val list = findViewById<RecyclerView>(R.id.episode_list)
        list.layoutManager = LinearLayoutManager(this)
        episodeAdapter = EpisodeAdapter(series.episodesForSeason(currentSeason)) { ep ->
            playEpisode(ep)
        }
        list.adapter = episodeAdapter
    }

    private fun refreshEpisodes() {
        episodeAdapter.update(series.episodesForSeason(currentSeason))
    }

    private fun playEpisode(ep: Episode) {
        val intent = Intent(this, PlaybackActivity::class.java)
        intent.putExtra(PlaybackActivity.EXTRA_EPISODE, ep)
        startActivity(intent)
    }
}
