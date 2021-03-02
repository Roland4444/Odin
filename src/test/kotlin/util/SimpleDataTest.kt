package util

import junit.framework.TestCase

class SimpleDataTest : TestCase() {

    fun testGetA() {
        val data : SimpleData = SimpleData("12","14")
        val bytes = Saver.savedToBLOB(data)
        val restored = Saver.restored(bytes)
        assertEquals(data, restored)
    }
}