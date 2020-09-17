package com.capston.recipe

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Api.userApi.UserService
import kotlinx.android.synthetic.main.fragment_init_question.*
import kotlinx.android.synthetic.main.fragment_init_question.view.*

class InitQuestionFragment : Fragment() {
    private val TAG ="InitQuestion Tag"
    private lateinit var sft: SharedPreferenceTool
    private lateinit var name:String
    private lateinit var userService: UserService
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val fragmentInflater = inflater.inflate(R.layout.fragment_init_question, container, false)
        sft = SharedPreferenceTool(context!!)
        userService = UserService(activity!!, context!!, ServerAddress.serverAddress)
        name=sft.loadObjectSharedPreference<UserApiItem>(USER)!!.name!!
        Log.w(TAG, "initQuestion $name")
        viewsListeners(fragmentInflater)
        Log.i(TAG, fragmentInflater.editText_user_nickName.hint.toString())
        // 닉네임 안겹치는 처리
        return fragmentInflater
    }
    private fun viewsListeners(fragmentInflater:View){
        var nickName ="${fragmentInflater.editText_user_nickName.hint} ($name)"
        fragmentInflater.editText_user_nickName.hint = nickName

        fragmentInflater.radioGroup_nickName.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.radioButton_nickName1 ->{
                    fragmentInflater.editText_user_nickName.setText(name)
                }
                R.id.radioButton_nickName2 ->{
                    if(editText_user_nickName.text.toString() == name)
                        fragmentInflater.editText_user_nickName.setText("")
                }
            }
        }
        fragmentInflater.editText_user_nickName.setOnClickListener {
            fragmentInflater.radioButton_nickName2.isChecked=true
        }
        fragmentInflater.imageView_checkNickName.setColorFilter(Color.parseColor("#FFFF0000"), PorterDuff.Mode.SRC_IN)
        fragmentInflater.editText_user_nickName.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                fragmentInflater.imageView_checkNickName.setImageResource(R.drawable.ic_clear_black_24dp)
                fragmentInflater.imageView_checkNickName.setColorFilter(Color.parseColor("#FFFF0000"), PorterDuff.Mode.SRC_IN)
                fragmentInflater.imageView_checkNickName.tag = "False"
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
        fragmentInflater.button_nicknameCheck.setOnClickListener {
            nickName = fragmentInflater.editText_user_nickName.text.toString()
            userService.getConfirmNicknameIsAvailable(nickName)
            Log.i("initQuestionFragment", "log : $nickName")
        }
        fragmentInflater.button_submit.setOnClickListener {
            if(fragmentInflater.imageView_checkNickName.tag =="True"){
                sft = SharedPreferenceTool(context!!)
                val checkArr = arrayListOf<CheckBox>(checkBox1,checkBox2,checkBox3
                    ,checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9, checkBox10, checkBox11)
                var favoriteFood = ""
                for (check in checkArr) {
                    if (check.isChecked) {
                        if (favoriteFood != "")
                            favoriteFood += ","
                        favoriteFood += check.text.toString()
                    }
                }
                Log.e(TAG, favoriteFood)
                val email= sft.loadObjectSharedPreference<UserApiItem>(USER)!!.userId
                name=sft.loadObjectSharedPreference<UserApiItem>(USER)!!.name!!
                val user = UserApiItem(
                    userId = email,
                    name = name,
                    introduce = fragmentInflater.editText_introduce.text.toString(),
                    favoriteFood = favoriteFood,
                    nickname = fragmentInflater.editText_user_nickName.text.toString(),
                    memberType= null,
                    profileImage=null,
                    followers=null
                )
                Log.e(TAG, user.toString())
                userService.patchInitialSettingUser(user)
                Log.i(TAG, favoriteFood)

            }else{
                val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AppTheme))
                builder.setTitle("Nickname is already exist.")
                builder.setTitle("중복확인을 눌러주세요!!")
                builder.setPositiveButton("확인"){dialog, id ->
                }
                builder.show()
            }
        }
    }
}
