package DSLGuided.requestsx.Supervisor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import DSLGuided.requestsx.Sber.simpleString
import abstractions.Role

class DSLSupervisor: DSLProcessor() {
    var FILELOG: simpleString = {""}
    var DELAY                 = {1000}
    override fun r(DSL: String): Any {
        TODO("Not yet implemented")
    }



    val filelog: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "filelog")
                FILELOG = {a.key.Param as String}
        }
    }
    val delay: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "delay")
                DELAY = {a.key.Param as Int}
        }
    }
}