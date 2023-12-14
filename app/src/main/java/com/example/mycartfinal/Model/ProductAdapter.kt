package com.example.mycartfinal.Model

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mycartfinal.R

class ProductAdapter(products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.Companion.productsViewHolder>() {

    private var listProducts: List<Product> = products


    companion object{
          class productsViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView){

              var txtViewName: TextView = itemView.findViewById(R.id.txtProduct) as TextView
              var txtViewCant: TextView = itemView.findViewById(R.id.textCantProduct) as TextView
              var cantArt:Int = 0

              var btMinusCant2: Button = itemView.findViewById(R.id.btMinusProduct)

              //funcionalidad botones
              var btMinusCant = itemView.findViewById<Button>(R.id.btMinusProduct).setOnClickListener {

                  if (cantArt!=0){

                      cantArt--

                      txtViewCant.text = cantArt.toString()
                  }

                  if (cantArt==0)
                      it.isEnabled=false
              }

              var btPlusCant = itemView.findViewById<Button>(R.id.btPlusProduct).setOnClickListener {

                  if (cantArt==0)
                      btMinusCant2.isEnabled=true

                  cantArt++

                  txtViewCant.text = cantArt.toString()
              }

              //drag and drop para articulos
              @SuppressLint("ResourceAsColor")
              var cardView  = itemView.findViewById<CardView>(R.id.cardView).setOnLongClickListener {

                  //clipboard
                  val clipText = txtViewCant.text.toString() + "," + txtViewName.text.toString()

                  val item = ClipData.Item(clipText)
                  val mimeTypes = arrayOf(ClipDescription.MIMETYPE_TEXT_PLAIN)
                  val data = ClipData(clipText,mimeTypes,item)

                  val dragShadowBuilder = View.DragShadowBuilder(it)

                  //asigno la acción de arrastrar y soltar a la vista
                  it.startDragAndDrop(data,dragShadowBuilder,it,0)



                  Companion

                  true


              }

         }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): productsViewHolder {

        val vista = LayoutInflater.from(parent.context).inflate(R.layout.product, parent, false)

        return productsViewHolder(vista)

    }

    override fun onBindViewHolder(holder: productsViewHolder, position: Int) {
        val product: Product = listProducts.get(position)

        holder.txtViewName.text = product.name
        holder.txtViewCant.text = ""+product.cant


    }

    override fun getItemCount(): Int {
        return listProducts.size
    }
}