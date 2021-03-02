package abstractions


import java.lang.StringBuilder

data class DSLRole(var ObjectName: String, var Roles: List<Role>){

    override fun toString(): String {
        var roles: StringBuilder = StringBuilder()
        Roles.forEach { roles.append(""" ::$it{},""") }
        var res = """'$ObjectName' =>${roles.toString()}"""
        res=res.substring(0, res.length-1)
        res += "."
        return res
    }


}