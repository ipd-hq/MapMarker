package id.ipd.mapapp

import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.maps.model.LatLng
import id.ipd.mapipd.model.ItemLoc
import id.ipd.mapipd.ui.MAPIPDActivity
import id.ipd.mapipd.ui.MapIpdCurrentLocationActivity

class MainActivity: MapIpdCurrentLocationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init(
            null,
            null
        )
    }

}