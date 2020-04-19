package com.example.calculadoraDeCerveja

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BeerItemAdapter(private val context: Context, private val beerList: List<BeerItemModel>) :RecyclerView.Adapter<BeerItemAdapter.ViewHolder>(){
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var beerMarca : TextView = view.findViewById(R.id.marca)
        var beerValor : TextView = view.findViewById(R.id.valor)
        var beerTamanho : TextView = view.findViewById(R.id.tamanho)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.beer_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return beerList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val beerItem = beerList[position]

        holder.beerMarca.setText("Marca: "+beerItem.beerMarca)
        holder.beerValor.setText("R$ "+beerItem.beerValor.toString())
        holder.beerTamanho.setText("ML "+beerItem.beerTamanho.toString())
    }
}