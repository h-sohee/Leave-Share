package kr.ac.kumoh.s20171278.map_map_challenge.ui.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.FirebaseFirestore
import kr.ac.kumoh.s20171278.map_map_challenge.R
import kr.ac.kumoh.s20171278.map_map_challenge.SelectImageActivity
import kr.ac.kumoh.s20171278.map_map_challenge.SelectImageActivity.Companion.KEY_ALBUM_NAME
import kr.ac.kumoh.s20171278.map_map_challenge.album.main.AlbumDetailMemoActivity
import kr.ac.kumoh.s20171278.map_map_challenge.album.main.AlbumListActivity
import kr.ac.kumoh.s20171278.map_map_challenge.main.DetailMemoActivity


class SharedMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    lateinit var mView: MapView
    //googleMap을 MainMapFragment 안에 MapView로 받아와서 띄움
    private var albumData: ArrayList<SelectImageActivity.dbSite> = arrayListOf()
    private var locaArray: ArrayList<LatLng> = arrayListOf()
    val db = FirebaseFirestore.getInstance()
    private lateinit var albumName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        albumName = activity?.intent!!.getStringExtra(KEY_ALBUM_NAME)
        albumData = activity?.intent!!.getParcelableArrayListExtra(AlbumListActivity.ALBUM_DATA)

        for (i in 0 until albumData.size){
            locaArray.add(LatLng(albumData[i].lati, albumData[i].long))
        }
        var rootView = inflater.inflate(R.layout.shared_fragment_shared_map, container, false)
        mView = rootView.findViewById(R.id.fragment_select_mv) as MapView
        mView.onCreate(savedInstanceState)
        mView.onResume()
        mView.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val marker: MarkerOptions = MarkerOptions()
        var polyLineOptions: PolylineOptions = PolylineOptions()

        for (i in 0 until locaArray.size){
            // 마커표시

            marker.position(locaArray[i])
            googleMap.addMarker(marker)

            val boundsBuilder: LatLngBounds.Builder = LatLngBounds.builder()
            for (i in 0 until locaArray.size)
                boundsBuilder.include(locaArray[i])
            var bounds: LatLngBounds = boundsBuilder.build()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200))
        }
        // 경로표시
        polyLineOptions.width(5f)
            .color(Color.RED)
        polyLineOptions.addAll(locaArray)
        googleMap.addPolyline(polyLineOptions)

        googleMap.setOnMarkerClickListener(this)
    }

    fun Context.getBitmapFromVectorDrawable(drawableId: Int): Bitmap? {
        var drawable = ContextCompat.getDrawable(this, drawableId) ?: return null

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable).mutate()
        }

        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888) ?: return null

        return bitmap
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        // 팝업
        val intent = Intent(context, AlbumDetailMemoActivity::class.java)
        intent.putExtra(SelectImageActivity.KEY_ALBUM_NAME, albumName)
        intent.putExtra(AlbumListActivity.ALBUM_DATA, albumData)
        intent.putExtra(DetailMemoActivity.MARKER, marker?.position)
        startActivity(intent)

        return true
    }

    override fun onStart() {
        mView.onStart()
        super.onStart()
    }

    override fun onResume() {
        mView.onResume()
        super.onResume()
    }

    override fun onPause() {
        mView.onPause()
        super.onPause()
    }

    override fun onStop() {
        mView.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mView.onLowMemory()
        super.onLowMemory()
    }
}
