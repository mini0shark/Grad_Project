package com.capston.recipe.Api.userApi

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.capston.recipe.*
import com.capston.recipe.Api.RetrofitClient
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.SharedPreferenceTool
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UserService( val activity: Activity, val context:Context, API_URL:String){
    //    var gsonBuilder = GsonBuilder().registerTypeAdapter(LocalDateTime, )
    private val TAG = "UserServiceTag"
    private lateinit var sft: SharedPreferenceTool
    val retrofit: Retrofit = RetrofitClient.getClient(API_URL)
    var service: UserApiService
    internal lateinit var result:String
    init {
        service = retrofit.create(UserApiService::class.java)
    }


    fun postSendTokenToServer(idToken:String){
        service.postTokenToServer(hashMapOf("idToken" to idToken)).enqueue(object: Callback<UserApiItem>{
            override fun onResponse(call: Call<UserApiItem>, response: Response<UserApiItem>) {
                val statusCode = response.code()

                if(response.isSuccessful){
                    Log.e("User_Test", "idToken 성공")
                    Log.e("User_Test", "name  : ${response.body().toString()}")
                    val responseBody = response.body()

                    if(responseBody!=null)
                        Log.i("User_Test", "name  : ${responseBody.name}, email : ${responseBody.userId}")
                    else
                        Log.i("User_Test", "name  : fail (UserService)")
                    val sf =
                        SharedPreferenceTool(context)
                    sf.saveObjectSharedPreference(USER, responseBody)

//                    if(responseBody.memberType!! > 0){
//                    }else{
//                        startFragment()
//                    }
                }else{
                    if(statusCode==404) {
                        // 서버와 연결할 수 없습니다.
                        Toast.makeText(context, "서버와 연결할 수 없습니다.", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Log.e("User_Test", "에러코드 111: $statusCode :\n ${response.message()}," +
                                "\n\n${response.headers()}, \n\n ${response.errorBody()}" +
                                "\n\n${response.isSuccessful}")
                    }
                }
            }

            override fun onFailure(call: Call<UserApiItem>, t: Throwable) {
                Log.e(TAG, "Err sending ID token to backend.")
                t.printStackTrace()
            }
        })
    }



    // user 한명 가져오기
    fun getConfirmNicknameIsAvailable(nickname:String){
        service.getCheckNickName(nickname).enqueue(object: Callback<Boolean>{
            override fun onFailure(call: Call<Boolean>, t: Throwable) {
            }

            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                val isNicknameAvailable = response.body()
                try {

                    if(isNicknameAvailable!!) {
                        val image = activity.findViewById<ImageView>(R.id.imageView_checkNickName)
                        image.setImageResource(R.drawable.ic_check_black_24dp)
                        image.setColorFilter(Color.parseColor("#FF1DDB16"), PorterDuff.Mode.SRC_IN)
                        image.tag = "True"
                    }else{
                        // 닉네임이 이미 존자한다는 다이얼로그
                        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
                        builder.setTitle("Nickname is already exist.")
                        builder.setTitle("해당 닉네임이 이미 존재합니다. 다른 닉네임을 입력해 주세요!")
                        builder.setPositiveButton("확인"){ _, _ ->
                        }
                        builder.show()
                    }
                }catch (e:Exception){
                    e.printStackTrace()
                    result = "Fail to accss $nickname"
                    Log.e(TAG, "nickname $result")
                }

            }

        })
    }
    fun patchInitialSettingUser(user: UserApiItem){
        val id = user.userId
        user.userId=null
//        service.patchUpdateUser(id, user.password, user.introduce)?.enqueue(object: Callback<User>{
        service.patchUpdateStory(id!!, user, null).enqueue(object: Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if(response.isSuccessful){
                    Log.e("User_Test", "성공")
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                    activity.finish()
                }else{
                    val statusCode = response.code()
                    Log.e("User_Test", "에러코드 : $statusCode")
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("User_Test", "닥치고 실패..ㅠㅠ")
            }
        })

    }


    // 유저 목록 가져오기
    fun getUsers(){
        service.getListUsers().enqueue(object:Callback<Array<UserApiItem>>{
            override fun onResponse(call: Call<Array<UserApiItem>>, response: Response<Array<UserApiItem>>) {
                try {
                    result = ""
                    for (usr in response.body()!!){
                        result += usr.toString()+"\n"
                    }

                    Log.e("User_Text", result+" : "+ response.body()!![0].toString())
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<Array<UserApiItem>>, t: Throwable) {
                result = "Fail"
                Log.e("User_Text", result+" : "+service)
            }

        })
    }
    // user 한명 가져오기
    fun getUser(user_id:String):UserApiItem?{
        return service.getSingleUser(user_id).execute().body()
//        service.getSingleUser(user_id)?.enqueue(object:Callback<UserApiItem>{
//            override fun onResponse(call: Call<UserApiItem>, response: Response<UserApiItem>) {
//                try{
//                    result = response.body()!!.toString()
//                    Log.e("User_Text", result)
//                }catch (e: Exception){
//                    e.printStackTrace()
//                    result = "Fail to accss $user_id"
//                    Log.e("User_Text", result)
//                }
//            }
//
//            override fun onFailure(call: Call<UserApiItem>, t: Throwable) {
//                result = "Fail"
//                Log.e("User_Text", result+" : "+retrofit.toString())
//            }
//
//        })
    }
    //  유저 등록
    fun postUsers(user: UserApiItem){
//        service.postRegisterUser(user.userId, user.password, user.introduce)?.enqueue(object: Callback<User>{
        service.postRegisterUser(user).enqueue(object: Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e("User_Test", "Post_test")
                if(response.isSuccessful){
                    Log.e("User_Test", "성공")
                }else{
                    val statusCode = response.code()
                    Log.e("User_Test", "에러코드 : $statusCode :\n ${response.message()},\n\n${response.headers()}, \n\n ${response.errorBody()}")
                    if(statusCode==400){
                        Log.e("User_Test", "id가 존재합니다.")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("User_Test", "닥치고 실패..ㅠㅠ")
            }
        })
    }
    // 수정
//    fun patchUsers(user: UserApiItem){
//        val id = user.userId
//        user.userId=null
////        service.patchUpdateUser(id, user.password, user.introduce)?.enqueue(object: Callback<User>{
//        service.patchUpdateUser(id!!, user).enqueue(object: Callback<UserApiItem>{
//            override fun onResponse(call: Call<UserApiItem>, response: Response<UserApiItem>) {
//                Log.e("User_Test", "Put_test")
//                if(response.isSuccessful){
//                    Log.e("User_Test", "성공")
//                }else{
//                    val statusCode = response.code()
//                    Log.e("User_Test", "에러코드 : $statusCode")
//                }
//            }
//
//            override fun onFailure(call: Call<UserApiItem>, t: Throwable) {
//                Log.e("User_Test", "닥치고 실패..ㅠㅠ")
//            }
//        })
//    }

    fun deleteUser(user_id:String){
        service.deleteRemoveUser(user_id).enqueue(object: Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.e("User_Test", "Put_test")
                if(response.isSuccessful){
                    Log.e("User_Test", "성공 : ${response.body()}")
                }else{
                    val statusCode = response.code()
                    Log.e("User_Test", "에러코드 : $statusCode")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("User_Test", "닥치고 실패..ㅠㅠ")
            }
        })
    }
}