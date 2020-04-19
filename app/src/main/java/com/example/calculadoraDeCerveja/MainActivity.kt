package com.example.calculadoraDeCerveja

import android.graphics.Color
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
import kotlinx.android.synthetic.main.activity_main.*


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
        calculoCerveja(beersList)

    }

    // Controla o que vai aparecer
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

        //Funcao de clique longo
        recyclerView!!.addOnItemTouchListener(
            ItemLongPressListener(
                this,
                recyclerView!!,
                object : ItemLongPressListener.ClickListener {
                    override fun onClick(view: View, position: Int) {}

                    override fun onLongClick(view: View?, position: Int) {
                        showActionsDialog(position)
                    }

                }
        ))
        calculoCerveja(beersList)


    }

    // Mostra as ações ao ter um clique longo
    private fun showActionsDialog(position: Int) {
        val options = arrayOf<CharSequence>(getString(R.string.editar),
        getString(R.string.excluir), getString(R.string.excluirTudo))
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.tituloOpcao))
        builder.setItems(options){
            dialog, itemIndex ->
                when(itemIndex) {
                    0 -> showDialog(true, beersList[position], position)
                    1 -> deleteBeerItem(position)
                    2 -> deleteAllItens()
                    else -> Toast.makeText(applicationContext, getString(R.string.toastError), Toast.LENGTH_SHORT).show()
                }
        }
        builder.show()

    }

    // Delete uma cerveja
    private fun deleteBeerItem(position: Int) {
        db!!.deleteBeerItem(beersList[position])
        beersList.removeAt(position)
        adapter!!.notifyItemRemoved(position)
        calculoCerveja(beersList)
    }

    // Deleta todas as cervejas
    private fun deleteAllItens() {
        db!!.deleteAllBeer()
        beersList.removeAll(beersList)
        adapter!!.notifyDataSetChanged()
        calculoCerveja(beersList)
    }

    // Funcao que cria o dialog para inserir uma beer
    private fun showDialog(isUpdate: Boolean, beerItemModel: BeerItemModel?, position: Int) {
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
            } else if (TextUtils.isEmpty(valor.text.toString())) {
                Toast.makeText(this@MainActivity, "O valor não pode ser vazio", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            } else if (TextUtils.isEmpty(tamanho.text.toString())) {
                Toast.makeText(this@MainActivity, "O tamanho não pode ser vazio", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }else {
                alertDialog.dismiss()
            }

            var beer = Beer(null, marca.text.toString(), valor.text.toString().toDouble(), tamanho.text.toString().toInt())
            createBeerItem(beer)

        })

        calculoCerveja(beersList)
    }

    // Função que cria um Beer Item
    private fun createBeerItem(beer:Beer) {

        val item = db!!.insertBeerItem(beer)
        val novoItem = db!!.getBeerItem(item)

        if (novoItem != null) {
            beersList.add(0, novoItem)
            adapter!!.notifyDataSetChanged()
        }

    }

    // Lógica de calculo da cerveja mais em conta
    private fun calculoCerveja(beerList: ArrayList<BeerItemModel>) {
        var melhorPreco = 999.0

        if (beersList.size == 0) {
            marcaEmConta.text = ""
            precoEmConta.text = ""
            tamanhoEmConta.text = ""
            diferenca.text = ""
        }

        for( beer in beerList) {
            val mediaPreco = (beer.beerValor/beer.beerTamanho) * 1000
            melhorPreco = mediaPreco
            marcaEmConta.text = "Marca: " + beer.beerMarca
            precoEmConta.text = "Preço: R$ " + beer.beerValor.toString()
            tamanhoEmConta.text = "Tamanho: " + beer.beerTamanho.toString() + " ML"
            diferenca.text = "Preço do Litro: " + "%.2f".format(melhorPreco.toString().toDouble()) + " L"

            if (mediaPreco < melhorPreco) {
                melhorPreco = mediaPreco
                marcaEmConta.text = "Marca: " + beer.beerMarca
                precoEmConta.text = "Preço: R$ " + beer.beerValor.toString()
                tamanhoEmConta.text = "Tamanho: " + beer.beerTamanho.toString() + " ML"
                diferenca.text = "Preço do Litro: " + "%.2f".format(melhorPreco.toString().toDouble()) + " L"
            }
        }
    }

    private fun setSupportActionBar(toolbar: Toolbar) {}
}

