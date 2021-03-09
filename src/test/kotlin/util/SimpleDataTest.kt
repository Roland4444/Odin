package util

import junit.framework.TestCase
import org.junit.Test

class SimpleDataTest : TestCase() {

    @Test
    fun testGetA() {
        val data : SimpleData = SimpleData("12","14")
        val bytes = Saver.savedToBLOB(data)
        val restored = Saver.restored(bytes)
        assertEquals(data, restored)
    }
}