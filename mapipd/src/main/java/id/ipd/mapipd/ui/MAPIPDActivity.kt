package id.ipd.mapipd.ui

import android.Manifest.permission
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import id.ipd.mapipd.R
import id.ipd.mapipd.databinding.ActivityMapIpdBinding
import id.ipd.mapipd.model.ItemLoc


open class MAPIPDActivity : AppCompatActivity(),
    OnMapReadyCallback,
    OnMarkerClickListener,
    OnMapClickListener
{

    private val REQUEST_CODE = 3300

    private lateinit var listLocation: List<ItemLoc>
    private var data : HashMap<Marker, ItemLoc> = hashMapOf()
    private var markerIcon : Bitmap? = null
    private lateinit var userIcon : Bitmap
    private var textButton : String? = null
    private var listenerButton : ((String)->Unit)? = null

    private val binding by lazy { ActivityMapIpdBinding.inflate(layoutInflater) }
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment : SupportMapFragment
    private var mMarker: Marker? = null
    private var mPopupWindow: PopupWindow? = null
    private var mWidth = 0
    private var mHeight = 0

    private var mLastLocation: Location? = null
    private var mCurrLocationMarker: Marker? = null
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mLocationRequest: LocationRequest
    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                val location = locationList.last()
                mLastLocation = location
                if (mCurrLocationMarker != null) {
                    mCurrLocationMarker?.remove()
                }

                val latLng = LatLng(location.latitude, location.longitude)
                val markerOptions = MarkerOptions()
                markerOptions.position(latLng)
                markerOptions.title("Current Position")
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(userIcon))
                mCurrLocationMarker = mGoogleMap.addMarker(markerOptions)

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun initData(
        listLocation: List<ItemLoc>,
        markerIcon : Bitmap? = null,
        userIcon : Bitmap = (getDrawable(R.drawable.ic_user) as BitmapDrawable).bitmap,
        textButton : String? = null,
        listenerButton : ((String)->Unit)? = null
    ) {
        this.listLocation = listLocation
        this.markerIcon = if(markerIcon != null) convertBitmapSize(markerIcon) else null
        this.userIcon = convertBitmapSize(userIcon)
        this.textButton = textButton
        this.listenerButton = listenerButton

        initView()
    }

    private fun initView(){

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    fun convertBitmapSize(bitmap : Bitmap) : Bitmap{
        val height = 100
        val width = 100
        return Bitmap.createScaledBitmap(bitmap, width, height, false)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap.uiSettings.isZoomControlsEnabled = true
        mGoogleMap.setPadding(10, 10, 10, 10)


        listLocation.forEach{
            var markerOption = MarkerOptions()
                    .position(it.position)
                    .title(it.title)

            if(markerIcon != null) markerOption.icon(BitmapDescriptorFactory.fromBitmap(markerIcon!!))
            var marker = mGoogleMap.addMarker(markerOption)
            data.put(marker!!, it)
        }

        mLocationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
                mGoogleMap.isMyLocationEnabled = true
            } else {
                checkLocationPermission()
            }
        } else {
            mFusedLocationClient?.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
            mGoogleMap.isMyLocationEnabled = true
        }

        mGoogleMap.setOnMarkerClickListener(this)
        mGoogleMap.setOnMapClickListener(this)
    }

    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission Needed")
                    .setMessage("This app needs the Location permission, please accept to use location functionality")
                    .setPositiveButton(
                        "OK"
                    ) { _, _ ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(permission.ACCESS_FINE_LOCATION),
                            REQUEST_CODE
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission.ACCESS_FINE_LOCATION),
                    REQUEST_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        mFusedLocationClient?.requestLocationUpdates(
                            mLocationRequest,
                            mLocationCallback,
                            Looper.myLooper()
                        )
                        mGoogleMap.setMyLocationEnabled(true)
                    }

                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show()
                }
                return
            }
        }
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        showDialog(p0)
        return true
//
    }

    override fun onMapClick(p0: LatLng) {
        hideDialog()
    }

    fun showDialog(marker : Marker){

        val datum = data.get(marker)
        datum?.let{

            datum?.title?.let {
                binding.titleTv.visibility = View.VISIBLE
                binding.titleTv.text = datum?.title
            }?:run{
                binding.titleTv.visibility = View.GONE
            }

            datum?.desription?.let{
                binding.descriptionTv.visibility = View.VISIBLE
                binding.descriptionTv.text = datum?.desription
            }?:run{
                binding.descriptionTv.visibility = View.GONE
            }

            binding.directionB.setOnClickListener {
                try{
                    val uri = Uri.parse("google.navigation:q=${datum.position.latitude},${datum.position.longitude}")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    startActivity(mapIntent)
                } catch (exexption: java.lang.Exception){
                    Toast.makeText(this, "Aplikasi Google Maps belum terpasang. Harap install aplikasi terlebih dahulu", Toast.LENGTH_LONG).show()
                }
            }

            if(
                textButton != null  &&
                listenerButton != null
            ){
                binding.actionB.visibility = View.VISIBLE
                binding.actionB.text = textButton
                binding.actionB.setOnClickListener {
                    listenerButton?.invoke(datum.id)
                }
            }

            binding.dialogCv.setVisibility(View.VISIBLE)
            binding.dialogCv.setAlpha(0.0f);
            binding.dialogCv.translationY = binding.dialogCv.height.toFloat()

            binding.dialogCv.animate()
                .translationY(0.0f)
                .alpha(1.0f)
                .setListener(null);
        }

    }

    fun hideDialog(){
        binding.dialogCv.animate()
            .translationY(binding.dialogCv.getHeight().toFloat())
            .alpha(0.0f)
            .setDuration(300)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    binding.dialogCv.setVisibility(View.GONE)
                }
            })
    }

}