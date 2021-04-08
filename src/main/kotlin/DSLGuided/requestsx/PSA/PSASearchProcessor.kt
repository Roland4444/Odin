package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import fr.roland.DB.Executor
import java.sql.ResultSet
import java.util.*
class PSASearchProcessor  : DSLProcessor() {
    companion object{
        fun search(input: String, PSASearch : PSASearchProcessor): String{
            PSASearch.render(input)
            return PSASearch.createJSONResponce(PSASearch.getPSA())

        }
    }
    lateinit var searchFrom: String
    lateinit var searchTo: String
    lateinit var numberPsa:String
    lateinit var client__:String
    lateinit var platenumber_: String
    lateinit var typepayment_: String
    var departments = mutableListOf<String>()
    lateinit var executor: Executor
    var if_present = false
    var initialString: StringBuilder = StringBuilder()
    var first = true
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        println("\n\n\nCALLING Handlers!\n\n\n")
        mapper.forEach { it.value.invoke(it.key)  }
        println("EFFECTIVE STRIUNG \n${initialString.toString()}")
        return "OK"
    }
    fun params(){
        if (!if_present){
            initialString.append(" where  ")
            if_present = true
        }
        if (first){
            first =  false
            return
        }
        initialString.append(" AND  ")
    }
    fun getPSA(): ResultSet? {
        val stmt = executor.conn.createStatement()
        val res = stmt?.executeQuery(initialString.toString())
        return res
    }
    fun getdepIdExecutor(input: String): String {
        var param = ArrayList<Any?>()
        param.add(input)
        val res: ResultSet =
            executor.executePreparedSelect("SELECT * FROM `psa`.`department` WHERE `name` = ?;", param)
        if (res.next()) {
            return res.getString("id")
        };
            return ""
    }
    fun getdepNameExecutor(input: String): String {
        var param = ArrayList<Any?>()
        param.add(input)
        val res: ResultSet =
            executor.executePreparedSelect("SELECT * FROM `psa`.`department` WHERE `id` = ?;", param)
        if (res.next()) {
            return res.getString("name")
        };
        return ""
    }
    fun appendRow(row:ResultSet): String{
        var result = StringBuilder()
        result.append("""{"id":"${row.getString("id")}",
            "datetime":"${row.getString("date")}",
            "department":"${getdepNameExecutor(row.getString("department_id"))}",
            "psanumber":"${row.getString("number")}",
            "client":"${row.getString("client")}", 
            "platenumber":"${row.getString("plate_number")}",
            "metals":"БРОНЗА", "uuid":"126103"},""")
        return result.toString()
    }
    fun createJSONResponce(input : ResultSet?): String{
        var result= StringBuilder()
        result.append("[")
        while (input?.next() == true){
            result.append("${appendRow(input)}")
        }
        var resStr = result.toString()
        print("Length"+resStr.length)
        if( resStr.length==1)
            return "[]"
        return  resStr.substring(0, resStr.length-1)+"]"
    }
    fun departments(input: List<String>): String {
            val builder = StringBuilder()
            if (input.size == 0)
                return ""
            builder.append("(")
            for (i in 0..input.size - 1) {
                if (input.get(i) == "") continue
                val departmentId = getdepIdExecutor(input.get(i))
                println("\n\nDEPARTMENT!!! $departmentId  @${input.get(i)}\n\n")
                builder.append("(department_id=$departmentId)")
                if ((i == input.size - 1) or  (input.get(i+1)==""))
                    break
                println("I>>>>>$i");
                builder.append("   or ")
            }
            builder.append(")")
            return builder.toString()
        }

        val department: RoleHandler = {
            mapper.forEach { a ->
                if (a.key.Name == "department") {
                    departments = a.key.Param as MutableList<String>
                    params()
                    val appendix = departments(departments)
                    println("APPENDING ${appendix}")
                    initialString.append(appendix)
                }
            }
        }
        val numberpsa: RoleHandler = {
            mapper.forEach { a ->
                if (a.key.Name == "numberpsa") {
                    numberPsa = a.key.Param as String
                    params()
                    val appendix = "( number='$numberPsa')"
                    println("APPENDING ${appendix}")
                    initialString.append(appendix)
                }
            }
        }
        val sql: RoleHandler = {
            mapper.forEach { a ->
                if (a.key.Name == "sql") {
                    initialString.append(a.key.Param as String)
                }
            }
        }
        val datarange: RoleHandler = {
            mapper.forEach { a ->
                if (a.key.Name == "datarange") {
                    val keyvalue: KeyValue = a.key.Param as KeyValue
                    searchFrom = keyvalue.Key
                    searchTo = keyvalue.Value as String
                    params()
                    val appendix = "( `psa`.`date` between '${searchFrom}' and '${searchTo}')"
                    println("APPENDING ${appendix}")
                    initialString.append(appendix)
                }
            }
        }
        val client: RoleHandler = {
            mapper.forEach { a ->
                if (a.key.Name == "client") {
                    client__ = a.key.Param as String
                    params()
                    val appendix = " (client = '${client__}')"
                    println("APPENDING ${appendix}")
                    initialString.append(appendix)

                }
            }
        }
        val typepayment: RoleHandler = {
            mapper.forEach { a ->
                if (a.key.Name == "typepayment") {
                    typepayment_ = a.key.Param as String
                    params()
                }
            }
        }
        val platenumber: RoleHandler = {
            mapper.forEach { a ->
                if (a.key.Name == "platenumber") {
                    platenumber_ = a.key.Param as String
                    params()
                    val appendix = " (plate_number= '${platenumber_}')"
                    println("APPENDING ${appendix}")
                    initialString.append(appendix)
                }
            }
        }
        override fun parseRoles(DSL: String): List<Role> {
            return parser.parseRoles(DSL!!)
        }
        fun loadRoles(D: List<Role>): Unit {
            mapper.clear()
            D.forEach { appendRole(it) }
        }
        fun appendRole(R: Role) {
            print("Adding role ${R.Name}\n")
            when (R?.Name) {
                "numberpsa" -> mapper.put(R, numberpsa)
                "datarange" -> mapper.put(R, datarange)
                "client" -> mapper.put(R, client)
                "typepayment" -> mapper.put(R, typepayment)
                "platenumber" -> mapper.put(R, platenumber)
                "department" -> mapper.put(R, department)
                "sql" -> mapper.put(R, sql)
            }
        }
    }