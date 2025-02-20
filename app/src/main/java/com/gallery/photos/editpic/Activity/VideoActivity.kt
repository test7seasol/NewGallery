package com.gallery.photos.editpic.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Adapter.VideoAdapter
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Fragment.AllVideosFragment
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    lateinit var bind: ActivityVideoBinding
    private var activeFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        bind = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(bind.root)

        handleBackPress {
            if ((findViewById<RecyclerView>(R.id.recyclerViewVideos).adapter as VideoAdapter).selectedItems.isEmpty()) {
                finish()
            } else {
                (findViewById<RecyclerView>(R.id.recyclerViewVideos).adapter as VideoAdapter).disableSelectionMode()
            }
        }


        bind.apply {
//            rvVideos.adapter =
            loadFragment(AllVideosFragment())
        }
    }

    private fun loadFragment(newFragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        val fragmentTag = newFragment::class.java.simpleName

        // Check if the fragment already exists
        var fragment = fragmentManager.findFragmentByTag(fragmentTag)

        if (activeFragment != null) {
            transaction.hide(activeFragment!!) // Hide the currently active fragment
        }

        if (fragment == null) {
            // Add the new fragment only if it's not already added
            fragment = newFragment
            transaction.add(R.id.videoframecontainer, fragment, fragmentTag)
        } else {
            // If the fragment exists, just show it
            transaction.show(fragment)
        }

        transaction.commitAllowingStateLoss()
        activeFragment = fragment
    }
}