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
    abstract fun render(DSL: String): Any
    abstract fun parseRoles(DSL: String): List<Role>
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
