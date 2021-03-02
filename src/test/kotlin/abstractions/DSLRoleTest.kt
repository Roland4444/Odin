package abstractions

import junit.framework.TestCase
import java.util.*

class DSLRoleTest : TestCase() {

    fun testTestToString() {
        val readRole = Role("read")
        val writeRole = Role("write")
        val createRole = Role("create")
        val Roles = ArrayList(Arrays.asList(readRole, writeRole, createRole))
        val dsl = DSLRole("requests", Roles)
        assertNotNull(dsl.toString())
        print(dsl.toString())
    }
}