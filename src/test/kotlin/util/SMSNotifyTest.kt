package util

import junit.framework.TestCase
import org.junit.Test
import kotlin.test.assertNotEquals

class SMSNotifyTest : TestCase() {

  //  @Test
    fun testSendSMS() {
        val bad = "shit happens"
        assertNotEquals(bad, SMSNotify.sendSMS("89996013370", "Привед медвед"))   //<<==passed
    }
}