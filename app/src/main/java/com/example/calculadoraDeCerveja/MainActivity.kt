package com.example.calculadoraDeCerveja

import android.graphics.Color
import android.graphics.Color.WHITE
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.beer_dialog.*


class MainActivity : AppCompatActivity() {

    private var coordinatorLayout: CoordinatorLayout? = null
    private var recyclerView: RecyclerView? = null
    private var db: DBHelper? = null
    private var beersList = ArrayList<BeerItemModel>()
    private var adapter : BeerItemAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        controle()
    }

    private fun controle() {
        coordinatorLayout = findViewById(R.id.layout_main)
        recyclerView = findViewById(R.id.recycler_main)
        db = DBHelper(this)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener {
            showDialog(false, null, -1)
        }

        beersList.addAll(db!!.itensList)
        adapter = BeerItemAdapter(this, beersList)

        val layoutManager = LinearLayoutManager(applicationContext)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.itemAnimator = DefaultItemAnimator()
        recyclerView!!.adapter = adapter
    }

    private fun showDialog(isUpdate: Boolean, nothing: Nothing?, position: Int) {
        val layoutInflaterAndroid = LayoutInflater.from(applicationContext)
        val view = layoutInflaterAndroid.inflate(R.layout.beer_dialog, null)

        val userInput = AlertDialog.Builder(this@MainActivity)
        userInput.setView(view)

        val titulo = view.findViewById<TextView>(R.id.titulo)
        val marca = view.findViewById<EditText>(R.id.editTextMarca);
        val valor = view.findViewById<EditText>(R.id.editTextValor);
        val tamanho = view.findViewById<EditText>(R.id.editTextMedida);

        titulo.text = if (!isUpdate) getString(R.string.adiciona) else getString(R.string.editar)

        //Evita clicarmos fora e fechar o dialog
        userInput.setCancelable(false)
            .setPositiveButton(if(isUpdate) getString(R.string.atualizar) else getString(R.string.salvar)) {dialogBox, id->}
            .setNegativeButton(getString(R.string.cancelar)){dialogBox, id->dialogBox.cancel()}

        val alertDialog = userInput.create()
        alertDialog.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(View.OnClickListener {
            if (TextUtils.isEmpty(marca.text.toString())){
                Toast.makeText(this@MainActivity, getString(R.string.toastMarca), Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else {
                alertDialog.dismiss()
            }

            var beer = Beer(null, marca.text.toString(), valor.text.toString().toDouble(), tamanho.text.toString().toDouble())
            createBeerItem(beer)

        })
    }

    private fun createBeerItem(beer:Beer) {
        val item = db!!.insertBeerItem(beer)
        val novoItem = db!!.getBeerItem(item)

        if (novoItem != null) {
            beersList.add(0, novoItem)
            adapter!!.notifyDataSetChanged()
        }
    }


    private fun setSupportActionBar(toolbar: Toolbar) {

    }


}
