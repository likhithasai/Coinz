package com.apps.likhithasai.coinz

/**
 * A data class for coin
 *
 * This class models a coin in the game
 *
 * @property id the id of the coin
 * @property currency the currency of the coin.
 * @property value the value of the coin (between 0 and 10)
 */
data class Coin(val id:String = "", val currency:String = "", val value: String = "")