package DSLGuided.requestsx
import abstractions.Role
class requests(override val DSL: String?) : DSLProcessor(DSL!!  ) {
    val write: RoleHandler = {print("Apply write\n")}
    val read: RoleHandler = {print("Apply read\n")}
    val super_: RoleHandler = {print("Apply super\n")}
    val add: DumbHandler={it+2}
    val add2: DumbHandler2 = { i: Int, i1: Int -> i+i1}
    fun dumbsum(A: Int, B: Int)=A+B
    override fun render(DSL: String): String {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke()  }
        return  outtemplate
    }

    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }
    fun appendRole(R: Role){
        print("Adding role ${R.Name}\n")
        when (R?.Name){
            "read" -> mapper.put(R, read)
            "write" -> mapper.put(R, write)
            "super" -> mapper.put(R, super_)
        }
    }
    fun loadRoles(D: List<Role>): Unit{
        D.forEach { appendRole(it) }
    }
    val test2: RoleHandler = {outtemplate += "xxx"}
}



