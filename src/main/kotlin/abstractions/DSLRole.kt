package abstractions
import java.io.Serializable
import java.lang.StringBuilder
data class DSLRole(var ObjectName: String, var Roles: List<Role>) : Serializable{
    override fun toString(): String {
        var roles = StringBuilder()
        Roles.forEach { roles.append(""" ::$it{},""") }
        var res = """'$ObjectName' =>${roles}"""
        return res.substring(0, res.length-1)+"."
    }
}