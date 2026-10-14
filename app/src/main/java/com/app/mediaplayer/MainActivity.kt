package com.app.mediaplayer

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.mediaplayer.fragments.AudioFragment
import com.app.mediaplayer.fragments.MeFragment
import com.app.mediaplayer.fragments.SearchFragment
import com.app.mediaplayer.fragments.VideoFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    private val videoFragment by lazy { VideoFragment.newInstance() }
    private val audioFragment by lazy { AudioFragment.newInstance() }
    private val searchFragment by lazy { SearchFragment.newInstance() }
    private val meFragment by lazy { MeFragment.newInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottom_nav)

        setupBottomNav()
        setupBackPress()

        if (savedInstanceState == null) {
            switchFragment(videoFragment, TAG_VIDEO)
        }
    }

    private fun setupBottomNav() {
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_video -> {
                    switchFragment(videoFragment, TAG_VIDEO)
                    true
                }
                R.id.nav_audio -> {
                    switchFragment(audioFragment, TAG_AUDIO)
                    true
                }
                R.id.nav_search -> {
                    switchFragment(searchFragment, TAG_SEARCH)
                    true
                }
                R.id.nav_me -> {
                    switchFragment(meFragment, TAG_ME)
                    true
                }
                else -> false
            }
        }
    }

    private fun switchFragment(fragment: Fragment, tag: String) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (currentFragment != null && currentFragment::class == fragment::class) {
            return
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment, tag)
            .commit()
    }

    private fun setupBackPress() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (bottomNav.selectedItemId != R.id.nav_video) {
                    bottomNav.selectedItemId = R.id.nav_video
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })
    }

    companion object {
        private const val TAG_VIDEO = "TAG_VIDEO"
        private const val TAG_AUDIO = "TAG_AUDIO"
        private const val TAG_SEARCH = "TAG_SEARCH"
        private const val TAG_ME = "TAG_ME"
    }
}
