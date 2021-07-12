package kr.co.bepo.snsuploadapp.photo

import android.app.Activity
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import kr.co.bepo.snsuploadapp.R
import kr.co.bepo.snsuploadapp.databinding.ActivityImagePreivewListBinding
import kr.co.bepo.snsuploadapp.util.PathUtil
import java.io.File
import java.io.FileNotFoundException


class ImagePreviewListActivity : AppCompatActivity() {

    companion object {
        private const val URI_LIST_KEY = "uriList"

        fun newIntent(activity: Activity, uriList: List<Uri>) =
            Intent(activity, ImagePreviewListActivity::class.java).apply {
                putExtra(URI_LIST_KEY, ArrayList<Uri>().apply { uriList.forEach { add(it) } })
            }
    }

    private lateinit var binding: ActivityImagePreivewListBinding
    private lateinit var imageViewPagerAdapter: ImageViewPagerAdapter

    private val uriList by lazy<List<Uri>> { intent.getParcelableArrayListExtra(URI_LIST_KEY)!! }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImagePreivewListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolBar)
        setupImageList()
    }

    private fun setupImageList() = with(binding) {
        if (::imageViewPagerAdapter.isInitialized.not()) {
            imageViewPagerAdapter = ImageViewPagerAdapter(uriList)
        }
        imageViewPager.adapter = imageViewPagerAdapter
        indicator.setViewPager(imageViewPager)
        imageViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                toolBar.title = getString(
                    R.string.images_page,
                    position + 1,
                    imageViewPagerAdapter.uriList.size
                )
            }
        })

        deleteButton.setOnClickListener {
            removeImage(imageViewPagerAdapter.uriList[imageViewPager.currentItem])
        }

        confirmButton.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(URI_LIST_KEY, ArrayList<Uri>(imageViewPagerAdapter.uriList))
            })
            finish()
        }
    }

    private fun removeImage(uri: Uri) {
        try {
            val file = File(PathUtil.getPath(this, uri) ?: throw FileNotFoundException())
            file.delete()
            imageViewPagerAdapter.uriList.let {
                val imageList = it.toMutableList()
                imageList.remove(uri)
                imageViewPagerAdapter.uriList = imageList
                imageViewPagerAdapter.notifyDataSetChanged()
            }

            MediaScannerConnection.scanFile(this, arrayOf(file.path), arrayOf("image/jpeg"), null)
            binding.indicator.setViewPager(binding.imageViewPager)
            if (imageViewPagerAdapter.uriList.isEmpty()) {
                Toast.makeText(this, "삭제할 수 있는 이미지가 없습니다.", Toast.LENGTH_SHORT).show()
                finish()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "이미지가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        setResult(Activity.RESULT_OK, Intent().apply {
            putExtra(
                URI_LIST_KEY,
                putExtra(
                    URI_LIST_KEY,
                    ArrayList<Uri>().apply { imageViewPagerAdapter.uriList.forEach { add(it) } })
            )
        })
        super.onDestroy()
    }
}