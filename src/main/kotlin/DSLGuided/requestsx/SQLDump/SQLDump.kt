package DSLGuided.requestsx.SQLDump

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.RoleHandler
import abstractions.Role

class SQLDump: DSLProcessor() {
    override fun r(DSL: String): Any {
        TODO("Not yet implemented")
    }

    lateinit var psaconnector: PSAConnector



    val count: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "count") {
              ///  typepayment_ = a.key.Param as String
            ////    params()
            }
        }
    }
}