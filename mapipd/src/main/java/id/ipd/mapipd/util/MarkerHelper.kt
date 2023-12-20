package id.ipd.mapipd.util

import com.google.android.gms.maps.model.*
import kotlin.collections.ArrayList



/**

Created by
name : Septiawan Aji Pradana
email : septiawanajipradana@gmail.com
website : dewakoding.com

 **/
object MarkerHelper {

    fun getRectangleCorner(centerLatLon: LatLng, distance: Double): MutableList<LatLng> {
        val cornerRectangle: MutableList<LatLng> = ArrayList()

        cornerRectangle.add(getDestinationPoint(centerLatLon, 45.toDouble(), distance))
        cornerRectangle.add(getDestinationPoint(centerLatLon, 135.toDouble(), distance))
        cornerRectangle.add(getDestinationPoint(centerLatLon, 225.toDouble(), distance))
        cornerRectangle.add(getDestinationPoint(centerLatLon, 315.toDouble(), distance))
        return cornerRectangle
    }

    private fun getDestinationPoint(source: LatLng,brng: Double, dist: Double): LatLng {
        val dist = dist / (6371 * 1000);
        val brng = Math.toRadians(brng);

        val lat1: Double = Math.toRadians(source.latitude)
        val lon1: Double = Math.toRadians(source.longitude)

        val lat2: Double = Math.asin(Math.sin(lat1) * Math.cos(dist) +
                Math.cos(lat1) * Math.sin(dist) * Math.cos(brng));
        val lon2: Double = lon1 + Math.atan2(Math.sin(brng) * Math.sin(dist) *
                Math.cos(lat1),
            Math.cos(dist) - Math.sin(lat1) *
                    Math.sin(lat2));

        val latLng = LatLng(Math.toDegrees(lat2), Math.toDegrees(lon2))
        return latLng
    }

}
