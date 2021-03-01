package DSLGuided.requestsx

import abstractions.Role
import com.avs.ParseDSL


typealias RoleHandler = () -> Unit
typealias DumbHandler = (Param:Int) -> Int
typealias DumbHandler2 = (Param:Int, Param2: Int) -> Int

abstract class DSLProcessor(DSL: String) {
    var parser = ParseDSL()
    var mapper = mutableMapOf<Role, RoleHandler>()
    init{
        print("From constructor::  $DSL\n")
    }
    abstract fun render(DSL: String): String
    abstract fun parseRoles(DSL: String): List<Role>
    abstract val DSL: Any?
    var outtemplate: String = """"""
    fun init(){

    }
    
}
