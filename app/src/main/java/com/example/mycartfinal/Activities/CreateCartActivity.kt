package com.example.mycartfinal.Activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.view.DragEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycartfinal.Model.*
import com.example.mycartfinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class CreateCartActivity : AppCompatActivity() {

    //bandera para comprobar si hay algún producto añadido
    private var flagCart = false

    //variable para el RecyclerView
    private lateinit var recyclerView: RecyclerView

    //variable para el RecyclerViewFinal
    private lateinit var recyclerViewFinal: RecyclerView

    //mi lista de productos
    lateinit var listProducts: ArrayList<Product>

    //adapter del rv
    lateinit var adapter: ProductAdapter

    //adapter del rvFinal
    private lateinit var adapterFinal: ProductAdapterFinal

    //conexion firebase
    private lateinit var db : FirebaseDatabase

    //usuario
    private lateinit var auth : FirebaseAuth

    private lateinit var btSave :ImageButton

    //variable para guardar los productos que va añadiendo al carrito
    private var productCart: HashMap<String, Int> = HashMap() //k: nombre product v: cantidad




    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_cart)

        //titulo del activity
        this.title = getString(R.string.it_create_cart)

        //recojo el usuario
        auth = FirebaseAuth.getInstance()

        //inicializo el rv
        recyclerView = findViewById(R.id.recyclerViewProducts)


        //lo pongo en el activity
        recyclerView.layoutManager = LinearLayoutManager(this)

        //inicializo el array de prodcutos
        listProducts = ArrayList()

        //creo el adapter
        adapter = ProductAdapter(listProducts)

        //vinculo el adapter con el rv
        recyclerView.adapter = adapter

        //boton guardar
        btSave = findViewById(R.id.btSaveCart)

        btSave.setOnClickListener {
            saveDefinitiveCart()
        }

        val linearCarrito: LinearLayout = findViewById(R.id.linearCarrito)

        //le asigno el dragListener a al linear Layout donde se soltarán los productos
        linearCarrito.setOnDragListener(dragListener())

        //conexion db
        db = FirebaseDatabase.getInstance()

        //muestro el listado de producto en el recycler view
        showRecycler()

        initCart()

    }

    private fun dragListener(): View.OnDragListener{

        //variable para la funcion de arrastrar
        val dragListener = View.OnDragListener { view, dragEvent ->

            when(dragEvent.action){
                //empieza a arrastrar
                DragEvent.ACTION_DRAG_STARTED ->{


                    dragEvent.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)

                }

                //entra a la posición final (arriba de la pantalla)
                DragEvent.ACTION_DRAG_ENTERED->{
                    view.invalidate()
                    true
                }


                DragEvent.ACTION_DRAG_LOCATION->true

                //sale de su posicion inicial
                DragEvent.ACTION_DRAG_EXITED->{
                    view.invalidate()
                    true
                }

                //cuando suelta el view
                DragEvent.ACTION_DROP->{


                    //recojo el clipdata del evento
                    val item = dragEvent.clipData.getItemAt(0)

                    val dragData = item.text.toString()

                    //compruebo si el artículo tiene más de 0

                    val cant = dragData.split(",")[0].toInt()
                    val name = dragData.split(",")[1]

                    if (cant>0){

                        //añado el producto y la cantidad a la posible compra
                        saveCart(dragData)

                        Toast.makeText(view.context, "$cant $name "+getString(R.string.prod_added), Toast.LENGTH_SHORT).show()

                        flagCart = true

                        true

                    }else {
                        Toast.makeText(view.context, R.string.error_quantity, Toast.LENGTH_SHORT)
                            .show()
                        false
                    }

                }

                //cuando termina de arrastar
                DragEvent.ACTION_DRAG_ENDED->{

                    view.invalidate()
                    true
                }

                else->false
            }

        }

        return dragListener
    }

    private fun saveDefinitiveCart() {

        //compruebo si hay cosas en la cesta
        if (flagCart){

            //creo la ventana de dialogo
            val builder = AlertDialog.Builder(this)

            //pongo el titulo
            builder.setTitle(getString(R.string.tittle_confirmCart))


            //inflo el layout del popup
            val view : LinearLayout= layoutInflater.inflate(R.layout.popup_layout,null) as LinearLayout

            //inicializo el rv final
            recyclerViewFinal = view.findViewById(R.id.recyclerViewFinal)

            //lo pongo en el popup
            recyclerViewFinal.layoutManager = LinearLayoutManager(view.context)

            showRecyclerFinal()

            //inicializo todos los componentes del popup
            val btSave : Button = view.findViewById(R.id.btSave)
            val btCancel: Button = view.findViewById(R.id.btCancel)
            val editTextPurName : EditText = view.findViewById(R.id.editTextPurName)

            builder.setView(view)

            //creo el alertDialog con el builder

            val alertDialog: AlertDialog = builder.create()

            alertDialog.show()

            //variable para guardar el nombre del carrito
            var purchaseName: String

            btSave.setOnClickListener {

                //si hay algo escrito en el nombre lo guardo
                if (!editTextPurName.text.isNullOrEmpty()){

                    purchaseName = editTextPurName.text.toString()
                    addCart(purchaseName)

                }else{

                    editTextPurName.error = getString(R.string.txt_cartName)

                }
            }

            btCancel.setOnClickListener {
                alertDialog.dismiss()
            }


        }else{
            Toast.makeText(this,R.string.txt_noProducts,Toast.LENGTH_SHORT).show()
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun showRecyclerFinal() {

        //array para los productos finales del carrito
        val listProductsFinal: ArrayList<Product> = ArrayList()

        val productCartAux = emptyProducts()
        var product: Product?

        //parseo el hashmap
        for (i in productCartAux){
            product = Product(i.key, i.value.toString())

            listProductsFinal.add(product)
        }

        //creo el adapter
        adapterFinal = ProductAdapterFinal(listProductsFinal)

        //vinculo el adapter con el rv
        recyclerViewFinal.adapter = adapterFinal

        adapterFinal.notifyDataSetChanged()
    }

    private fun addCart(purchaseName: String) {

        val purchase : Purchase?

        //me quedo con los productos añadidos
        val products : HashMap<String, Int> = emptyProducts()

        purchase = Purchase(products)

        //guardo el carrito en la base de datos purchase en las listas del usuario(uid) con clave purchasename
        FirebaseAuth.getInstance().currentUser?.let { it1 ->
            FirebaseDatabase.getInstance().getReference("Purchase")
                .child(it1.uid).child(purchaseName).setValue(purchase).addOnSuccessListener {

                    //mensaje de confirmacion
                    Toast.makeText(this, R.string.alert_Cart_Saved,Toast.LENGTH_LONG).show()

                    //vuelve a la pantalla de inicio
                    showHome()
                }.addOnFailureListener {

                    //mensaje de error
                    Toast.makeText(this,
                        R.string.alert_Cart_Failed,Toast.LENGTH_LONG).show()

                }
        }
    }

    private fun emptyProducts(): java.util.HashMap<String, Int> {

        //vacio la lista donde no tengan más de 0 productos

        val productCartAux : HashMap<String, Int> = HashMap()



        for (i in productCart){
            if (i.value != 0)
                productCartAux.put(i.key, i.value)
        }

        return productCartAux
    }

    private fun initCart() {
        productCart.clear()

        for (i in listProducts){
            i.cant?.let { i.name?.let { it1 -> productCart.put(it1, it.toInt()) } }

        }



    }



    private fun saveCart(dragData: String) {

        // cantidad del producto
        val cant = dragData.split(",")[0].toInt()
        //nombre del producto
        val prod = dragData.split(",")[1]

        productCart[prod] = cant

    }

    private fun showRecycler() {
        //limpio el array antes de llenarlo
        listProducts.clear()

        db.getReference("Products").addValueEventListener(object: ValueEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {


                snapshot.children.forEach {
                    val name = it.child("Name").value.toString()
                    val product = Product(name)


                    listProducts.add(product)


                }

                //inicializo el listCart
                initCart()

                adapter.notifyDataSetChanged()

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun showHome(){
        val homeIntent = Intent(this, HomeActivity::class.java)

        startActivity(homeIntent)
    }

}