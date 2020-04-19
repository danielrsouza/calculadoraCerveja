package com.example.calculadoraDeCerveja

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private val DB_NAME = "beer_db"
class DBHelper (context: Context): SQLiteOpenHelper(context, DB_NAME, null, 1) {
   val itensList: List<BeerItemModel>
        get(){
            val beerItens = ArrayList<BeerItemModel>()
            val selectQuery = "SELECT * FROM " + BeerItemModel.BEER_LIST_NAME
            val db = this.writableDatabase
            val cursor = db.rawQuery(selectQuery, null)

            if (cursor.moveToFirst()) {
                do{
                    val beerItem = BeerItemModel()
                    beerItem.beerId = cursor.getInt(cursor.getColumnIndex(BeerItemModel.BEER_ID_COLUMN))
                    beerItem.beerMarca = cursor.getString(cursor.getColumnIndex(BeerItemModel.BEER_MARCA_COLUMN))
                    beerItem.beerValor = cursor.getDouble(cursor.getColumnIndex(BeerItemModel.BEER_VALOR_COLUMN))
                    beerItem.beerTamanho = cursor.getInt(cursor.getColumnIndex(BeerItemModel.BEER_TAMANHO_COLUMN))

                    beerItens.add(beerItem)
                } while (cursor.moveToNext())
            }

            db.close()
            return beerItens
        }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(BeerItemModel.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + BeerItemModel.BEER_LIST_NAME)

        onCreate(db)
    }

    fun insertBeerItem(beer: Beer): Long {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(BeerItemModel.BEER_MARCA_COLUMN, beer.marca)
        values.put(BeerItemModel.BEER_VALOR_COLUMN, beer.valor)
        values.put(BeerItemModel.BEER_TAMANHO_COLUMN, beer.tamanho)

        val item = db.insert(BeerItemModel.BEER_LIST_NAME, null, values)

        db.close()
        return item
    }

    fun getBeerItem(item: Long) : BeerItemModel{
        val db = readableDatabase
        val cursor = db.query(BeerItemModel.BEER_LIST_NAME, arrayOf(BeerItemModel.BEER_ID_COLUMN,
        BeerItemModel.BEER_MARCA_COLUMN, BeerItemModel.BEER_VALOR_COLUMN, BeerItemModel.BEER_TAMANHO_COLUMN
        ), BeerItemModel.BEER_ID_COLUMN + "=?", arrayOf(item.toString()), null, null, null, null)

        cursor?.moveToFirst()

        val beer = Beer(
            cursor!!.getInt(cursor.getColumnIndex(BeerItemModel.BEER_ID_COLUMN)),
            cursor.getString(cursor.getColumnIndex(BeerItemModel.BEER_MARCA_COLUMN)),
            cursor.getDouble(cursor.getColumnIndex(BeerItemModel.BEER_VALOR_COLUMN)),
            cursor.getInt(cursor.getColumnIndex(BeerItemModel.BEER_TAMANHO_COLUMN))
        )
        val beerItem = BeerItemModel(
            beer
        )
        cursor.close()
        return beerItem
    }

    fun deleteBeerItem(beerItemModel: BeerItemModel) {
        val db = writableDatabase
        db.delete(BeerItemModel.BEER_LIST_NAME, BeerItemModel.BEER_ID_COLUMN + " = ?",
        arrayOf(beerItemModel.beerId.toString()))
        db.close()
    }

    fun deleteAllBeer()
    {
        val db = writableDatabase
        db.delete(BeerItemModel.BEER_LIST_NAME, BeerItemModel.BEER_ID_COLUMN + " > ?",
        arrayOf("0"))
        db.close()
    }

    fun updateBeerItem(beerItemModel: BeerItemModel): Int {
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(BeerItemModel.BEER_MARCA_COLUMN, beerItemModel.beerMarca)
        values.put(BeerItemModel.BEER_VALOR_COLUMN, beerItemModel.beerValor)
        values.put(BeerItemModel.BEER_TAMANHO_COLUMN, beerItemModel.beerTamanho)

        return db.update(BeerItemModel.BEER_LIST_NAME, values, BeerItemModel.BEER_ID_COLUMN + " = ?",
        arrayOf(beerItemModel.beerId.toString()))
    }
}