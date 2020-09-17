package com.capston.recipe.Utils

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.*
import java.lang.Exception

object BitmapUtils{
    fun getBitmapFromAssets(context: Context, fileName:String, width:Int, height:Int): Bitmap?{
        val assetManager=context.assets
        val inputStream:InputStream
        try{
            val options= BitmapFactory.Options()
            options.inJustDecodeBounds=true
            inputStream = assetManager.open(fileName)
            options.inSampleSize = calculateInSampleSize(options,width, height)
            options.inJustDecodeBounds=false
            return BitmapFactory.decodeStream(inputStream,null, options)
        }catch(e:IOException){
            Log.e("DEBUG", e.message)
        }
        return null
    }

    fun getImageUriFromBitmap(context: Context, bitmap:Bitmap):Uri{
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(context.contentResolver, bitmap, "Title", null)
        return Uri.parse(path.toString())
    }


    fun getBitmapFromUri(activity: Activity, uri:Uri): Bitmap?{
        var bitmap: Bitmap? =null
        try {
            bitmap = MediaStore.Images.Media.getBitmap(activity.contentResolver, uri)
        }catch (e:FileNotFoundException){
            e.printStackTrace()
        }catch (e:IOException){
            e.printStackTrace()
        }
        return bitmap
    }

//    fun getBitmapFromGallery(context: Context, path:Uri, width:Int, height:Int):Bitmap{
//        // 26
//        val filePathColumns= arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = context.contentResolver.query(path, filePathColumns, null, null)
//        cursor!!.moveToFirst()
//        val columnIndex=cursor.getColumnIndex(filePathColumns[0])
//        val picturePath=cursor.getString(columnIndex)
//        cursor.close()
//        val options = BitmapFactory.Options()
//        options.inJustDecodeBounds=true
//        BitmapFactory.decodeFile(picturePath, options)
//        options.inSampleSize= calculateInSampleSize(options, width, height)
//        options.inJustDecodeBounds=false
//        return BitmapFactory.decodeFile(picturePath ,options)
//    }
    fun insertImage(contentResolver:ContentResolver, source:Bitmap?, title:String, description:String):String?{
        //depressed
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME,title)
        values.put(MediaStore.Images.Media.DESCRIPTION,description)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        var url:Uri?=null
        var stringUri:String?=null
        try{
            url=contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if(source != null){
                val imageOut=contentResolver.openOutputStream(url!!)
                try{
                    source.compress(Bitmap.CompressFormat.PNG,50,imageOut)
                }finally{
                    imageOut!!.close()
                }
                val id = ContentUris.parseId(url)
                val miniThumb=MediaStore.Images.Thumbnails.getThumbnail(contentResolver,
                    id,
                    MediaStore.Images.Thumbnails.MINI_KIND,
                    null)
                storeThumbnail(contentResolver,miniThumb, id,50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND)
            }else{
                contentResolver.delete(url!!, null, null)
                url=null
            }
        }catch (e:Exception){
            if(url!=null){
                contentResolver.delete(url,null,null)
                url=null
            }
        }
        if(url!=null){
            stringUri=url.toString()
        }
        return stringUri
    }

    private fun storeThumbnail(contentResolver: ContentResolver, source: Bitmap?, id:Long, width: Float, height: Float, microKind: Int):Bitmap? {
        val matrix = Matrix()
        val scaleX = width/source!!.width
        val scaleY =height/source!!.height
        matrix.setScale(scaleX, scaleY)
        val thumb = Bitmap.createBitmap(source,0,0, source.width, source.height, matrix, true)

        val values=ContentValues(4)
        values.put(MediaStore.Images.Thumbnails.KIND, microKind)
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,id.toInt())
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.height)
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.width)

        val url = contentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,values)

        try{
            val thumbOut = contentResolver.openOutputStream(url!!)
            thumb.compress(Bitmap.CompressFormat.PNG, 100, thumbOut)
            thumbOut!!.close()
            return thumb
        }catch (e:FileNotFoundException){
            return null
            e.printStackTrace()
        }catch (e:IOException){
            return null
            e.printStackTrace()
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, regWidth: Int, regHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if(height > regHeight || width>regWidth){
            val halfHeight = height/2
            val halfWidth = width/2
            while(halfHeight / inSampleSize >= regHeight && halfWidth / inSampleSize >= regHeight)
                inSampleSize *=2
        }
        return inSampleSize
    }
}