package com.example.mycartfinal.Activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.mycartfinal.R
import com.example.mycartfinal.Model.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import androidx.lifecycle.lifecycleScope
import com.example.mycartfinal.Model.GooglePlaceModel
import com.example.mycartfinal.Model.GoogleResponseModel
import com.example.mycartfinal.Utility.State
import com.example.mycartfinal.ViewModel.LocationViewModel
import kotlin.reflect.KProperty


class HomeActivity : AppCompatActivity(), OnMapReadyCallback{

    //variables
    private lateinit var toogle : ActionBarDrawerToggle

    private var user: User? = null

    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var dbReference: DatabaseReference

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var  googlePlaceList: ArrayList<GooglePlaceModel>

    private var locationViewModel: LocationViewModel by viewModels<LocationViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        //creo el fragment de google maps
        createFragment()

        //autenticacion
        auth = FirebaseAuth.getInstance()

        //conexion base de datos
        database = FirebaseDatabase.getInstance()
        dbReference = auth.currentUser?.uid?.let { database.getReference("Users").child(it) }!!



        //inicializo el arrayList
        googlePlaceList = ArrayList()

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)

        //Boton para desplegar el menu
        toogle = ActionBarDrawerToggle(this,drawerLayout , R.string.open_menu, R.string.close_menu)

        //asigno el boton al layout
        drawerLayout.addDrawerListener(toogle)
        toogle.syncState()


        //para cambiar el aspecto del boton una vez pulsado
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        //posicion actual
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        //le doy acciones a los items del menú
        setMenuListener()

        //setup del header del menu
        setup()

    }

    private fun setMenuListener() {
        val navView = findViewById<NavigationView>(R.id.navView)


        //funcion para asignar un listener a cada item del menu
        navView.setNavigationItemSelectedListener {

            when(it.itemId){

                R.id.it_create_cart -> showCreate()
                R.id.it_my_carts -> showViewCart()
                R.id.it_logout -> logOut()

                R.id.it_edit_profile -> editProfile()
                /*
                R.id.it_rate -> //accion que debe hacer al clicar el item
                R.id.it_share -> //accion que debe hacer al clicar el item
                */
            }

            true
        }
    }

    private fun editProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)

        startActivity(intent)
    }

    private fun logOut() {

        //cierro la sesión del usuario
        auth.signOut()

        //redirijo a la pantalla de inicio
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun showCreate() {
        val intent = Intent(this, CreateCartActivity::class.java)

        startActivity(intent)
    }

    private fun showViewCart() {
        val intent = Intent(this, ViewCartActivity::class.java)

        startActivity(intent)
    }

    companion object{
        const val PERMISSION_REQUEST_ACCESS_LOCATION = 100

    }

    private fun getLocation(){

        if (checkPermission()){


            if (isLocationEnabled()){

                if(ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED)
                {

                    requestPermission()
                    return

                }

                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {task->
                    val location: Location? = task.result

                    if (location!=null){

                        //actualizo localizacion
                        updateLocation(location.latitude, location.longitude)

                    }else{
                        Toast.makeText(this, "NULL",Toast.LENGTH_SHORT).show()
                    }
                }


            }else{
                Toast.makeText(this, "Turn on location",Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        }else{

            requestPermission()
        }

    }



    private fun updateLocation(latitude: Double, longitude: Double) {

        //muevo la camara a la posicion del usuario
        val latlon = LatLng(latitude,longitude)

        val cameraPosition = CameraPosition.builder().target(latlon).zoom(15F).build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

        //verifico que el usuario ha permitido acceso a su ubicacion y activo el botón CurrentPosition del mapa
        if (checkPermission())
            googleMap.isMyLocationEnabled = true

        //busco los supermercados cercanos al usuario

        val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json" +
                "?location="+latitude+","+longitude+ //posicion actual
                "&radius=1000"+//radio de busqueda
                "&type=supermarket" + //lo que voy a buscar
                "&sensor=true"+ //sensor
                "&key=AIzaSyA0mQV2bXiX_u-pocc58kXsdkSkY380UAw"//clave de api de google map


        //Muestro en el mapa los supermercados cercanos
        getNerbyPlaces(url)

    }

    private fun getNerbyPlaces(url: String){

        lifecycleScope.launchWhenCreated {
            locationViewModel.getNearByPlace(url).collect{
                when(it){


                    is State.Success<*> ->{
                        val googleResponseModel: GoogleResponseModel =
                            it.data as GoogleResponseModel

                        if(googleResponseModel.googlePlaceModelList!= null &&
                                googleResponseModel.googlePlaceModelList.isNotEmpty()){

                            //vacio la lista
                            googlePlaceList.clear()
                            //vacio el mapa
                            googleMap.clear()

                            //recorro la lista de los resultados y los añado a mi lista para ponerlos en el mapa como marcadores
                            for (i in googleResponseModel.googlePlaceModelList.indices){
                                googlePlaceList.add(googleResponseModel.googlePlaceModelList[i])
                                addMarker(googleResponseModel.googlePlaceModelList[i],i)


                            }

                        }else{
                            googleMap.clear()
                            googlePlaceList.clear()
                        }
                    }

                    else -> {}
                }
            }
        }

    }

    private fun addMarker(googlePlaceModel: GooglePlaceModel,position: Int) {
        val markerOptions: MarkerOptions = MarkerOptions().position(
            LatLng(
                googlePlaceModel.geometry?.location?.lat!!,
                googlePlaceModel.geometry.location.lng!!
            )
        ).title(googlePlaceModel.name).snippet(
            googlePlaceModel.vicinity)

        googleMap.addMarker(markerOptions)?.tag = position


    }

    private fun isLocationEnabled():Boolean{

        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.
        isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode== PERMISSION_REQUEST_ACCESS_LOCATION)
            if (grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                getLocation()

    }

    private fun requestPermission(){
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    private fun checkPermission():Boolean{

        if(ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED){

            return false
        }

        return true

    }

    private fun setup(){

        //Pongo el email del usuario en la cabecera del menú
        val navView = findViewById<NavigationView>(R.id.navView)

        val menu = navView.getHeaderView(0)

        val emailTextView = menu.findViewById<TextView>(R.id.user_email)
        val nameTextView = menu.findViewById<TextView>(R.id.user_name)


        //Leo el objeto de la base de datos Users
        dbReference.addValueEventListener(object: ValueEventListener {


            override fun onDataChange(snapshot: DataSnapshot) {

                user = snapshot.getValue(User::class.java)


                //Pongo el nombre más los apellidos del usuario en la cabecera del menu
                emailTextView.setText(auth.currentUser?.email)
                nameTextView.setText(user?.name+" "+user?.surname)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    //funcion que devuelve el item del menu seleccionado
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toogle.onOptionsItemSelected(item)){

            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onMapReady(map: GoogleMap) {

        googleMap = map

        //recojo posicion del usuario
        getLocation()



    }

    private fun createFragment(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }



}

private operator fun Any.setValue(homeActivity: HomeActivity, property: KProperty<*>, locationViewModel: LocationViewModel) {

}

