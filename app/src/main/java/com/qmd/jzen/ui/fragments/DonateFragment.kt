package com.qmd.jzen.ui.fragments

import android.content.ContentResolver
import android.content.ContentValues
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.orhanobut.logger.Logger
import com.qmd.jzen.R
import com.qmd.jzen.databinding.FragmentDonateBinding
import com.qmd.jzen.utils.Toaster
import java.io.IOException

/**
 * 捐赠
 */
class DonateFragment : Fragment() {

    companion object {
        const val keyDonateWay = "donateWay"
    }

    // 捐赠方式
    enum class DonateWay(val index: Int, val url: String) {
        Alipay(0, "http://qmd.oss.cn-north-1.jcloudcs.com/alipay.jpeg"),
        Wechat(1, "https://qmd.s3.cn-north-1.jdcloud-oss.com/wechat.JPG")
    }

    private var donateWay: DonateWay = DonateWay.Wechat
    private lateinit var binding: FragmentDonateBinding

    // 缓存下来的bitmap，用于保存
    private var imageResource: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val donateWayIndex = arguments?.getInt(keyDonateWay)
        if (donateWayIndex == null) {
            error()
            return
        }

        donateWay = when (donateWayIndex) {
            DonateWay.Alipay.index -> DonateWay.Alipay
            DonateWay.Wechat.index -> DonateWay.Wechat
            else -> {
                error(donateWay)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDonateBinding.inflate(inflater, container, false)
        // 加载图片
        val target = object : SimpleTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                binding.imageDonate.setImageBitmap(resource)
                imageResource = resource
            }
        }
        Glide.with(requireContext()).asBitmap().load(donateWay.url).into(target)
        // 保存按钮
        binding.buttonSave.setOnClickListener {
            if (saveImage()) {
                MaterialDialog(requireContext()).show {
                    title(text = "保存成功")
                    message(R.string.donate)
                    positiveButton(text = "好的")
                }
            } else {
                Toaster.out("保存失败")
            }
        }
        return binding.root
    }

    /**
     * 发生错误，退出页面
     */
    private fun error(msg: String = "") {
        Toaster.out("参数异常")
        findNavController().popBackStack()
        if (msg.isNotEmpty()) {
            Logger.e(msg)
        }
    }

    private fun saveImage(): Boolean {
        val bitmap: Bitmap = imageResource ?: return false
        val imageName = "${donateWay.name}_donate${System.currentTimeMillis()}.png"
        ContentValues().also {
            it.put(MediaStore.Images.Media.DISPLAY_NAME, imageName)
            it.put(MediaStore.Images.Media.TITLE, imageName)
            it.put(MediaStore.Images.Media.MIME_TYPE, "image/png")

            requireActivity().contentResolver.apply {
                insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, it)?.let { uri ->
                    openOutputStream(uri).use { stream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                        return true
                    }
                }
            }
        }
        return false
    }
}