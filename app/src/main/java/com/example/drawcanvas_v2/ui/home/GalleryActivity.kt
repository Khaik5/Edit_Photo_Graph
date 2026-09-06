package com.example.drawcanvas_v2.ui.home
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.drawcanvas_v2.databinding.ActivityHomeBinding
import com.example.drawcanvas_v2.ui.camera.CameraActivity
import com.example.drawcanvas_v2.utils.ImagePermissionUtils
import com.example.drawcanvas_v2.utils.InsetsUtils
import com.example.drawcanvas_v2.utils.collectFlow
import androidx.core.graphics.toColorInt
class GalleryActivity : AppCompatActivity() {
    private val binding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val viewModel: GalleryHomeViewModel by viewModels()
    private val tabUi by lazy { GalleryTabUi(binding) }
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            viewModel.onAction(
                GalleryHomeAction.PermissionResult(granted)
            )
        }
    private val cameraPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(
                    this,
                    "Please allow camera access",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(
            savedInstanceState
        )

        setContentView(
            binding.root
        )
        setupView()
        setupListener()
        observeViewModel()
        checkPermission()
    }

    private fun setupView() {

        InsetsUtils.apply(
            this,
            binding.root
        )
    }
    private fun setupListener() {
        binding.tabRecents.setOnClickListener { selectTab(0) }
        binding.tabFavourites.setOnClickListener { selectTab(1) }
        binding.tabSelfies.setOnClickListener { selectTab(2) }
        binding.btnCamera.setOnClickListener {
            checkCameraPermission()
        }
    }
    private fun checkCameraPermission() {
        val granted =
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            openCamera()
        } else {
            cameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }
    private fun openCamera() {
        startActivity(Intent(this, CameraActivity::class.java))
    }
    private fun checkPermission() {
        val granted =
            ImagePermissionUtils
                .isGranted(
                    this
                )
        viewModel.onAction(GalleryHomeAction.Start(granted))
    }
    private fun requestPermission() {
        permissionLauncher.launch(
            ImagePermissionUtils
                .getPermission()
        )
    }
    private fun setupViewPager() {
        if (
            binding.vpGallery.adapter != null
        ) {
            return
        }
        binding.vpGallery.adapter = GalleryViewPageAdapter(this)
        binding.vpGallery.registerOnPageChangeCallback(
                object :
                    ViewPager2
                    .OnPageChangeCallback() {
                    override fun onPageSelected(
                        position: Int
                    ) {
                        viewModel.onAction(GalleryHomeAction.TabChanged(position)
                        )
                    }
                }
            )
    }
    private fun selectTab(
        position: Int
    ) {
        if (
            binding.vpGallery.adapter == null
        ) {
            return
        }
        binding.vpGallery.currentItem = position
    }
    private fun observeViewModel() {
        collectFlow(
            viewModel.state
        ) { state ->
            showState(state)
        }
        collectFlow(
            viewModel.effect
        ) { effect ->
            handleEffect(effect)
        }
    }

    private fun showState(
        state: GalleryHomeState
    ) {
        tabUi.show(state.currentTab)
    }
    private fun showTab(
        position: Int
    ) {
        val selected = "#18181B"
        val normal = "#8A8A92"
        binding.tabRecents.setTextColor(
                if (position == 0) {
                    selected
                } else {
                    normal
                }.toColorInt()
        )

        binding.tabFavourites.setTextColor(
            if (position == 1) {
                selected
            } else {
                normal
            }.toColorInt()
            )

        binding.tabSelfies
            .setTextColor(
                if (position == 2) {
                    selected
                } else {
                    normal
                }.toColorInt()
            )

        binding.lineRecents.alpha =
            if (position == 0) {
                1f
            } else {
                0f
            }

        binding.lineFavourites.alpha =
            if (position == 1) {
                1f
            } else {
                0f
            }

        binding.lineSelfies.alpha =
            if (position == 2) {
                1f
            } else {
                0f
            }
    }

    private fun handleEffect(
        effect: GalleryHomeEffect
    ) {

        when (effect) {

            GalleryHomeEffect.RequestPermission -> {

                requestPermission()
            }

            GalleryHomeEffect.ShowGallery -> {
                setupViewPager()
            }
            GalleryHomeEffect.PermissionDenied -> {

                Toast.makeText(
                    this,
                    "Please allow photo access",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
