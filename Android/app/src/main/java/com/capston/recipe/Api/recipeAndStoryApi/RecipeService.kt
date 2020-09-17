package com.capston.recipe.Api.recipeAndStoryApi

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.capston.recipe.Api.RetrofitClient
import com.capston.recipe.Api.chatApi.RecipeApiService
import com.capston.recipe.Items.RecipeDetailApiItem
import com.capston.recipe.Items.RecipeContainerApiItem
import com.capston.recipe.Items.RegisterRecipeApiItem
import com.capston.recipe.Utils.SharedPreferenceTool
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class RecipeService(val activity: Activity, val context:Context, API_URL:String){
    //    var gsonBuilder = GsonBuilder().registerTypeAdapter(LocalDateTime, )
    val TAG ="RecipeService"
    private lateinit var sft: SharedPreferenceTool
    var retrofit: Retrofit= RetrofitClient.getClient(API_URL)
    var service: RecipeApiService
    internal lateinit var result:String
    init {
        service = retrofit.create(RecipeApiService::class.java)
    }

    fun postRegisterRecipeToServer(user:Int,
                                   registerRecipeItem: RegisterRecipeApiItem,
                                   body:ArrayList<MultipartBody.Part>,
                                   fragmentManager:FragmentManager){
        val call = service.postRegisterRecipe(user, registerRecipeItem, body)
        call.enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                call.cancel()
                Toast.makeText(context, "ERROR ", Toast.LENGTH_SHORT).show()
                Log.i("tag", "ERROR : \n${t.printStackTrace()}")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    Toast.makeText(context, "Image upload successfully", Toast.LENGTH_SHORT).show()
                    //Fragment main으로 이동 후 닫기
                }else{
                    Toast.makeText(context, "ResponseCode : ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    fun patchRegisterRecipeToServer(
        user:Int,
        registerRecipeItem: RegisterRecipeApiItem,
        body: ArrayList<MultipartBody.Part>,
        fragmentManager:FragmentManager){
        val call = service.postEditionRegisterRecipe(user, registerRecipeItem, body)
        call.enqueue(object: Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                call.cancel()
                Toast.makeText(context, "ERROR ", Toast.LENGTH_SHORT).show()
                Log.i("tag", "ERROR : \n${t.printStackTrace()}")
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    Toast.makeText(context, "Image upload successfully", Toast.LENGTH_SHORT).show()
                    //Fragment main으로 이동 후 닫기
                }else{
                    Toast.makeText(context, "ResponseCode : ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    fun postLike(userId:Int,
                 recipeId:Int,
                 flag:Boolean,
                 volumeTextView:TextView){
        var call = service.postClickRecipeLike(userId, recipeId, flag)
        call.enqueue(object : Callback<Boolean>{
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {

                val responseBody = response.body()
                val current = volumeTextView.text.toString().toInt()
                Log.i(TAG, "res $responseBody")
                if(responseBody!=null && responseBody){
                    volumeTextView.text = "${current+1}"
                    Log.i(TAG, "like $current")
                }else if( responseBody!=null&& !responseBody && current>0){
                    volumeTextView.text = "${current-1}"
                    Log.i(TAG, "unlike$current")
                }else{
                    Log.i(TAG, "else $current")
                }
                Log.i(TAG, "$responseBody")
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                call.cancel()
                Log.i(TAG, "fail => printstck trace")
                t.printStackTrace()
            }

        })
    }

    fun getRecipeList(userId:Int){
        val call = service.getRecipeContainer(userId)
        call.enqueue(object: Callback<Array<RecipeContainerApiItem>>{
            override fun onFailure(call: Call<Array<RecipeContainerApiItem>>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<Array<RecipeContainerApiItem>>, response: Response<Array<RecipeContainerApiItem>>) {
                Log.i("tag", "=================start================" )
                Log.i("tag", "${response.body()}\n" )
                val recipe = response.body()
                if(recipe != null){
                    for( res in recipe){
                        Log.i("tag", "$res" )
                    }

                }
            }

        })
    }

    fun getHomeItemList(userId:Int){
        val call = service.getHomeItems(userId)
        call.enqueue(object: Callback<RecipeDetailApiItem>{
            override fun onFailure(call: Call<RecipeDetailApiItem>, t: Throwable) {
                t.printStackTrace()
            }

            override fun onResponse(call: Call<RecipeDetailApiItem>, response: Response<RecipeDetailApiItem>) {
                val homeItem = response.body()
                Log.i("tag", "=================start================" )
                Log.i("tag", "${homeItem}\n" )

            }

        })
    }

}