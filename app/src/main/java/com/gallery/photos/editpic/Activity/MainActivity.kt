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
import com.gallery.photos.editpic.Dialogs.ExitDialog
import com.gallery.photos.editpic.Dialogs.SMBottomSheetDialog
import com.gallery.photos.editpic.Extensions.PIN_LOCK
import com.gallery.photos.editpic.Extensions.PREF_LANGUAGE_CODE
import com.gallery.photos.editpic.Extensions.gone
import com.gallery.photos.editpic.Extensions.handleBackPress
import com.gallery.photos.editpic.Extensions.invisible
import com.gallery.photos.editpic.Extensions.log
import com.gallery.photos.editpic.Extensions.notifyGalleryRoot
import com.gallery.photos.editpic.Extensions.onClick
import com.gallery.photos.editpic.Extensions.setLanguageCode
import com.gallery.photos.editpic.Extensions.startActivityWithBundle
import com.gallery.photos.editpic.Extensions.visible
import com.gallery.photos.editpic.Fragment.AlbumFragment
import com.gallery.photos.editpic.Fragment.RecentsPictureFragment
import com.gallery.photos.editpic.R
import com.gallery.photos.editpic.databinding.ActivityMainBinding
import com.gallery.photos.editpic.myadsworld.MyAddPrefs
import com.gallery.photos.editpic.myadsworld.MyAllAdCommonClass
import com.google.firebase.analytics.FirebaseAnalytics

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
        setLanguageCode(this, MyApplicationClass.getString(PREF_LANGUAGE_CODE)!!)
        setContentView(binding.root)
        window.statusBarColor = resources.getColor(android.R.color.white, theme)

        //Calldorado..

        val firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "MainActivity_Gallery")
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)


        hideBottomNavigationBar(R.color.white)

        handleBackPress {
            val fragmentManager = supportFragmentManager
            val currentFragment = fragmentManager.findFragmentById(R.id.framecontainer)

            try {

                if (currentFragment is AlbumFragment) {
                    if (findViewById<TextView>(R.id.tvAlbumeTitle)?.text?.toString() != getString(
                            R.string.albums
                        )
                    ) {
                        findViewById<RelativeLayout>(R.id.selectedcontaineralbumsid).gone()
                        (findViewById<RecyclerView>(R.id.recyclerViewAlbums).adapter as FolderAdapter).unselectAllItems()
                        findViewById<TextView>(R.id.tvAlbumeTitle).text = getString(R.string.albums)
                        binding.mainTopTabsContainer.visible()
                    } else {
                        when (currentFragment) {
                            is AlbumFragment -> {
                                ("is Album").log()
                                loadFragment(
                                    RecentsPictureFragment()
                                )

                                binding.apply {
                                    tvpicture.setTextColor(Color.BLACK)
                                    picview.visible()
                                    mainTopTabsContainer.visible()
                                    tvalbum.setTextColor(getColor(R.color.hint_black))
                                    albumview.invisible()
                                }
                            }

                            is RecentsPictureFragment -> {
                                showExitDialog()
                            }

                            else -> {
                                if (fragmentManager.backStackEntryCount > 0) {
                                    fragmentManager.popBackStack()
                                } else {
                                    finish() // Exit app if no fragments left
                                }
                            }
                        }

                    }
                } else if (currentFragment is RecentsPictureFragment) {
                    if (findViewById<TextView>(R.id.tvSelection)?.text?.toString() != getString(R.string.pictures)) {
                        findViewById<RelativeLayout>(R.id.selectedcontainerid).gone()
                        (findViewById<RecyclerView>(R.id.recyclerViewRecentPictures).adapter as RecentPictureAdapter).unselectAllItems()
                    findViewById<TextView>(R.id.tvSelection).text = getString(R.string.pictures)
                        binding.mainTopTabsContainer.visible()
                    } else {
                        when (currentFragment) {
                            is AlbumFragment -> {
                                ("is Album").log()
                                loadFragment(
                                    RecentsPictureFragment()
                                )

                                binding.apply {
                                    tvpicture.setTextColor(Color.BLACK)
                                    picview.visible()
                                    mainTopTabsContainer.visible()
                                    tvalbum.setTextColor(getColor(R.color.hint_black))
                                    albumview.invisible()
                                }
                            }

                            is RecentsPictureFragment -> {
                                showExitDialog()
                            }

                            else -> {
                                if (fragmentManager.backStackEntryCount > 0) {
                                    fragmentManager.popBackStack()
                                } else {
                                    finish() // Exit app if no fragments left
                                }
                            }
                        }

                    }
                } else {
                    when (currentFragment) {
                        is AlbumFragment -> {
                            ("is Album").log()
                            loadFragment(
                                RecentsPictureFragment()
                            )

                            binding.apply {
                                tvpicture.setTextColor(Color.BLACK)
                                picview.visible()
                                mainTopTabsContainer.visible()
                                tvalbum.setTextColor(getColor(R.color.hint_black))
                                albumview.invisible()
                            }
                        }

                        is RecentsPictureFragment -> {
                            showExitDialog()
                        }

                        else -> {
                            if (fragmentManager.backStackEntryCount > 0) {
                                fragmentManager.popBackStack()
                            } else {
                                finish() // Exit app if no fragments left
                            }
                        }
                    }
            }
            } catch (e: Exception) {
                e.message!!.log()
                val fragmentManager = supportFragmentManager
                val currentFragment = fragmentManager.findFragmentById(R.id.framecontainer)

                when (currentFragment) {
                    is AlbumFragment -> {
                        loadFragment(
                            RecentsPictureFragment()
                        )
                        binding.apply {
                            tvpicture.setTextColor(Color.BLACK)
                            picview.visible()
                            mainTopTabsContainer.visible()
                            tvalbum.setTextColor(getColor(R.color.hint_black))
                            albumview.invisible()
                        }
                    }

                    is RecentsPictureFragment -> {
                        showExitDialog()
                    }

                    else -> {
                        if (fragmentManager.backStackEntryCount > 0) {
                            fragmentManager.popBackStack()
                        } else {
                            finish() // Exit app if no fragments left
                        }
                    }
                }
//                showExitDialog()
            }
        }
        // Set light status bar icons (dark icons)
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        // Handle system insets

        MyAllAdCommonClass.showAdmobBanner(
            this@MainActivity,
            binding.bannerContainer,
            binding.shimmerContainerBanner,
            false,
            MyAddPrefs(this@MainActivity).admBannerId
        )

        openPhotos()
        setLinearClickListeners()
    }

    private fun showExitDialog() {
        ExitDialog(this) {}
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
                                overridePendingTransition(0, 0);  // Disables the transition effect

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

    private fun loadFragment(newFragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()

        transaction.replace(R.id.framecontainer, newFragment, newFragment::class.java.simpleName)

        if (addToBackStack) {
            transaction.addToBackStack(newFragment::class.java.simpleName)
        }

        transaction.commitAllowingStateLoss()
        activeFragment = newFragment
    }

}