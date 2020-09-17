package com.capston.recipe.interfaces

import com.zomato.photofilters.imageprocessors.Filter

interface FilterListFragmentListener {
    fun onFilterSelected(filter: Filter)
}