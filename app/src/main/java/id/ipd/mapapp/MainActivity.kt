package id.ipd.mapapp

import android.os.Bundle
import com.google.android.gms.maps.model.LatLng
import id.ipd.mapipd.model.ItemLoc
import id.ipd.mapipd.ui.MAPIPDActivity

class MainActivity: MAPIPDActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val list = ArrayList<ItemLoc>()
        list.add(
            ItemLoc(
                id = "1",
                position = LatLng(-6.1675702, 106.838328),
                title = "Title 1",
                desription = "Description 1"
            )
        )
        list.add(
            ItemLoc(
                id = "2",
                position = LatLng(-6.1686664, 106.8388735),
                title = "Title 2",
                desription = "Description 2"
            )
        )
        list.add(
            ItemLoc(
                id = "3",
                position = LatLng(-6.192553, 106.8510131),
                title = "Title 3",
                desription = "Description 3"
            )
        )

        initData(list)
    }

}