package id.ipd.mapipd.model

import com.google.android.gms.maps.model.LatLng

data class ItemLoc(
    val id : String,
    val position : LatLng,
    val title : String?,
    val desription : String?
)
