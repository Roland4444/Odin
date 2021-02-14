package com.avs

import abstractions.DSLRole
import abstractions.Role

class ParseDSL {
    fun getDSLRulestoObject(input: String): DSLRole? {
        val objectName: String = input.substring(input.indexOf("'")+1, input.lastIndexOf("'"))
        println("""Loading rules for object <$objectName>""")
        return DSLRole(objectName, mutableListOf())
    }
    fun parseRole(input: String): Role {
        val rolename: String= input.substring(input.indexOf("::")+2, input.indexOf("{"))
        println("""Loading rules  <$rolename>""")
        return Role(rolename)
    }
}