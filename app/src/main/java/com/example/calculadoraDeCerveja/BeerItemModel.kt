package com.example.calculadoraDeCerveja

class BeerItemModel {

    var beerId: Int = 0;
    var beerMarca: String? = null
    var beerValor: Double = 0.0
    var beerTamanho: Int = 0

    constructor()

    constructor(beer: Beer) {
        this.beerMarca = beer.marca
        this.beerValor = beer.valor
        this.beerTamanho = beer.tamanho
    }

    companion object{
        val BEER_LIST_NAME = "beer_list"

        val BEER_ID_COLUMN = "id"
        val BEER_MARCA_COLUMN = "marca"
        val BEER_VALOR_COLUMN = "valor"
        val BEER_TAMANHO_COLUMN = "tamanho"

        val CREATE_TABLE = (
                "CREATE TABLE "
                + BEER_LIST_NAME + "("
                + BEER_ID_COLUMN + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BEER_MARCA_COLUMN + " TEXT,"
                + BEER_VALOR_COLUMN + " DOUBLE,"
                + BEER_TAMANHO_COLUMN + " INTEGER"
                + ")" )
    }
}