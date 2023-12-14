package com.example.mycartfinal.Model

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.mycartfinal.R

class ProductAdapterFinal(productos: List<Product>) :
    RecyclerView.Adapter<ProductAdapterFinal.Companion.productsViewHolder>() {

    var listProducts: List<Product> = productos



    companion object{
          class productsViewHolder(val itemView: View) : RecyclerView.ViewHolder(itemView){

              var txtViewName: TextView = itemView.findViewById(R.id.txtProductFinal) as TextView
              var txtViewCant: TextView = itemView.findViewById(R.id.textCantProductFinal) as TextView

         }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): productsViewHolder {

        val vista = LayoutInflater.from(parent.context).inflate(R.layout.product_final,parent,false)
        val productsViewHolder = productsViewHolder(vista)

        return productsViewHolder

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