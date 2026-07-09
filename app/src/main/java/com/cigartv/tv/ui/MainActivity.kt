package com.cigartv.tv.ui

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.cigartv.tv.R

/**
 * Entry activity. Hosts the main browse experience (VOD grid of series). The
 * chooser (Live vs Browse) and live playback mirror the Roku flow and can be
 * layered on next; this scaffold opens directly into the Originals browse grid.
 */
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_browse_fragment, MainBrowseFragment())
                .commitNow()
        }
    }
}
