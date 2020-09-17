package com.capston.recipe


import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.capston.recipe.Api.userApi.UserService
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import kotlinx.android.synthetic.main.fragment_t5_edit_profile.view.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class T5EditProfile : Fragment() {
    val TAG ="T5EditProfile tag"
    lateinit var fragmentInflater:View
    lateinit var sft: SharedPreferenceTool
    var user:UserApiItem? =null
    private lateinit var checkArr:ArrayList<CheckBox>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentInflater = inflater.inflate(R.layout.fragment_t5_edit_profile, container, false)
        sft = SharedPreferenceTool(context!!)
        user = sft.loadObjectSharedPreference<UserApiItem>(USER)
        if( user == null){
            sft.saveObjectSharedPreference(USER, null)
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
            return null
        }
        (activity as AppCompatActivity).supportActionBar?.title = "Edit Profile"
        //--사진 편집
        ImageUtils.putImageIntoView(context!!, user!!.profileImage, fragmentInflater.imageView_editProfile_profileImage)
        fragmentInflater.imageView_editProfile_profileImage.setOnClickListener {
            val imageAddFragment = EditSingleImageFragment()
            val transaction = fragmentManager!!.beginTransaction()
            transaction.add(R.id.frameLayout_editProfile_editImage, imageAddFragment, "edit")
            transaction.addToBackStack(null)
            transaction.commit()
//            fragmentInflater.scrollView_editProfile.visibility = View.GONE
            fragmentInflater.frameLayout_editProfile_editImage.visibility = View.VISIBLE
            imageAddFragment.setDoneClickListener(object : EditSingleImageFragment.DoneClickListener{
                override fun onClickDone(imgList: java.util.ArrayList<Bitmap>) {
                    fragmentInflater.frameLayout_editProfile_editImage.visibility = View.GONE
                    for(img in imgList)
                        fragmentInflater.imageView_editProfile_profileImage.setImageBitmap(img)
                }

                override fun onCancel() {
                    (activity!! as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
                    fragmentInflater.frameLayout_editProfile_editImage.visibility = View.GONE
//            fragmentInflater.scrollView_editProfile.visibility = View.VISIBLE
                }

            })
        }
        //사진 편집--
        //--유저 이름
        fragmentInflater.editText_editProfile_nickName.hint = user!!.nickname
        fragmentInflater.editText_editProfile_nickName.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "$s")
                if(user!!.nickname == s.toString()){
                    fragmentInflater.imageView_editProfile_checkNickName.setImageResource(R.drawable.ic_check_black_24dp)
                    fragmentInflater.imageView_editProfile_checkNickName.setColorFilter(Color.parseColor("#FF1DDB16"), PorterDuff.Mode.SRC_IN)
                    fragmentInflater.imageView_editProfile_checkNickName.tag = "True"
                }else{
                    fragmentInflater.imageView_editProfile_checkNickName.setImageResource(R.drawable.ic_clear_black_24dp)
                    fragmentInflater.imageView_editProfile_checkNickName.setColorFilter(Color.parseColor("#FFFF0000"), PorterDuff.Mode.SRC_IN)
                    fragmentInflater.imageView_editProfile_checkNickName.tag = "False"
                    val service = UserService(activity!!, context!!, ServerAddress.serverAddress).service
                    val call = service.getCheckNickName(s.toString())
                    call.enqueue(object : Callback<Boolean>{
                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            val responseBody = response.body()
                            fragmentInflater.textView_editProfile_nickNameCheck.visibility= View.VISIBLE
                            if(responseBody==true){
                                fragmentInflater.imageView_editProfile_checkNickName.setImageResource(R.drawable.ic_check_black_24dp)
                                fragmentInflater.imageView_editProfile_checkNickName.setColorFilter(Color.parseColor("#FF1DDB16"), PorterDuff.Mode.SRC_IN)
                                fragmentInflater.imageView_editProfile_checkNickName.tag = "True"
                                fragmentInflater.textView_editProfile_nickNameCheck.text = "사용 가능한 닉네임 입니다."
                            }else{
                                fragmentInflater.imageView_editProfile_checkNickName.setImageResource(R.drawable.ic_clear_black_24dp)
                                fragmentInflater.imageView_editProfile_checkNickName.setColorFilter(Color.parseColor("#FFFF0000"), PorterDuff.Mode.SRC_IN)
                                fragmentInflater.imageView_editProfile_checkNickName.tag = "False"
                                fragmentInflater.textView_editProfile_nickNameCheck.text = "존재하는 닉네임입니다."
                            }

                        }
                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                            fragmentInflater.imageView_editProfile_checkNickName.setImageResource(R.drawable.ic_clear_black_24dp)
                            fragmentInflater.imageView_editProfile_checkNickName.setColorFilter(Color.parseColor("#FFFF0000"), PorterDuff.Mode.SRC_IN)
                            fragmentInflater.imageView_editProfile_checkNickName.tag = "False"
                            fragmentInflater.textView_editProfile_nickNameCheck.text = "통신에 실패해 확인할 수 없습니다."
                        }

                    })
                }
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                fragmentInflater.textView_editProfile_nickNameCheck.visibility= View.INVISIBLE
            }
        })
        //유저 이름--

        fragmentInflater.editText_editProfile_introduce.hint = user!!.introduce

        checkArr = arrayListOf(fragmentInflater.checkBox1_editProfile,fragmentInflater.checkBox2_editProfile,
            fragmentInflater.checkBox3_editProfile,fragmentInflater.checkBox4_editProfile, fragmentInflater.checkBox5_editProfile,
            fragmentInflater.checkBox6_editProfile, fragmentInflater.checkBox7_editProfile , fragmentInflater.checkBox8_editProfile,
            fragmentInflater.checkBox9_editProfile, fragmentInflater.checkBox10_editProfile, fragmentInflater.checkBox11_editProfile)
        val favorite = user!!.favoriteFood?.split(",")
        if (favorite != null) {
            for(item in favorite)
                for(check in checkArr)
                    if(check.text == item)
                        check.isChecked = true
        }
        fragmentInflater.button_editProfile_submit.setOnClickListener {
            if(fragmentInflater.editText_editProfile_nickName.text.contains(" ")){
                Toast.makeText(context!!, "띄어쓰기 노노해!!", Toast.LENGTH_SHORT).show()
            }else {
                sendListen()
            }
        }
        fragmentInflater.layout_editProfile.setOnClickListener {
            hideKeyboard()
        }

        return fragmentInflater
    }
    private fun hideKeyboard(){
        val inputMethodManager= activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(fragmentInflater.windowToken, 0)
    }

    private fun sendListen() {
        val editedUser = UserApiItem(id=user!!.id)
        val userProfileImage = ImageUtils.bitmapToMultiPartBodyPart(context!!
            , fragmentInflater.imageView_editProfile_profileImage.drawable.toBitmap()
        ,"userProfile_${editedUser.id}", "profile", -1)
        editedUser.nickname = if(fragmentInflater.editText_editProfile_nickName.text.isNullOrEmpty()) null
        else fragmentInflater.editText_editProfile_nickName.text.toString()
        editedUser.introduce =  if(fragmentInflater.editText_editProfile_introduce.text.isNullOrEmpty()) null
        else fragmentInflater.editText_editProfile_introduce.text.toString()

        var favoriteFood = ""
        for (check in checkArr) {
            if (check.isChecked) {
                if (favoriteFood != "")
                    favoriteFood += ","
                favoriteFood += check.text.toString()
            }
        }
        editedUser.favoriteFood = favoriteFood
        val service = UserService(activity!!, context!!, ServerAddress.serverAddress).service
        val call = service.patchUpdateStory(user!!.userId!!, editedUser, userProfileImage)
        call.enqueue(object:Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
//                findNavController().popBackStack()
                if(response.isSuccessful){
                    if(context!=null)
                        Toast.makeText(context!!, "성공적으로 수정했습니다.", Toast.LENGTH_SHORT).show()
                    findNavController().popBackStack()
                    service.getSingleUser(user!!.userId!!).enqueue(object : Callback<UserApiItem>{
                        override fun onFailure(call: Call<UserApiItem>, t: Throwable) {}
                        override fun onResponse(call: Call<UserApiItem>,response: Response<UserApiItem>) {
                            val responseBody = response.body()
                            sft.saveObjectSharedPreference(USER, responseBody)
                        }
                    })
                }else{
                    Toast.makeText(context!!, "수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                call.cancel()
                t.printStackTrace()
                Toast.makeText(context!!, "네트워크 연결을 확인하세요.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
