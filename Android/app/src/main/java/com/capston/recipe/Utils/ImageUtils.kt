package com.capston.recipe.Utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.capston.recipe.R
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object ImageUtils {
    val TAG = "ImageUtils"
    fun stringToImage(imageString:String?, context: Context):Bitmap{
        return try {
            val byteArray = Base64.decode(imageString, 0)
            val inStream = ByteArrayInputStream(byteArray)
            BitmapFactory.decodeStream(inStream)
        }catch (e:Exception){
            Log.i(TAG,"imageUtil String Format error")

            val drawable = ContextCompat.getDrawable(
                context,
                R.drawable.image_uncallable
            ) // 이미지 부를 수 없으면 부를 수 없다고 설정
            val bitmapDrawable = drawable as BitmapDrawable
            bitmapDrawable.bitmap
        }
    }
    fun bitmapToString(bitmap:Bitmap):String{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, baos)
        val bytes = baos.toByteArray()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }
    fun bitmapToMultiPartBodyPart(context:Context, bitmap:Bitmap,imageName:String, imageClassification:String,i:Int): MultipartBody.Part{
        val filesDir = context.filesDir
        val imageFile = if(i<0) File(filesDir, "${imageName}.png")
        else File(filesDir, "${imageName}_$i.png")
        val os: OutputStream
        try {
            os = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Error writing bitmap ", e)
        } finally {
            val requestBody =
                RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
            return MultipartBody.Part.createFormData(
                if(i<0) "$imageClassification"
                else "${imageClassification}_${i}"
                ,
                imageFile.name,
                requestBody)    //imageClassification => compelete,recipe, story, profile,
        }
    }
    //followingName = "userId_recipe_number"
    fun bitmapListToMultipartBodyPartList(bitmapList:ArrayList<Bitmap>, followingName:String, context:Context):ArrayList<MultipartBody.Part>{
        val imageName = SimpleDateFormat("yyyy_MM_dd_hh_mm_ss").format(Date(System.currentTimeMillis()))+"_${followingName}"
        val multiBodyList = arrayListOf<MultipartBody.Part>()
        var i=0
        for(bitmap in bitmapList) {
            multiBodyList.add(bitmapToMultiPartBodyPart(context, bitmap,imageName, followingName,i++))
        }
        return multiBodyList
    }
    fun dwonSizeUriToBitmap(context:Context, uri: Uri, resize:Int):Bitmap?{
        var resizeBitmap:Bitmap

        val options = BitmapFactory.Options()
        try {
            val a = BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri), null, options)

            Log.i("dwonSizeUriToBitmap", "with : ${a!!.height}")

            var width = options.outWidth
            var height = options.outHeight
            var sampleSize = 1
            while (true) {//2번
                Log.i("dwonSizeUriToBitmap", "with : $width, $height")
                if (width / 2 < resize || height / 2 < resize)
                    break
                width /= 2
                height /= 2
                sampleSize *= 2
            }

            options.inSampleSize = sampleSize
            val bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options) //3번
            Log.i("dwonSizeUriToBitmap", "with : ${bitmap!!.height}")
            resizeBitmap=bitmap
            return resizeBitmap
        } catch (e:FileNotFoundException) {
            e.printStackTrace()
            return null
        }
    }
    fun putImageIntoView(context:Context, image:String?, view:ImageView){
        if (image != null) {
            if(image.contains("http")!!)
                Glide.with(context).load(image).into(view)
            else
                Glide.with(context).load(ServerAddress.serverAddress+image).into(view)
        }else
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_error_black_24dp)).into(view)
    }

    fun putImageIntoCircleView(context:Context, image:String?, view:ImageView){
        if (image != null) {
            if(image.contains("http")!!)
                Glide.with(context).load(image).circleCrop().into(view)
            else
                Glide.with(context).load(ServerAddress.serverAddress+image).circleCrop().into(view)
        }else
            Glide.with(context).load(ContextCompat.getDrawable(context, R.drawable.ic_person_black_24dp)).circleCrop().into(view)
    }
    fun putImageIntoCircleView(context:Context, image:Drawable, view:ImageView){
        Glide.with(context).load(image).circleCrop().into(view)
    }
}