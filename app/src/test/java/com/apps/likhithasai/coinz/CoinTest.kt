package com.apps.likhithasai.coinz

import org.junit.Assert
import org.junit.Test

class CoinTest {
    @Test
    fun coinCreation() {
        val coin1 = Coin("4643-39cd-becf-6aa5-4ef1-bcdf", "DOLR", "3.644575729326507")
        val coin2 = Coin("b956-4ea3-5196-b46d-9a7e-2157", "QUID", "6.676745622323417")
        Assert.assertEquals("4643-39cd-becf-6aa5-4ef1-bcdf", coin1.id)
        Assert.assertEquals("6.676745622323417", coin2.value)
    }
}