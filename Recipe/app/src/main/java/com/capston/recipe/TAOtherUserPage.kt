package com.capston.recipe


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.capston.recipe.Adapter.T5MyPageViewPagerAdapter
import com.capston.recipe.Api.userApi.UserService
import com.capston.recipe.Items.MyPageUserInfoApiItem
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ConstCodes.FOLLOWER
import com.capston.recipe.Utils.ConstCodes.FOLLOWING
import com.capston.recipe.Utils.ConstCodes.RECIPE
import com.capston.recipe.Utils.ConstCodes.STORY
import com.capston.recipe.Utils.GettingUser
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_t5_my_page.view.*
import kotlinx.android.synthetic.main.fragment_ta_other_user_page.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TAOtherUserPage : Fragment() {
    val TAG = "TAOtherUserPage tag"
    lateinit var tabLayout:TabLayout
    lateinit var viewPager:ViewPager
    lateinit var fragmentAdapter:T5MyPageViewPagerAdapter
    lateinit var fragmentArr:ArrayList<Fragment>
    lateinit var sft: SharedPreferenceTool
    var userId:Int?=null
    var user:UserApiItem?=null
    lateinit var fragmentInflater:View
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "oncreateView")
        // Inflate the layout for this fragment
        try {
            user = GettingUser.getUserInfo(context!!)!!
        }catch (e:NullPointerException){
            e.printStackTrace()
            GettingUser.sft.saveObjectSharedPreference(USER, null)
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()
            return null
        }
        fragmentInflater = inflater.inflate(R.layout.fragment_ta_other_user_page, container, false)
        userId = arguments?.getInt("userId")!!

        userInformation()
        tabLayoutSetting()
        viewSetting()
        return fragmentInflater
    }

    private fun viewSetting() {
        fragmentInflater.layout_fragmentOtherUsers_recipe.setOnClickListener {
            val scrollView = fragmentInflater.scrollView_fragmentOtherUsers
            scrollView.scrollTo(0, fragmentInflater.viewPager_fragmentOtherUsers.bottom)
            viewPager.currentItem = 0

        }
        fragmentInflater.layout_fragmentOtherUsers_story.setOnClickListener {
            val scrollView = fragmentInflater.scrollView_fragmentOtherUsers
            scrollView.scrollTo(0, fragmentInflater.viewPager_fragmentOtherUsers.bottom)
            viewPager.currentItem = 1
        }
        fragmentInflater.layout_fragmentOtherUsers_follower.setOnClickListener {
            val bundle=Bundle()
            bundle.putInt("type", FOLLOWER)
            bundle.putInt("userId", userId!!)
            findNavController().navigate(R.id.action_otherUserPage_to_followList, bundle)
        }
        fragmentInflater.layout_fragmentOtherUsers_following.setOnClickListener {
            val bundle=Bundle()
            bundle.putInt("type", FOLLOWING)
            bundle.putInt("userId", userId!!)
            findNavController().navigate(R.id.action_otherUserPage_to_followList, bundle)
        }
    }

    private fun userInformation() {
        val userService = UserService(activity!!, context!!, ServerAddress.serverAddress)
        val call = userService.service.getUserPageInfo(userId!!, user!!.id!!)
        call.enqueue(object : Callback<MyPageUserInfoApiItem> {
            override fun onResponse(call: Call<MyPageUserInfoApiItem>, response: Response<MyPageUserInfoApiItem>) {
                val responseBody = response.body()
                if(responseBody != null){
                    ImageUtils.putImageIntoView(context!!, responseBody.profileImage
                        , fragmentInflater.imageView_fragmentOtherUsers_profileImage)
                    ImageUtils.putImageIntoCircleView(context!!, responseBody.profileImage, fragmentInflater.imageView_fragmentOtherUsers_profileImage)
                    Log.i(TAG, "${responseBody.profileImage}")
                    (activity as AppCompatActivity).supportActionBar?.title = responseBody.nickname
                    fragmentInflater.textView_fragmentOtherUsers_nickname.text = responseBody.nickname
                    fragmentInflater.textView_fragmentOtherUsers_introduce.text = responseBody.introduce
                    fragmentInflater.textView_fragmentOtherUsers_recipeCount.text = responseBody.recipeCount.toString()
                    fragmentInflater.textView_fragmentOtherUsers_storyCount.text = responseBody.storyCount.toString()
                    fragmentInflater.textView_fragmentOtherUsers_followers.text = responseBody.followerCount.toString()
                    fragmentInflater.textView_fragmentOtherUsers_followings.text = responseBody.followingCount.toString()
                    if(user!!.id == responseBody.id) {
                        fragmentInflater.textView_otherUserPage_follow.visibility = View.GONE
                    }
                    else {
                        setHasOptionsMenu(true)
                        if (responseBody.isFollow) {
                            unFollow()
                        } else {
                            follow()
                        }
                        fragmentInflater.button_otherUserPage_follow.setOnClickListener {
                            followButton()
                        }
                    }


                }
            }

            override fun onFailure(call: Call<MyPageUserInfoApiItem>, t: Throwable) {
                Log.i(TAG, "Fail to Receive my Information")
                call.cancel()
                t.printStackTrace()
            }

        })
    }
    fun follow(){   //팔로우가 안되어있을 때
        fragmentInflater.textView_otherUserPage_follow.setTextColor(resources.getColor(R.color.follow_color))
        fragmentInflater.textView_otherUserPage_follow.text = "팔로우 하기"
    }
    fun unFollow(){ //팔로우가 되어있을 때
        fragmentInflater.textView_otherUserPage_follow.setTextColor(resources.getColor(R.color.unfollow_color))
        fragmentInflater.textView_otherUserPage_follow.text = "팔로우 취소"
    }

    private fun followButton() {
        val service = UserService(activity!!, context!!, ServerAddress.serverAddress).service
        val call = service.postFollowUser(userId!!, user!!.id!!)
        call.enqueue(object : Callback<Boolean>{
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                if(response.isSuccessful){
                    val responseBody = response.body()
                    if(responseBody!!){
                        // 팔로우 X-> 팔로우
                        unFollow()
                    }else{
                        // 팔로우-> 팔로우 X
                        follow()
                    }
                }else{
                    Log.i(TAG, "Fail to Follow")
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.i(TAG, "Fail to Follow")
                call.cancel()
                t.printStackTrace()
            }
        })
    }

    private fun tabLayoutSetting(){
        fragmentArr = arrayListOf()
        fragmentArr.add(T5MyPageTabLayoutItem(userId!!, RECIPE))
        fragmentArr.add(T5MyPageTabLayoutItem(userId!!, STORY))
        fragmentAdapter = T5MyPageViewPagerAdapter(fragmentManager!!)
        tabLayout = fragmentInflater.tabLayout_fragmentOtherUsers
        viewPager = fragmentInflater.viewPager_fragmentOtherUsers
        fragmentAdapter.addFragment("Recipe", fragmentArr[0])
        fragmentAdapter.addFragment("Story", fragmentArr[1])
        viewPager.adapter = fragmentAdapter

        viewPager.addOnPageChangeListener(
            TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object:TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab) {
                var i = tab.position
                viewPager.currentItem = i
                tabIconSelect()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) { }
            override fun onTabReselected(tab: TabLayout.Tab) { }
        })
        tabLayout.setupWithViewPager(viewPager)
        tabIconSelect()
    }
    fun tabIconSelect() {
        for (i in 0..1) {
            val tab = tabLayout.getTabAt(i)!!
            if (tab.isSelected) {
                // TabBtnText.get(i).setTextColor(resources.getColor(R.color.point))
            } else {
                // TabBtnText.get(i).setTextColor(resources.getColor(R.color.lightGray))
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "ondestView")
        val trans = fragmentManager!!.beginTransaction()
        for(frag in fragmentArr)
            trans.remove(frag)
        trans.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu_mypage, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.menuItem_myPage_chatting->{
                val bundle = Bundle()
                bundle.putInt("recipeId", -1)
                bundle.putInt("opponentUserId", userId!!)
                findNavController().navigate(R.id.action_otherUserPage_to_chatting  , bundle)
                //단순 1대 1대화
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        while(findNavController().currentDestination!!.id!=R.id.otherUserPage)
            findNavController().popBackStack()
    }

}
