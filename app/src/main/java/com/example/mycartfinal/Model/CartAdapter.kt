package com.example.mycartfinal.Model

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.graphics.BlendMode
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycartfinal.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartAdapter(purchase: List<PurchaseName>) :
    RecyclerView.Adapter<CartAdapter.Companion.CartViewHolder>() {

    //lista de carritos
    private var listPurchase: List<PurchaseName> = purchase

    //conexion firebase
    private var db: FirebaseDatabase = FirebaseDatabase.getInstance()

    //usuario
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    //variable para el RecyclerViewPupUp
    private lateinit var recyclerViewPupUp: RecyclerView

    //adapter del rvPopUp
    private lateinit var adapterPupup: ProductAdapterFinal

    //lista de productos
    private var products: ArrayList<Product> = ArrayList()


    companion object {
        class CartViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView){

            //titulo carrito
            var txtPurchaseName: TextView = itemView.findViewById(R.id.txtProductView)


            //imageButton para ver los productos del carrito
            var btProducts = itemView.findViewById<ImageButton>(R.id.btSpandProducts)

            //imageButton para borrar el carrito
            var btDelete : ImageButton = itemView.findViewById(R.id.btDeleteCart)

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {

        val vista = LayoutInflater.from(parent.context).inflate(R.layout.view_cart, parent, false)

        return CartViewHolder(vista)
    }


    override fun getItemCount(): Int {
        return listPurchase.size
    }


    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        //recojo el objeto según la posicion del rv
        val purchase: PurchaseName = listPurchase[position]

        val name = purchase.name

        //pongo el titulo del carrito en el textView
        holder.txtPurchaseName.text = name

        holder.btProducts.setOnClickListener { v ->

            showCart(v, name)

        }


        holder.btDelete.setOnClickListener {
            deleteCart(it, name)
        }

    }

    private fun deleteCart(v: View?, name: String?) {

        //inflo el layout del popup
        val view :LinearLayout = LayoutInflater.from(v?.rootView?.context).inflate(R.layout.popup_layout,null) as LinearLayout

        //recojo el boton de guardar para cambiar su texto del popup
        val btDelete : Button = view.findViewById<Button>(R.id.btSave)

        btDelete.text = "Borrar"

        //recojo el boton de cancelar para asignarle listener
        val btCancel: Button = view.findViewById<Button>(R.id.btCancel)

        //recojo el EditText para quitarlo del popup
        val editText : EditText = view.findViewById(R.id.editTextPurName)

        editText.visibility = EditText.GONE


        //inicializo el rv del popup
        recyclerViewPupUp = view.findViewById(R.id.recyclerViewFinal)

        //lo pongo en el popup
        recyclerViewPupUp.layoutManager = LinearLayoutManager(view.context)

        //creo la ventana de dialogo
        val builder = AlertDialog.Builder(v?.rootView?.context)

        //pongo el titulo
        builder.setTitle("Seguro que quiere borrar el carrito $name?")

        //añado el popup al alertDialog
        builder.setView(view)

        builder.setCancelable(true)

        //creo el alertDialog con el builder
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

        btDelete.setOnClickListener{

            confirmDeleteCart(name,it)


            alertDialog.dismiss()
        }


        btCancel.setOnClickListener {

            alertDialog.dismiss()

        }

        //cargo todos los datos
        showRecyclerPopUp(name)

    }

    private fun confirmDeleteCart(name: String?, view : View) {

        var result : Task<Void>? = null

        auth.uid?.let { uid ->
            name?.let { name ->

                db.getReference("Purchase").child(uid).child(name).addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {

                        //borro el carrito "name"
                        for (i in snapshot.children)
                            result = i.ref.removeValue()

                        if (result?.isSuccessful == true){

                            Toast.makeText(view.rootView.context, "Carrito $name borrado con exito",Toast.LENGTH_SHORT)

                        }else{
                            Toast.makeText(view.rootView.context, "Error al borrar el carrito $name",Toast.LENGTH_SHORT)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })


            }
        }
    }


    private fun showCart(v : View, name : String?){
        //inflo el layout del popup
        val view :LinearLayout = LayoutInflater.from(v.rootView?.context).inflate(R.layout.popup_layout,null) as LinearLayout

        //recojo el boton de guardar para borrarlo del popup
        val btSave : Button = view.findViewById<Button>(R.id.btSave)

        btSave.visibility = Button.GONE

        //recojo el boton de cancelar para asignarle listener
        val btCancel: Button = view.findViewById<Button>(R.id.btCancel)

        //recojo el EditText para quitarlo del popup
        val editText : EditText = view.findViewById(R.id.editTextPurName)

        editText.visibility = EditText.GONE


        //inicializo el rv del popup
        recyclerViewPupUp = view.findViewById(R.id.recyclerViewFinal)

        //lo pongo en el popup
        recyclerViewPupUp.layoutManager = LinearLayoutManager(view.context)

        //creo la ventana de dialogo
        val builder = AlertDialog.Builder(v.rootView?.context)

        //pongo el titulo
        builder.setTitle("Carrito $name")

        //añado el popup al alertDialog
        builder.setView(view)

        builder.setCancelable(true)

        //creo el alertDialog con el builder
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()


        btCancel.setOnClickListener {

            alertDialog.dismiss()

        }

        //cargo todos los datos
        showRecyclerPopUp(name)
    }

    private fun showRecyclerPopUp(name: String?) {




        //objeto producto
        var product: Product

        //recojo todos los productos del carrito name
        auth.uid?.let { uid ->
            if (name != null) {
                db.getReference("Purchase").child(uid).child(name).child("products")
                    .addValueEventListener(object : ValueEventListener {
                        @SuppressLint("NotifyDataSetChanged")
                        override fun onDataChange(snapshot: DataSnapshot) {

                            products.clear()

                            snapshot.children.forEach {
                                product = Product(it.key, it.value.toString())


                                products.add(product)
                            }

                            //creo el adapter
                            adapterPupup = ProductAdapterFinal(products)

                            //vinculo el adapter con el rv
                            recyclerViewPupUp.adapter = adapterPupup

                            adapterPupup.notifyDataSetChanged()

                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
            }


        }

    }


}
