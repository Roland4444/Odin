package DSLGuided.requestsx

import abstractions.Role
import com.avs.ParseDSL

typealias RoleHandler = (Role: Role) -> Unit
typealias DumbHandler = (Param:Int) -> Int
typealias StringHandler = (Param:String) -> String
typealias DumbHandler2 = (Param:Int, Param2: Int) -> Int

abstract class DSLProcessor() {
    var parser = ParseDSL()
    var mapper = mutableMapOf<Role, RoleHandler>()
    init{

    }
    abstract fun render(DSL: String): Any
    abstract fun parseRoles(DSL: String): List<Role>
    var outtemplate: String = """"""
    fun init(){

    }
    
}
