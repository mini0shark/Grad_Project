package com.capston.recipe

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.capston.recipe.Api.userApi.UserService
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ConstCodes.RC_SIGN_IN
import com.capston.recipe.Utils.ServerAddress.serverAddress
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.*

class LoginActivity : AppCompatActivity() {
    private val TAG = "LoginActivity"


    lateinit var mGoogleSignInClient: GoogleSignInClient

    lateinit var sf: SharedPreferenceTool
    lateinit var intentToMain :Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        Dexter.withActivity(this)
            .withPermissions(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.ACCESS_NETWORK_STATE)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    init()
                }
                override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?,token: PermissionToken?) {
                    token!!.continuePermissionRequest()
                }

            }).check()

    }
    private fun init(){
        ServerAddress(this)
        intentToMain = Intent(this, MainActivity::class.java)
        sf = SharedPreferenceTool(this)

        var user= sf.loadObjectSharedPreference<UserApiItem>(USER)
        Log.e(TAG, "userActivity----${user}")
        if(user != null){ // id가 있을때(로그인 한적이 있을 때)
            // 서버에서 ID랑  토큰이 맞는지 확인 후 틀리면 login()
            startActivity(intentToMain)
            finish()
        }else{      // id가 없을때(첫 방문)
            login()
        }
    }

    private fun login(){
        val serverClientId = getString(R.string.server_client_id)
        val gso= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .requestProfile()
            .build()
        // requesting additional scope
        // https://developers.google.com/identity/sign-in/android/additional-scopes
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//
        button_signIn.setOnClickListener{
            val intent = mGoogleSignInClient.signInIntent
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    private fun handleSignInResult(completedTask:Task<GoogleSignInAccount>){
        try{
            val account = completedTask.getResult(ApiException::class.java)
//                    ServerAddress(this)
            checkIfHadAAccountAndCreate(account!!, this, this)
            Log.w(TAG, "handleSignInResult")
        }catch (e:ApiException){
            Log.w(TAG, "Google sign in failed", e)
        }

    }
    private fun checkIfHadAAccountAndCreate(account:GoogleSignInAccount, activity: Activity, context: Context){
        val call = UserService(this,this, serverAddress).service.getSingleUser(account.email!!)
        call.enqueue(object: Callback<UserApiItem> {
            override fun onFailure(call: Call<UserApiItem>, t: Throwable) {
                Log.w(TAG, "checkIfHadAAccountAndCreate onFailure\n" +
                        "${t.message}")
                t.printStackTrace()
            }

            override fun onResponse(call: Call<UserApiItem>, response: Response<UserApiItem>) {

                Log.w(TAG, "checkIfHadAAccountAndCreate onResponse")
                val code = response.code()
                if(code == 404){
                    val idToken = account.idToken
                    if (idToken!=null){
                        postSendToken(idToken)
                    }else{
                        Log.w(TAG, "Token is null")
                    }
                }else{
                    val responseUser= response.body()
                    if(responseUser != null){

                        sf.saveObjectSharedPreference(USER, responseUser)
                        val intent =Intent(context, MainActivity::class.java)
                        activity.startActivity(intent)
                        activity.finish()
                    }
                    else{
                        Toast.makeText(applicationContext, "유저 정보가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()

                    }
                }
            }

        })
    }

    private fun postSendToken(idToken: String) {
        val context:Context = this
        ServerAddress(context)
        Log.e("User_Test", "postSendToken")
        UserService(this,context, serverAddress).service.postTokenToServer(hashMapOf("idToken" to idToken)).
            enqueue(object: Callback<UserApiItem>{
                override fun onResponse(call: Call<UserApiItem>, response: Response<UserApiItem>) {
                    val statusCode = response.code()

                    if(response.isSuccessful){
                        Log.e("User_Test", "idToken 성공")
                        Log.e("User_Test", "name  : ${response.body().toString()}")
                        val responseBody = response.body()
                        if(responseBody!=null){
                            Log.i("User_Test", "name  : ${responseBody.name}, email : ${responseBody.userId}")
                            val sf =
                                SharedPreferenceTool(context)
                            sf.saveObjectSharedPreference(USER, responseBody)

                            val fragmentManager = supportFragmentManager
                            fragmentManager.beginTransaction()
                                .add(R.id.frameLayout_mainActivity, InitQuestionFragment())
                                .commit()
                            if(responseBody.memberType!! == 1)   //구글 멤버면
                                startFragment()
                        }
                        else{
                            Log.i("User_Test", "name  : fail (UserService)")
                        }

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


    private fun startFragment(){
        findViewById<LinearLayout>(R.id.linearLayout_signIn).visibility = View.GONE
        val frameLayout = findViewById<FrameLayout>(R.id.frameLayout_mainActivity)
        frameLayout.visibility = View.VISIBLE

    }
}
