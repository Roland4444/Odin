package util

import junit.framework.TestCase

class CredentialTest : TestCase() {

    fun testGetPass() {
        val cred = Credential("avs", "7BBAP7nkTCA4L3r")
        Saver.write(Saver.savedToBLOB(cred), "cred.bin")
        assertNotNull(Saver.savedToBLOB(cred))
        val rest: Credential = Saver.restored(Saver.readBytes("cred.bin")) as Credential
        assertEquals(cred.login, rest.login)
        assertEquals(cred.pass, rest.pass)

    }
}