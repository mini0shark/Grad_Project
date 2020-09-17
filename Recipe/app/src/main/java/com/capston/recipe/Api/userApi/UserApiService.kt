package com.capston.recipe.Api.userApi

import com.capston.recipe.Items.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface UserApiService{
    // 서버에 토큰 보내기
    @POST("userApi/tokenSignIn")
    fun postTokenToServer(@Body tokenSet:HashMap<String, String>):Call<UserApiItem>
    @GET("userApi/checkNickName")
    fun getCheckNickName(@Query("nickname") nickname:String):Call<Boolean>
    // user목록
    @GET("userApi/users")
    fun getListUsers():Call<Array<UserApiItem>>
    //
    @GET("userApi/user/{user_id}")
    fun getSingleUser(@Path("user_id") userId:String):Call<UserApiItem>
    // MyPage UserInformation 가져오기
    @GET("userApi/myPage/{user_id}")
    fun getUserPageInfo(@Path("user_id") userId:Int,@Query("from") to:Int):Call<MyPageUserInfoApiItem>
    @GET("userApi/searchUser")
    fun getSimpleUserForSearch(@Query("query") searchId:String):Call<ArrayList<SearchResultUserApiItem>>

    // follow 관련
    @GET("userApi/following/{user_id}")
    fun getFollowingList(@Path("user_id") userId:Int):Call<ArrayList<FollowUserApiItem>>
    @GET("userApi/follower/{user_id}")
    fun getFollowerList(@Path("user_id") userId:Int):Call<ArrayList<FollowUserApiItem>>
    @GET("userApi/followingWeeks/{user_id}")
    fun getWeeksFollowerList(@Path("user_id") userId:Int):Call<ArrayList<FollowUserApiItem>>
    @POST("userApi/follow/{user_id}")
    fun postFollowUser(@Path("user_id") userId:Int
                       , @Query("from") from:Int):Call<Boolean>

    @Multipart
    @PATCH("userApi/user/{user_id}")
    fun patchUpdateStory(@Path("user_id") userId:String,
                          @Part("user") user:UserApiItem,
                          @Part images: MultipartBody.Part?
    ): Call<ResponseBody>


    @POST("userApi/users")
    fun postRegisterUser(@Body user: UserApiItem):Call<ResponseBody>

    @DELETE("userApi/user/{user_id}")
    fun deleteRemoveUser(@Path("user_id") userId:String):Call<ResponseBody>
//    //유저 만들기
//    @FormUrlEncoded
//    @POST("recipeApi/users")
//    fun postRegisterUser(@Field("user_id") userId:String,
//                         @Field("password") password:String,
//                         @Field("introduce") introduce:String
//    ):Call<User>
//
//
//    @FormUrlEncoded
//    @PATCH("recipeApi/user/{user_id}")
//    fun patchUpdateUser(@Path("user_id") userId:String,
//                        @Field("password") password:String?,
//                        @Field("introduce") introduce:String):Call<User>
//

}