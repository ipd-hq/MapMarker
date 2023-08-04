package id.ipd.mapipd

import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.ipd.mapipd.databinding.ActivityMapIpdBinding

open class MAPIPDActivity(): AppCompatActivity() {

    private lateinit var listLocation: List<Location>

    private val binding by lazy { ActivityMapIpdBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun initData(listLocation: List<Location>){
        this.listLocation = listLocation
        initView()
    }

    fun initView() {

    }
}