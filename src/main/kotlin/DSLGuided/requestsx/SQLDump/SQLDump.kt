package DSLGuided.requestsx.SQLDump

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.RoleHandler
import abstractions.Role

class SQLDump: DSLProcessor() {
    override fun render(DSL: String): Any {
        TODO("Not yet implemented")
    }

    lateinit var psaconnector: PSAConnector

    override fun parseRoles(DSL: String): List<Role> {
        TODO("Not yet implemented")
    }

    val count: RoleHandler{

    }
}