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
import com.capston.recipe.Utils.ConstCodes.MULTIPLE_REQUEST_CODE
import com.capston.recipe.interfaces.EditImageFragmentListener
import com.capston.recipe.interfaces.FilterListFragmentListener
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import kotlinx.android.synthetic.main.activity_edit_multiple_image.view.*
import kotlinx.android.synthetic.main.content_main.view.*
import java.io.File
import java.lang.Exception
import java.util.*

class EditMultipleImageFragment: Fragment(), FilterListFragmentListener,
    EditImageFragmentListener {
    val TAG = "EditActivity tag"

    init{
        System.loadLibrary("NativeImageProcessor")
    }
    var menu:Menu?=null
    lateinit var listener:DoneClickListener
    val fragment:Fragment = this

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
        fragmentInflater = inflater.inflate(R.layout.activity_edit_multiple_image, container, false)
        actionPick()
        viewInitiate()
        return fragmentInflater
    }
    fun actionPick(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type="image/*"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), MULTIPLE_REQUEST_CODE)
    }

    private fun viewInitiate() {
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportActionBar?.title = "이미지 등록"
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        editImageFragment=EditImageFragment.getInstance()
        filterListFragment=FilterListFragment.getInstance(originalImage)
        fragmentInflater.include_editMultipleImage.btn_filters.setOnClickListener {
            middleImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
            filterListFragment.setListener(this)
            filterListFragment.show(fragmentManager!!, filterListFragment.tag)
        }
        fragmentInflater.include_editMultipleImage.btn_edit.setOnClickListener {
            middleImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
            editImageFragment.setListener(this)
            editImageFragment.show(fragmentManager!!, editImageFragment.tag)
        }
        fragmentInflater.include_editMultipleImage.btn_crop.setOnClickListener {

            Dexter.withActivity(activity!!)
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        startCrop(BitmapUtils.getImageUriFromBitmap(context!!  ,filteredImage!!))
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token!!.continuePermissionRequest()
                    }

                }).check()
        }
        fragmentInflater.include_editMultipleImage.btn_resetImage.setOnClickListener{
            loadImage()
        }
    }

    private fun startCrop(uri: Uri?) {
        Dexter.withActivity(activity!!)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    var destinationFileName = StringBuilder(UUID.randomUUID().toString()).append(".png").toString()
                    try{
                        var uCrop = UCrop.of(uri!!, Uri.fromFile(File(activity!!.cacheDir, destinationFileName)))
                        uCrop.start(context!!, fragment)
                    }catch (e:NullPointerException){
                        Toast.makeText(context!!, "이미지를 등록하세요", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    token!!.continuePermissionRequest()
                }

            }).check()
        //==> onActivityResult로
    }

    private fun loadImage() {
        if(imageUriList.size == imageUriListIndex+1)
            menu?.findItem(R.id.action_next)?.title = "DONE"
        image_selected_uri = imageUriList[imageUriListIndex]
        originalImage = BitmapUtils.getBitmapFromUri(activity!!, image_selected_uri!!)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
//        finalImage = originalImage!!
        fragmentInflater.include_editMultipleImage.image_preview.setImageBitmap(originalImage)
    }

    override fun onFilterSelected(filter: Filter) {
        resetControls()
        filteredImage = middleImage!!.copy(Bitmap.Config.ARGB_8888, true)
        fragmentInflater.include_editMultipleImage.image_preview.setImageBitmap(filter.processFilter(filteredImage))
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
        fragmentInflater.include_editMultipleImage.image_preview.setImageBitmap(bitmap)
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        var tempImage = myFilter.processFilter((filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)))
        val bitmap = myFilter.processFilter(tempImage)
        fragmentInflater.include_editMultipleImage.image_preview.setImageBitmap(bitmap)
    }

    override fun onConstrantChanged(constrant: Float) {
        contrastFinal= constrant
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(constrant))
        var tempImage = myFilter.processFilter((filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)))
        val bitmap = myFilter.processFilter(tempImage)
        fragmentInflater.include_editMultipleImage.image_preview.setImageBitmap(bitmap)
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
                if(imageUriList.size-1==imageUriListIndex){
                    returnToFragment()
                    return true
                }else{
                    editNextImage()
                }
            }
            android.R.id.home ->{
                Toast.makeText(context, "취소했습니다.", Toast.LENGTH_SHORT).show()
                fragmentManager!!.beginTransaction().remove(this).commit()
                fragmentManager!!.popBackStack()
            }
