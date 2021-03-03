package DSLGuided.requestsx

import abstractions.Param
import abstractions.Role
import com.avs.ParseDSL
import java.util.*


typealias RoleHandler = (Param: Param) -> Unit
typealias DumbHandler = (Param:Int) -> Int
typealias DumbHandler2 = (Param:Int, Param2: Int) -> Int

abstract class DSLProcessor() {
    var parser = ParseDSL()
    var mapper = mutableMapOf<Role, RoleHandler>()
    init{

    }
    abstract fun render(DSL: String): String
    abstract fun parseRoles(DSL: String): List<Role>
    var outtemplate: String = """"""
    fun init(){

    }
    
}
