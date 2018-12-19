package com.apps.likhithasai.coinz

import org.junit.Assert
import org.junit.Test

class UserTest {
    @Test
    fun userCreation() {
        val user1 = User("test1", "123")
        val user2 = User("test2", "456")
        Assert.assertEquals("test1", user1.name)
        Assert.assertEquals("456", user2.score)
    }
}