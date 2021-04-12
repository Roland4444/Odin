package abstractions

import com.avs.ParseDSL
import com.mysql.cj.xdevapi.JsonArray
import junit.framework.TestCase
import org.junit.Test
import util.Saver
import java.util.*

class DSLRoleTest : TestCase() {
    val parser = ParseDSL()
    @Test
    fun testTestToString() {

        val readRole = Role("read","{}", parser)
        val writeRole = Role("write","{}", parser)
        val createRole = Role("create","{}", parser)
        val Roles = ArrayList(Arrays.asList(readRole, writeRole, createRole))
        val dsl = DSLRole("requests", Roles)
        assertNotNull(dsl.toString())
        print(dsl.toString())
    }
    @Test
    fun testsave(){
        val readRole = Role("read","{}", parser)
        val writeRole = Role("write","{}", parser)
        val createRole = Role("create","{}", parser)
        val Roles = ArrayList(Arrays.asList(readRole, writeRole, createRole))
        val dsl = DSLRole("requests", Roles)
        val bytes = Saver.savedToBLOB(dsl)
        val restored = Saver.restored(bytes)
        assertEquals(dsl.toString(), restored.toString())
    }


    @Test
    fun testjson(){
        var JSONArra = JsonArray()

    }
}