package util

import junit.framework.TestCase
import kotlin.test.assertNotEquals

class SMSNotifyTest : TestCase() {


    fun testSendSMS() {
        assertNotEquals(null, SMSNotify.sendSMS("avs2", "7BsdfdsfsdfsdfsL3r","89608607763", "Привед медвед снова"))   //<<==passed
    }
}