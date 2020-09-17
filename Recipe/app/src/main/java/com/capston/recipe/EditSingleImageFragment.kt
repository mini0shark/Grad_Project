package com.capston.recipe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.capston.recipe.Utils.BitmapUtils
import com.capston.recipe.Utils.CameraUtil
import com.capston.recipe.Utils.ConstCodes.MULTIPLE_REQUEST_CODE
import com.capston.recipe.interfaces.EditImageFragmentListener
import com.capston.recipe.interfaces.FilterListFragmentListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.theartofdev.edmodo.cropper.CropImage
import com.yalantis.ucrop.UCrop
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import kotlinx.android.synthetic.main.activity_edit_multiple_image.view.*
import kotlinx.android.synthetic.main.activity_edit_single_image.view.*
import kotlinx.android.synthetic.main.content_main.view.*
import java.io.File
import java.util.*

class EditSingleImageFragment: Fragment(), FilterListFragmentListener,
    EditImageFragmentListener {
    val TAG = "EditActivitySingle tag"

    init{
        System.loadLibrary("NativeImageProcessor")
    }
    var menu:Menu?=null
    lateinit var listener:DoneClickListener
    val fragment:Fragment = this
    lateinit var camera: CameraUtil

    private var originalImage: Bitmap?=null
    private var filteredImage:Bitmap?=null
    private var middleImage:Bitmap?=null
    private lateinit var finalImage:Bitmap
    private val imageList = arrayListOf<Bitmap>()
    private val imageUriList=arrayListOf<Uri>()
    var imageUriListIndex = 0

    private lateinit var filterListFragment:FilterListFragment
    private lateinit var editImageFragment:EditImageFragment

    private var image_selected_uri:Uri?=null

    private var brightnessFinal = 0
    private var saturationFinal = 1.0f
    private var contrastFinal = 1.0f

    private lateinit var fragmentInflater: View


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fragmentInflater = inflater.inflate(R.layout.activity_edit_single_image, container, false)
        camera = CameraUtil(activity!!, context!!)
        actionPick()
        viewInitiate()
        return fragmentInflater
    }
    fun actionPick(){

        Dexter.withActivity(activity!!)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    camera.takePicture(null, fragment)
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,token: PermissionToken?) {
                    token!!.continuePermissionRequest()
                }

            }).check()
    }

    private fun viewInitiate() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "이미지 선택"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        fragmentInflater.include_editSingleImage.btn_crop.visibility=View.GONE
        editImageFragment=EditImageFragment.getInstance()
        filterListFragment=FilterListFragment.getInstance(originalImage)
        fragmentInflater.include_editSingleImage.btn_filters.setOnClickListener {
            middleImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
            filterListFragment.setListener(this)
            filterListFragment.show(fragmentManager!!, filterListFragment.tag)
        }
        fragmentInflater.include_editSingleImage.btn_edit.setOnClickListener {
            middleImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
            editImageFragment.setListener(this)
            editImageFragment.show(fragmentManager!!, editImageFragment.tag)
        }
        fragmentInflater.include_editSingleImage.btn_crop.visibility = View.GONE
        fragmentInflater.include_editSingleImage.btn_resetImage.setOnClickListener{
            loadImage()
        }
    }

    private fun loadImage() {
        if(imageUriList.size == imageUriListIndex+1)
            menu?.findItem(R.id.action_next)?.title = "DONE"
        image_selected_uri = imageUriList[imageUriListIndex]
        originalImage = BitmapUtils.getBitmapFromUri(activity!!, image_selected_uri!!)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
//        finalImage = originalImage!!
        fragmentInflater.include_editSingleImage.image_preview.setImageBitmap(originalImage)
    }

    override fun onFilterSelected(filter: Filter) {
        resetControls()
        filteredImage = middleImage!!.copy(Bitmap.Config.ARGB_8888, true)
        fragmentInflater.include_editSingleImage.image_preview.setImageBitmap(filter.processFilter(filteredImage))
//        finalImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun resetControls() {
        editImageFragment.resetControls()
        brightnessFinal=0
        saturationFinal = 1.0f
        contrastFinal = 1.0f
    }

    override fun onBrightnessChanged(brightness: Int) {
        brightnessFinal = brightness
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightness))
        var tempImage = myFilter.processFilter((filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)))
        val bitmap = myFilter.processFilter(tempImage)
        fragmentInflater.include_editSingleImage.image_preview.setImageBitmap(bitmap)
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        var tempImage = myFilter.processFilter((filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)))
        val bitmap = myFilter.processFilter(tempImage)
        fragmentInflater.include_editSingleImage.image_preview.setImageBitmap(bitmap)
    }

    override fun onConstrantChanged(constrant: Float) {
        contrastFinal= constrant
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(constrant))
        var tempImage = myFilter.processFilter((filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)))
        val bitmap = myFilter.processFilter(tempImage)
        fragmentInflater.include_editSingleImage.image_preview.setImageBitmap(bitmap)
    }

    override fun onEditStarted() {
    }

    override fun onEditCompleted() {
        val bitmap = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        myFilter.addSubFilter(ContrastSubFilter(contrastFinal))
        filteredImage = myFilter.processFilter(bitmap)
//        finalImage = myFilter.processFilter(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        activity!!.menuInflater.inflate(R.menu.edit_image_menu, menu)
        this.menu = menu
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_next->{
                imageList.add(filteredImage!!)
                returnToFragment()
                return true
            }
            android.R.id.home ->{
                Toast.makeText(context, "취소했습니다.", Toast.LENGTH_SHORT).show()
                fragmentManager!!.beginTransaction().remove(this).commit()
                fragmentManager!!.popBackStack()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun returnToFragment() {
        listener.onClickDone(imageList)
        fragmentManager!!.beginTransaction().remove(this).commit()
        fragmentManager!!.popBackStack()
    }
    interface DoneClickListener{
        fun onClickDone(imgList:ArrayList<Bitmap>)
        fun onCancel()
    }

    override fun onDetach() {
        super.onDetach()
        listener.onCancel()
        Log.i(TAG, "Detach!!!")
    }
    fun setDoneClickListener(listener:DoneClickListener){
        this.listener = listener
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK ){
            if( requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                val imageUri = CropImage.getPickImageResultUri(context!!, data)
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultImageUri = result.uri
                    imageUriList.add(resultImageUri)
                    loadImage()
//                    fragmentInflater.include_editSingleImage.image_preview.setImageURI(imageUri)
                }
            }
        }else{
            Toast.makeText(context, "사진을 가져오는데 실패했습니다..", Toast.LENGTH_SHORT).show()
            fragmentManager!!.beginTransaction().remove(this).commit()
            fragmentManager!!.popBackStack()
        }
    }
}
