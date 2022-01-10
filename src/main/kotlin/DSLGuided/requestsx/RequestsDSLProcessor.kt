package DSLGuided.requestsx
import abstractions.Role
class RequestsDSLProcessor() : DSLProcessor() {
    companion object{
        val req = RequestsDSLProcessor()

    }
    val write: RoleHandler = {
    }
    fun default():Unit{
        outtemplate="<h1>Недостаточно прав</h1>"
    }
    val marina: RoleHandler = {}

    val olga: RoleHandler = {print("Apply olga\n")
        outtemplate = outtemplate.replace("'SUSPENDING'}; ", "'EMPTY'};");
        outtemplate = outtemplate.replace("<script type=\"text/babel\"  src=\"js/processJSON.js\">", "<script type=\"text/babel\"  src=\">");

    }
    val guest: RoleHandler = {outtemplate="<h1>BOLT</h1>"}
    val add: DumbHandler={it+2}
    val add2: DumbHandler2 = { i: Int, i1: Int -> i+i1}

    override fun r(DSL: String): String {
        loadRoles(parseRoles(DSL))
        if (mapper.size==0)
            default()
        else
            mapper.forEach { it.value.invoke(it.key)  }
        return  outtemplate
    }


    override fun appendRole(R: Role){
        print("Adding role ${R.Name}\n")
        when (R?.Name){
            "full" -> mapper.put(R, marina)
            "view" -> mapper.put(R, olga)
            "guest" -> mapper.put(R, guest)
        }
    }

    val test2: RoleHandler = {outtemplate += "xxx"}
}




