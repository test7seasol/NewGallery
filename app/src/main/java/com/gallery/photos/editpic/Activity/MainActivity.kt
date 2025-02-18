package com.gallery.photos.editpic.Activity

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.gallery.photos.editpic.Adapter.FolderAdapter
import com.gallery.photos.editpic.Adapter.RecentPictureAdapter
import com.gallery.photos.editpic.Dialogs.SMBottomSheetDialog
import com.gallery.photos.editpic.Extensions.PIN_LOCK
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.invisible
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Fragment.AlbumFragment
import com.gallery.photos.editpic.Fragment.RecentsPictureFragment
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var activeFragment: Fragment? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ("onAct").log()
        if (resultCode == RESULT_OK && requestCode == 120) {
            ("onAct innner").log()
            findViewById<RecyclerView>(R.id.recyclerViewRecentPictures).scrollToPosition(0)
            notifyGalleryRoot(
                this,
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).path
            )
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(android.R.color.white, theme)

        handleBackPress {
            if (findViewById<TextView>(R.id.tvSelection).text.toString() != "Pictures") {
                findViewById<RelativeLayout>(R.id.selectedcontainerid).gone()
                (findViewById<RecyclerView>(R.id.recyclerViewRecentPictures).adapter as RecentPictureAdapter).unselectAllItems()
                findViewById<TextView>(R.id.tvSelection).text = "Pictures"
                binding.mainTopTabsContainer.visible()
            } else if (findViewById<TextView>(R.id.tvAlbumeTitle).text.toString() != "Albums") {
                findViewById<RelativeLayout>(R.id.selectedcontaineralbumsid).gone()
                (findViewById<RecyclerView>(R.id.recyclerViewAlbums).adapter as FolderAdapter).unselectAllItems()
                findViewById<TextView>(R.id.tvAlbumeTitle).text = "Albums"
                binding.mainTopTabsContainer.visible()
            } else {
                finishAffinity()
            }
        }
        // Set light status bar icons (dark icons)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // Handle system insets


        openPhotos()
        setLinearClickListeners()
    }


    fun setLinearClickListeners() {
        binding.apply {

            tvpicture.setTextColor(Color.BLACK)
            picview.visible()

            tvalbum.setTextColor(getColor(R.color.hint_black))
            albumview.invisible()

            picturebtnid.onClick {
                tvpicture.setTextColor(Color.BLACK)
                picview.visible()

                tvalbum.setTextColor(getColor(R.color.hint_black))
                albumview.invisible()
                openPhotos()
            }

            ivalbumid.onClick {
                tvalbum.setTextColor(getColor(R.color.black))
                albumview.visible()

                tvpicture.setTextColor(getColor(R.color.hint_black))
                picview.invisible()
                openAlbums()
            }

            menuitembtnid.onClick {
                SMBottomSheetDialog(this@MainActivity) {
                    when (it) {
                        "hideshowid" -> {
                            if (MyApplicationClass.getString(PIN_LOCK)?.isNotEmpty() == true) {
                                luancherForPin.launch(
                                    Intent(this@MainActivity, PatternAct::class.java)
                                        .putExtra("isFromHide", true)
                                )
                            } else {
                                startActivityWithBundle<HideActivity>()
                            }
                        }
                        "Recent" -> {
                            tvpicture.setTextColor(Color.BLACK)
                            picview.visible()
                            tvalbum.setTextColor(getColor(R.color.hint_black))
                            albumview.gone()
                            openPhotos()
                        }

                    }
                }
            }
        }
    }

    var luancherForPin =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val resultString = data?.getStringExtra("isFromHide") // Get returned data
                Log.d("TAG", "Result HideActivity: $resultString")
                if (resultString == "true") {
                    startActivityWithBundle<HideActivity>()
                }
            }
        }

    private fun resetSelection(
        buttons: List<Triple<LinearLayout, TextView, ImageView>>, defaultIcons: Map<ImageView, Int>
    ) {
        buttons.forEach { (_, textView, imageView) ->
            textView.setTextColor(ContextCompat.getColor(this, R.color.appgrey))
            imageView.setImageResource(defaultIcons[imageView] ?: 0)
        }
    }

    private fun openPhotos() {
        loadFragment(RecentsPictureFragment())
    }

    private fun openAlbums() {
//        binding.selectionToolbar.gone()
        loadFragment(AlbumFragment())
    }

    private fun openVideos() {
//        loadFragment(AllVideosFragment())

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
            transaction.add(R.id.framecontainer, fragment, fragmentTag)
        } else {
            // If the fragment exists, just show it
            transaction.show(fragment)
        }

        transaction.commitAllowingStateLoss()
        activeFragment = fragment
    }
}