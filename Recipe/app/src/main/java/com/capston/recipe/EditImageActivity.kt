package com.capston.recipe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import com.capston.recipe.Utils.BitmapUtils
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.SharedPreferenceTool
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
import kotlinx.android.synthetic.main.activity_edit_image.*
import kotlinx.android.synthetic.main.content_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.lang.NullPointerException
import java.util.*

class EditImageActivity: AppCompatActivity(), FilterListFragmentListener,
    EditImageFragmentListener {
    val TAG = "EditActivity tag"

    init{
        System.loadLibrary("NativeImageProcessor")
    }

    private var originalImage: Bitmap?=null
    private var filteredImage:Bitmap?=null
    private lateinit var finalImage:Bitmap
    private var isEdited=false

    private lateinit var filterListFragment:FilterListFragment
    private lateinit var editImageFragment:EditImageFragment

    private var image_selected_uri:Uri?=null

    private var brightnessFinal = 0
    private var saturationFinal = 1.0f
    private var constrantFinal = 1.0f

    var order = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_image)

        order = intent.getIntExtra("order",0)
        image_selected_uri = intent.getParcelableExtra("editImage")
        setSupportActionBar(toolBar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title="이미지 수정"
        loadImage()

        editImageFragment=EditImageFragment.getInstance()
        filterListFragment=FilterListFragment.getInstance(originalImage)


        btn_filters.setOnClickListener {
            if(filterListFragment!= null) {
                filterListFragment.setListener(this@EditImageActivity)
                filterListFragment.show(supportFragmentManager, filterListFragment.tag)
            }
        }
        btn_edit.setOnClickListener {
            if(editImageFragment != null) {
                editImageFragment.setListener(this@EditImageActivity)
                editImageFragment.show(supportFragmentManager, editImageFragment.tag)
            }
        }
        btn_crop.setOnClickListener {
            Dexter.withActivity(this)
                .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        startCrop(BitmapUtils.getImageUriFromBitmap(applicationContext  ,filteredImage!!))
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: MutableList<PermissionRequest>?,
                        token: PermissionToken?
                    ) {
                        token!!.continuePermissionRequest()
                    }

                }).check()
        }
        btn_resetImage.setOnClickListener{
            loadImage()
        }


    }

    private fun startCrop(uri: Uri?) {
        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    var destinationFileName = StringBuilder(UUID.randomUUID().toString()).append(".png").toString()
                    try{
                        var uCrop = UCrop.of(uri!!, Uri.fromFile(File(cacheDir, destinationFileName)))
                        uCrop.start(this@EditImageActivity)
                    }catch (e:NullPointerException){
                        Toast.makeText(applicationContext, "이미지를 등록하세요", Toast.LENGTH_SHORT).show()
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
        isEdited=false
        image_preview.adjustViewBounds= true
        originalImage = BitmapUtils.getBitmapFromUri(this, image_selected_uri!!)
        filteredImage = originalImage!!.copy(Bitmap.Config.ARGB_8888, true)
//        finalImage = originalImage!!
        image_preview.setImageBitmap(originalImage)
    }

    override fun onFilterSelected(filter: Filter) {
        resetControls()
        filteredImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.setImageBitmap(filter.processFilter(filteredImage))
        isEdited=true
//        finalImage = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun resetControls() {
        if(editImageFragment != null)
            editImageFragment.resetControls()
        brightnessFinal=0
        saturationFinal = 1.0f
        constrantFinal = 1.0f
    }

    override fun onBrightnessChanged(brightness: Int) {
        brightnessFinal = brightness
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightness))
        image_preview.setImageBitmap(myFilter.processFilter((filteredImage!!.copy(Bitmap.Config.ARGB_8888, true))))
        isEdited=true
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationFinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        image_preview.setImageBitmap(myFilter.processFilter((filteredImage!!.copy(Bitmap.Config.ARGB_8888, true))))
        isEdited=true
    }

    override fun onConstrantChanged(constrant: Float) {
        constrantFinal= constrant
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(constrant))
        image_preview.setImageBitmap(myFilter.processFilter((filteredImage!!.copy(Bitmap.Config.ARGB_8888, true))))
        isEdited=true
    }

    override fun onEditStarted() {
    }

    override fun onEditCompleted() {
        val bitmap = filteredImage!!.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightnessFinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationFinal))
        myFilter.addSubFilter(ContrastSubFilter(constrantFinal))
        isEdited=true
        filteredImage = myFilter.processFilter(bitmap)
//        finalImage = myFilter.processFilter(bitmap)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_image_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_next->{
                if(!isEdited){
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                    return true
                }
                returnToFragment()
                return true
            }
            android.R.id.home ->{
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
//            android.R.id.navigationBarBackground ->{
//                Toast.makeText(this, "back press", Toast.LENGTH_SHORT).show()
//            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun returnToFragment() {
        val intent = Intent()
        var bitmap = image_preview.drawable.toBitmap()
        val uri = BitmapUtils.getImageUriFromBitmap(this, bitmap)
//        bitmap = ImageUtils.dwonSizeUriToBitmap(this, uri, 500)!!
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val bytes = stream.toByteArray()
        val sft = SharedPreferenceTool(this)
        sft.saveObjectSharedPreference("editedImage", bytes)
//        intent.putExtra("editedImage", bytes)
        intent.putExtra("order", order)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK ){
            if(requestCode == UCrop.REQUEST_CROP){
                handleCropResult(data)
            }
        }else if(resultCode == UCrop.RESULT_ERROR){
            handleCropError(data)
        }
    }

    private fun handleCropError(data: Intent?) {
        var cropError = UCrop.getError(data!!)
        if(cropError != null){
            Toast.makeText(this@EditImageActivity, "${cropError.message}", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this@EditImageActivity, "Un expected Error", Toast.LENGTH_SHORT).show()
        }
    }
    private fun handleCropResult(data: Intent?) {
        var resultUri = UCrop.getOutput(data!!)
        if(resultUri != null){
            filteredImage = BitmapUtils.getBitmapFromUri(this, resultUri)
            image_preview.setImageBitmap(filteredImage!!)
            isEdited=true
        }else{
            Toast.makeText(this@EditImageActivity, "Cannot retrieve crop image", Toast.LENGTH_SHORT).show()
        }
    }
}
