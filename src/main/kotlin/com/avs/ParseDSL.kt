package com.avs
import abstractions.DSLRole
import abstractions.Role
class ParseDSL {
    fun getDSLRulesfromString(input: String): DSLRole? {
        val objectName: String = input.substring(input.indexOf("'")+1, input.lastIndexOf("'"))
        println("""Loading rules for object <$objectName>""")
        return DSLRole(objectName, parseRoles(input))
    }
    fun parseRole(input: String): Role? {
        if (input.indexOf("{") == -1) return null
        val rolename: String= input.substring(input.indexOf("::")+2, input.indexOf("{"))
        if ((rolename.length == 0) || (rolename ==null)) return null;
        println("""Loading rules  <$rolename>""")
        return Role(rolename)
    }
    fun parseRoles(input: String): List<Role>{
        var result: MutableList<Role>  = mutableListOf()
        var initialString = input
        var role: Role? = parseRole(initialString)
        while (role != null){
            result.add(role)
            initialString = initialString.substring(initialString.indexOf("}")+1)
            print("Current initial $initialString")
            role  = parseRole(initialString)
        };
        return result
    }
}