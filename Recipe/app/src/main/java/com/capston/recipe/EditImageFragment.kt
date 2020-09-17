package com.capston.recipe


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import com.capston.recipe.interfaces.EditImageFragmentListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class EditImageFragment : BottomSheetDialogFragment(), SeekBar.OnSeekBarChangeListener {
    private var listener: EditImageFragmentListener?=null
    var seekBar_brightness: SeekBar? = null
    var seekBar_saturation: SeekBar? = null
    var seekBar_contrast: SeekBar? = null

    companion object{
        internal var instance: EditImageFragment?=null
        fun getInstance(): EditImageFragment {
            if(instance ==null)
                instance = EditImageFragment()
            return instance!!
        }
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        var progress = progress
        if(listener != null){
            when(seekBar!!.id){
                R.id.seekbar_brightness ->{
                    listener!!.onBrightnessChanged(progress-100)
                }
                R.id.seekbar_contrast ->{
                    progress +=10
                    val floatVal = .10f*progress
                    listener!!.onConstrantChanged(floatVal)
                }
                R.id.seekbar_saturation ->{
                    val floatVal = .10f*progress
                    listener!!.onSaturationChanged(floatVal)
                }
            }
        }
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        if(listener != null)
            listener!!.onEditStarted()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if(listener != null)
            listener!!.onEditCompleted()
    }


    fun setListener(listener: EditImageFragmentListener){
        this.listener=listener
    }
    fun resetControls(){
        if(seekBar_brightness !=null && seekBar_contrast != null && seekBar_saturation != null) {
            seekBar_brightness!!.progress = 100
            seekBar_contrast!!.progress = 0
            seekBar_saturation!!.progress = 10
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.fragment_t3_edit_image, container, false)
        seekBar_brightness = fragmentView.findViewById(R.id.seekbar_brightness)
        seekBar_saturation = fragmentView.findViewById(R.id.seekbar_saturation)
        seekBar_contrast = fragmentView.findViewById(R.id.seekbar_contrast)

        seekBar_brightness!!.max = 200
        seekBar_brightness!!.progress = 100

        seekBar_contrast!!.max = 20
        seekBar_contrast!!.progress = 0

        seekBar_saturation!!.max = 30
        seekBar_saturation!!.progress = 10

        seekBar_brightness!!.setOnSeekBarChangeListener(this)
        seekBar_contrast!!.setOnSeekBarChangeListener(this)
        seekBar_saturation!!.setOnSeekBarChangeListener(this)
        return fragmentView
    }

}
