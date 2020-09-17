package com.capston.recipe


import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_t3_registration.view.*

class Main3Registration : Fragment() {
    val TAG = "Main3Registration tag"
    lateinit var bottomNav: BottomNavigationView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflater = inflater.inflate(R.layout.fragment_t3_registration, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Registration"
        setHasOptionsMenu(true)

        bottomNav = activity!!.findViewById(R.id.bottom_nav_view)



        inflater.button_to_register_food_story.setOnClickListener {
            findNavController().navigate(R.id.action_addStory_to_registerFoodStory, null)
            bottomNav.visibility=View.GONE
        }
        inflater.button_to_register_recipe.setOnClickListener {
            val id = findNavController().currentDestination!!.id
            when (id) {
                R.id.registerRecipe -> {
                    Log.i("tag", "addStory to~~")
                }
                R.id.addStory -> {
                    Log.i("tag","add story")
                }
                else -> {
                    Log.i("tag", "${findNavController().currentDestination!!.id}")
                }
            }
            findNavController().navigate(R.id.action_addStory_to_registerRecipe, null)
            bottomNav.visibility=View.GONE
        }
        return inflater
    }


    override fun onResume() {
        super.onResume()
        if(bottomNav.visibility== View.GONE)
            bottomNav.visibility=View.VISIBLE
        while(findNavController().currentDestination!!.id!=R.id.addStory)
            findNavController().popBackStack()

    }



}
