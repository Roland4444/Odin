package DSLGuided.requestsx.EcoProcessor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
/////"'eco'=>::generatefor{'quarter':4,'year':2019,'department':6},::enabled{'false'}."
class EcoProcessor:  DSLProcessor() {
    var quarter = 0
    var year = 0
    var department = -1
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return "OK"
    }

    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }

    val generatefor: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "generatefor") {
                val Lst = a.key.Param as MutableList<KeyValue>
                Lst.forEach {
                    val f: KeyValue = it as KeyValue
                    println("""KEY VALUE ${f.Key}::${f.Value}""")
                    when ((it as KeyValue).Key) {
                        "quarter" -> quarter = it.Value as Int
                        "year" -> year = it.Value as Int
                        "department" -> department = it.Value as Int
                    }
                }
            }
        }
    }

    fun loadRoles(D: List<Role>): Unit{
        mapper.clear()
        D.forEach { appendRole(it) }
    }

    fun appendRole(R: Role){
        when (R?.Name){
            "generatefor" -> mapper.put(R, generatefor)
            "enabled" -> mapper.put(R, enable)
        }
    }
}