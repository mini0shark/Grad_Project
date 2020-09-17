package com.capston.recipe

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.capston.recipe.Adapter.T5MyPageViewPagerAdapter
import com.capston.recipe.Api.userApi.UserService
import com.capston.recipe.Items.MyPageUserInfoApiItem
import com.capston.recipe.Items.UserApiItem
import com.capston.recipe.Utils.ConstCodes.BOOKMARK
import com.capston.recipe.Utils.ConstCodes.FOLLOWER
import com.capston.recipe.Utils.ConstCodes.FOLLOWING
import com.capston.recipe.Utils.ConstCodes.RECIPE
import com.capston.recipe.Utils.ConstCodes.STORY
import com.capston.recipe.Utils.ImageUtils
import com.capston.recipe.Utils.ServerAddress
import com.capston.recipe.Utils.SharedPreferenceTool
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_t5_my_page.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Main5MyPage : Fragment() {
    val TAG = "Main5MyPage Tag"
    private lateinit var bottomNav: BottomNavigationView
    lateinit var tabLayout:TabLayout
    lateinit var viewPager:ViewPager
    lateinit var fragmentAdapter:T5MyPageViewPagerAdapter
    lateinit var fragmentArr:ArrayList<Fragment>
    lateinit var sft: SharedPreferenceTool
    lateinit var user:UserApiItem
    lateinit var fragmentInflater:View
    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.i(TAG, "oncreateView")
        // Inflate the layout for this fragment
        fragmentInflater = inflater.inflate(R.layout.fragment_t5_my_page, container, false)
        sft = SharedPreferenceTool(context!!)
        val temp = sft.loadObjectSharedPreference<UserApiItem>(USER)
        if( temp != null){
            user = temp
        }else{
            sft.saveObjectSharedPreference(USER, null)
            val intent = Intent(activity!!, LoginActivity::class.java)
            startActivity(intent)
            activity!!.finish()

    }
        userInformation()
        tabLayoutSetting()
        viewSetting()



        (activity as AppCompatActivity).supportActionBar?.title = "MyPage"
//        setHasOptionsMenu(true)
        bottomNav = activity!!.findViewById(R.id.bottom_nav_view)

        return fragmentInflater
    }

    private fun viewSetting() {
        fragmentInflater.button_fragmentMyPage_editButton.setOnClickListener {
            hideBottomNavigation()
            findNavController().navigate(R.id.action_myPage_to_editProfile)
        }

        fragmentInflater.layout_fragmentMyPage_recipe.setOnClickListener {
            val scrollView = fragmentInflater.scrollView_fragmentMyPage
            scrollView.scrollTo(0, fragmentInflater.viewPager_fragmentMyPage.bottom)
            viewPager.currentItem = 0

        }
        fragmentInflater.layout_fragmentMyPage_story.setOnClickListener {
            val scrollView = fragmentInflater.scrollView_fragmentMyPage
            scrollView.scrollTo(0, fragmentInflater.viewPager_fragmentMyPage.bottom)
            viewPager.currentItem = 1
        }
        fragmentInflater.layout_fragmentMyPage_follower.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("type", FOLLOWER)
            bundle.putInt("userId", user.id!!)
            hideBottomNavigation()
            findNavController().navigate(R.id.action_myPage_to_followList, bundle)
        }
        fragmentInflater.layout_fragmentMyPage_following.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("type", FOLLOWING)
            bundle.putInt("userId", user.id!!)
            hideBottomNavigation()
            findNavController().navigate(R.id.action_myPage_to_followList, bundle)
        }
    }
    fun hideBottomNavigation(){
        bottomNav.visibility= View.GONE
    }

    private fun userInformation() {
        val userService = UserService(activity!!, context!!, ServerAddress.serverAddress)
        val call = userService.service.getUserPageInfo(user.id!!, user.id!!)
        call.enqueue(object : Callback<MyPageUserInfoApiItem>{
            override fun onResponse(call: Call<MyPageUserInfoApiItem>, response: Response<MyPageUserInfoApiItem>) {
                val responseBody = response.body()
                if(responseBody != null){
                    ImageUtils.putImageIntoCircleView(context!!, responseBody.profileImage, fragmentInflater.imageView_fragmentMyPage_profileImage)
                    fragmentInflater.textView_fragmentMyPage_nickname.text = responseBody.nickname
                    fragmentInflater.textView_fragmentMyPage_introduce.text = responseBody.introduce
                    fragmentInflater.textView_fragmentMyPage_recipeCount.text = responseBody.recipeCount.toString()
                    fragmentInflater.textView_fragmentMyPage_storyCount.text = responseBody.storyCount.toString()
                    fragmentInflater.textView_fragmentMyPage_followers.text = responseBody.followerCount.toString()
                    fragmentInflater.textView_fragmentMyPage_followings.text = responseBody.followingCount.toString()

                }
            }

            override fun onFailure(call: Call<MyPageUserInfoApiItem>, t: Throwable) {
                Log.i(TAG, "Fail to Receive my Infomation")
                call.cancel()
                t.printStackTrace()
            }

        })
    }

    private fun tabLayoutSetting(){
        fragmentArr = arrayListOf()
        fragmentArr.add(T5MyPageTabLayoutItem(user.id!!,RECIPE))
        fragmentArr.add(T5MyPageTabLayoutItem(user.id!!,STORY))
        fragmentArr.add(T5MyPageTabLayoutItem(user.id!!,BOOKMARK))
        fragmentAdapter = T5MyPageViewPagerAdapter(fragmentManager!!)
        tabLayout = fragmentInflater.tabLayout_fragmentMyPage
        viewPager = fragmentInflater.viewPager_fragmentMyPage
        fragmentAdapter.addFragment("Recipe", fragmentArr[0])
        fragmentAdapter.addFragment("Story", fragmentArr[1])
        fragmentAdapter.addFragment("BookMark", fragmentArr[2])
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
    override fun onResume() {
        super.onResume()
        if(bottomNav.visibility== View.GONE)
            bottomNav.visibility=View.VISIBLE
        while(findNavController().currentDestination!!.id!=R.id.myPage)
            findNavController().popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.i(TAG, "ondestView")
        val trans = fragmentManager!!.beginTransaction()
        for(frag in fragmentArr)
            trans.remove(frag)
        trans.commit()
    }

}
