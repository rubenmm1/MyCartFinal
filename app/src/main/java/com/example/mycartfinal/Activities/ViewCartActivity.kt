package com.example.mycartfinal.Activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycartfinal.Model.*
import com.example.mycartfinal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ViewCartActivity : AppCompatActivity() {

    //variable para el RecyclerView
    private lateinit var recyclerView: RecyclerView
    //adapter del rv
    lateinit var adapter: CartAdapter

    //mi lista de compras
    var listPurchase: ArrayList<PurchaseName> = ArrayList()

    //conexion firebase
    private lateinit var db : FirebaseDatabase

    //usuario
    private lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_cart)

        //titulo del activity
        this.title = getString(R.string.it_my_carts)

        //recojo el usuario
        auth = FirebaseAuth.getInstance()

        //conexion db
        db = FirebaseDatabase.getInstance()

        //inicializo el rv
        recyclerView = findViewById(R.id.rvViewCartPurchase)

        //lo pongo en el activity
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)


        //creo el adapter
        adapter = CartAdapter(listPurchase)

        //vinculo el adapter con el rv
        recyclerView.adapter = adapter

        //inflo el layout de view cart
        val view : CardView = layoutInflater.inflate(R.layout.view_cart,null) as CardView

        val btViewProduct = view.findViewById<ImageButton>(R.id.btSpandProducts)

        btViewProduct.setOnClickListener {

        }

        showRecycler()
    }

    private fun showRecycler() {
        //limpio el array antes de llenarlo


        //nombre del carrito
        var name: String

        //objeto PurchaseName
        var purchase : PurchaseName



        auth.uid?.let {
            db.getReference("Purchase").child(it).addValueEventListener(object: ValueEventListener {
                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    listPurchase.clear()

                        //recorro los nombres de los carritos
                        snapshot.children.forEach { it ->
                            name = it.key.toString()


                            purchase = PurchaseName(name)

                            listPurchase.add(purchase)
                        }

                    adapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }




    }




}