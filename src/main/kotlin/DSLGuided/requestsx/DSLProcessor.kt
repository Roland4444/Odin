package DSLGuided.requestsx
import abstractions.Role
import com.avs.ParseDSL
typealias RoleHandler = (Role: Role) -> Unit
typealias DumbHandler = (Param:Int) -> Int
typealias StringHandler = (Param:String) -> String
typealias DumbHandler2 = (Param:Int, Param2: Int) -> Int
abstract class DSLProcessor() {

    val TRUE_ATOM = "true"
    val FALSE_ATOM = "false"
    val OK = "OK"
    val EMPTY_ATOM = ""
    var enabled: String = "false"
    var parser = ParseDSL()
    var mapper = mutableMapOf<Role, RoleHandler>()
    abstract fun r(DSL: String): Any
    abstract fun appendRole(Role: Role): Any

    fun loadRoles(D: List<Role>): Unit {
        mapper.clear()
        D.forEach { appendRole(it) }
    }
    fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }
    var outtemplate: String = """"""
    val enable: RoleHandler = {
        mapper.forEach {
                a->
            if (a.key.Name=="enabled") {
                println("\n\n\ninto enable lambda")
                enabled = a.key.Param as String
            }
        }
    }
}
