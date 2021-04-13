package DSLGuided.requestsx.PSA

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import fr.roland.DB.Executor
import org.json.simple.JSONArray
import org.json.simple.JSONObject
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
    fun reset(){
        initialString.clear()
        searchFrom=""
        searchTo=""
        numberPsa=""
        client__=""
        platenumber_=""
        typepayment_=""
        departments.clear()
        if_present = false
        first = true
    }
    override fun render(DSL: String): Any {
        reset()
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        println("\n\n\nCALLING Handlers!\n\n\n")
        mapper.forEach { it.value.invoke(it.key)  }
        println("EFFECTIVE STRIUNG \n${initialString.toString()}")
        if (initialString.toString().equals("SELECT * FROM psa ")) {
            println("\n\n\n\n\nFULL REQUEST\n\n\n\n\n\n")
            initialString.clear()
            initialString.append("SELECT * FROM psa LIMIT 200")
        }

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

    fun getmetalName(metal_id: String):String{
        var param = ArrayList<Any?>()

        param.add(metal_id)
        val res: ResultSet =
            executor.executePreparedSelect("SELECT * FROM `psa`.`metal` WHERE `id` = ?;", param)
        if (res.next())
            return res.getString("title");
        return ""
    }

    fun loadMetals(id: String): String{
        var param = ArrayList<Any?>()
        param.add(id)
        var result = mutableListOf<String>()
        val res: ResultSet =
            executor.executePreparedSelect("SELECT * FROM `psa`.`weighing` WHERE `psa_id` = ?;", param)
        while (res.next()){
                var metalname =  getmetalName(   res.getString("metal_id"))
                result.add(metalname)
        }
        var restr = result.toString()
        var restr1 = restr.replace("[","")
        var restr2 = restr1.replace("]","")
        return restr2
    }
    fun appendRow(row:ResultSet): JSONObject {
        var jsonobj = JSONObject()
        jsonobj.put("id", row.getString("id"))
        jsonobj.put("datetime", row.getString("date"))
        jsonobj.put("department", getdepNameExecutor(row.getString("department_id")))
        jsonobj.put("psanumber", row.getString("number"))
        jsonobj.put("client", row.getString("client"))
        jsonobj.put("platenumber", row.getString("plate_number"))
        jsonobj.put("metals", loadMetals(row.getString("id")))
        jsonobj.put("uuid", row.getString("uuid"))
        return jsonobj

    }

    fun createJSONResponce(input : ResultSet?): String{
        var result= JSONArray()

        while (input?.next() == true) {
            println("client column="+input.getString("client"))
            println("uuid column="+input.getString("uuid"))
            result.add(appendRow(input))
        }
        return result.toString()
    }
    fun departments(input: List<String>): String {
            val builder = StringBuilder()
            if (input.size == 0)
                return ""
            builder.append("(")
            for (i in 0..input.size - 1) {
                if (input.get(i) == "") continue
                val departmentId = getdepIdExecutor(input.get(i))
                builder.append("(department_id=$departmentId)")
                if ((i == input.size - 1) or  (input.get(i+1)==""))
                    break
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
                    initialString.append(appendix)
                }
            }
        }
        val client: RoleHandler = {
            mapper.forEach { a ->
                if (a.key.Name == "client") {
                    client__ = a.key.Param as String
                    params()
                    val appendix = " (client LIKE '%${client__}%')"               /// val appendix = " (client = '${client__}')"
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