//            android.R.id.navigationBarBackground ->{
//                Toast.makeText(this, "back press", Toast.LENGTH_SHORT).show()
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun editNextImage() {
        imageUriListIndex++
        loadImage()
    }

    private fun returnToFragment() {
        listener.onClickDone(imageList)
        fragmentManager!!.beginTransaction().remove(this).commit()
        fragmentManager!!.popBackStack()
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        (context as MainActivity).setOnBackPressedListener(this)
//    }
//
//    override fun onBack() {
//        Toast.makeText(context, "취소했습니다.", Toast.LENGTH_SHORT).show()
//        fragmentManager!!.beginTransaction().remove(this).commit()
//        fragmentManager!!.popBackStack()
//        (activity!! as MainActivity).setOnBackPressedListener(null)
//        listener.onCancel()
//    }
    interface DoneClickListener{
        fun onClickDone(imgList:ArrayList<Bitmap>)
        fun onCancel()
    }

    override fun onDetach() {
        super.onDetach()
        try {
            listener.onCancel()
        }catch (e:Exception){

        }
        Log.i(TAG, "Detach!!!")
    }
    fun setDoneClickListener(listener:DoneClickListener){
        this.listener = listener
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "resCode = $resultCode, requestcode : $requestCode")
        Log.i(TAG, "ucrop = ${UCrop.REQUEST_CROP}")
        if(resultCode == Activity.RESULT_OK ){
            if(requestCode == UCrop.REQUEST_CROP){
                handleCropResult(data)
            }else if( requestCode == MULTIPLE_REQUEST_CODE){
                val uri = data!!.data
                val clipData = data.clipData
                if(clipData != null){
                    if(clipData.itemCount<=5) {
                        for (i in 0 until clipData.itemCount)
                            if (i < clipData.itemCount)
                                imageUriList.add(clipData.getItemAt(i).uri)
                        loadImage()
                    }else {
                        Toast.makeText(context, "사진은 5장까지 등록할 수 있습니다.", Toast.LENGTH_SHORT).show()
                        actionPick()
                    }

                }else if(uri != null){
                    imageUriList.add(uri)
                    loadImage()
                }
            }
        }else if(resultCode == UCrop.RESULT_ERROR){
            handleCropError(data)
        }else{
            Toast.makeText(context, "사진을 가져오는데 실패했습니다..", Toast.LENGTH_SHORT).show()
            fragmentManager!!.beginTransaction().remove(this).commit()
            fragmentManager!!.popBackStack()
//            if(requestCode!=UCrop.REQUEST_CROP) {
//            }
        }
    }
    private fun handleCropError(data: Intent?) {
        var cropError = UCrop.getError(data!!)
        if(cropError != null){
            Toast.makeText(context, "${cropError.message}", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "Un expected Error", Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleCropResult(data: Intent?) {
        var resultUri = UCrop.getOutput(data!!)
        if(resultUri != null){
            Toast.makeText(context!!, "crop 성공!!", Toast.LENGTH_SHORT).show()
            filteredImage = BitmapUtils.getBitmapFromUri(activity!!, resultUri)
            fragmentInflater.include_editMultipleImage.image_preview.setImageBitmap(filteredImage!!)
        }else{
            Toast.makeText(context!!, "Cannot retrieve crop image", Toast.LENGTH_SHORT).show()
        }
    }
}
