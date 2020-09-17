package com.capston.recipe

import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.capston.recipe.Api.userApi.UserService
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.capston.recipe.Utils.WebSocketClientWaiting
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.JsonObject
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.net.URI


const val USER = "user"
class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivityTag"
    val sft = SharedPreferenceTool(this)
    lateinit var id :String
    lateinit var user:UserApiItem
    lateinit var bottomNav:BottomNavigationView
    var clientWaiting: WebSocketClientWaiting?=null
    lateinit var listener:MainActivityController
    object Main{
        const val IMAGE_NAME ="sample.png"
    }
    var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbarController()
        val fragmentTransaction=supportFragmentManager.beginTransaction()

//        val fragments= arrayListOf(Main1Home(),Main2Search(),Main3Registration(),
//            Main4ActivityHistory(), Main5MyPage())
        val curFragment =  supportFragmentManager.primaryNavigationFragment
        if(curFragment != null){
            fragmentTransaction.hide(curFragment)
        }
        var fragment = supportFragmentManager.findFragmentByTag("home")
        if(fragment == null){
            fragment = Main1Home()
            fragmentTransaction.add(R.id.fragment_home, fragment, "1")
        }else{
            fragmentTransaction.show(fragment)
        }
        fragmentTransaction.setPrimaryNavigationFragment(fragment)
        fragmentTransaction.setReorderingAllowed(true)
        fragmentTransaction.commitNowAllowingStateLoss()

        ServerAddress(this)
        var tempUser  = sft.loadObjectSharedPreference<UserApiItem>(USER)
        if(tempUser== null){
            logout()
        }
        else{
            val userService = UserService(this,this, ServerAddress.serverAddress)
            Thread{
                kotlin.run {

                    val tempId= tempUser!!.userId!!
                    tempUser= userService.getUser(tempId)
                    if(tempUser == null){
                        logout()
                    }
                    else {
                        user = tempUser!!
                        id = user.userId!!
                        startConnection()
                    }
                }
            }.start()
        }


        val host:NavHostFragment=supportFragmentManager
            .findFragmentById(R.id.fragment_home) as NavHostFragment? ?:return

        setupBottomNavMenu(host.navController)
    }

    private fun startConnection() {
        val uri = URI("${ServerAddress.serverAddress}/ws/waiting/${user!!.id}/")
        clientWaiting = WebSocketClientWaiting(this, uri)
        clientWaiting!!.connect()
        Log.e(TAG, "$uri")
        clientWaiting!!.setOnResponseControlListener(object:WebSocketClientWaiting.ResponseController{
            override fun newChattingListener(json: JSONObject) {
                flag = json.getBoolean("flag")
                try {
                    listener.onReceiveMessage(flag)
                    listener.onReceiveMessage(json)
                }catch (e:Exception){

                }
            }

            override fun channelOpenListener() {
            }
        })
    }
    fun checkNewMessage(id:Int){
        Log.i(TAG, "send!!!!")
        if(clientWaiting!=null){
            val json = JSONObject()
            json.put("id", id)
            clientWaiting!!.send(json.toString())
        }
    }
    interface MainActivityController{
        fun onReceiveMessage(flag:Boolean)
        fun onReceiveMessage(json:JSONObject)
    }
    fun setOnMainActivityControlListener(listener:MainActivityController){
        this.listener = listener
    }
    fun toolbarController(){
        val toolbar = toolBar_fragmentHome
        setSupportActionBar(toolbar)
    }

    fun logout(){
//        Log.d(TAG, "id : ${sft.loadObjectSharedPreference<UserApiItem>(USER)!!.userId}, Tok = ${sft.loadObjectSharedPreference<UserApiItem>(USER)!!.name}")
//        sft.saveSharedPreference(ID, "")
//        sft.saveSharedPreference(NAME, "")
//        sft.saveSharedPreference(EMAIL, "")
        sft.saveObjectSharedPreference(USER, null)
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun setupBottomNavMenu(navController: NavController){
        bottomNav = findViewById(R.id.bottom_nav_view)
        bottomNav?.setupWithNavController(navController)
    }
    override fun onDestroy() {
        super.onDestroy()
        sft.saveObjectSharedPreference("homePageItem", null)
        if(clientWaiting!=null)
            clientWaiting!!.close()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.i(TAG, "mainAct")
        Log.i(TAG, "resCode = $resultCode, requestcode : $requestCode")
        Log.i(TAG, "ucrop = ${UCrop.REQUEST_CROP}")
    }
    private var pressedTime: Long = 0
    // 리스너 생성
    interface OnBackPressedListener {
        fun onBack()
        fun onBackWhenProgress()
    }

    // 리스너 객체 생성
    private var mBackListener: OnBackPressedListener? = null

    // 리스너 설정 메소드
    fun setOnBackPressedListener(listener: OnBackPressedListener?) {
        mBackListener = listener
    }
    // 리스너
    override fun onBackPressed() {
        // 다른 Fragment 에서 리스너를 설정했을 때 처리됩니다.
        if (mBackListener != null) {
            mBackListener!!.onBackWhenProgress()
            mBackListener!!.onBack()
            supportFragmentManager.popBackStack()
//            findNavController(R.id.fragment_home).popBackStack()
//            supportFragmentManager.popBackStackImmediate()
            Log.e("!!!", "Listener is not null")
            // 리스너가 설정되지 않은 상태(예를들어 메인Fragment)라면
            // 뒤로가기 버튼을 연속적으로 두번 눌렀을 때 앱이 종료됩니다.
        } else {
            Log.e("!!!", "Listener is null")
            val view = layout_main
            if (pressedTime.toInt() == 0) {
                Snackbar.make(
                    view,
                    " 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG
                ).show()
                pressedTime = System.currentTimeMillis()
            } else {
                val seconds = (System.currentTimeMillis() - pressedTime)
                if (seconds > 2000) {
                    Snackbar.make(
                        view,
                        " 한 번 더 누르면 종료됩니다.", Snackbar.LENGTH_LONG
                    ).show()
                    pressedTime = 0
                } else {
                    super.onBackPressed()
                    Log.e("!!!", "onBackPressed : finish, killProcess")
                    finish()
                    Process.killProcess(Process.myPid())
                }
            }
        }
    }
}
