package DSLGuided.requestsx.WProcessor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import se.roland.util.HTTPClient
import java.io.File
import java.io.FileOutputStream
import java.net.http.HttpClient
import java.sql.ResultSet
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/////"'wprocessor'=>::pathtoimgs{/home/romanx/IMG},::addresstoresend{db2.avs.com.ru/storage/purchase/import},::enabled{'false'}."
class WProcessor : DSLProcessor()  {
    companion object {
        fun saveImages(DSL: String, WProc: WProcessor, Arr1: ByteArray, Arr2: ByteArray,Department: String, Date: String, WaybillID: String){
            WProc.render(DSL)
            WProc.saveImages(Arr1, Arr2, Department, Date, WaybillID)
        }
        fun resend(DSL: String, WProc: WProcessor, Params: HashMap<String, String>){
            WProc.render(DSL)
            WProc.resenddata(Params)
        }
        fun getTransfers(DSL: String, WProc: WProcessor, DepId: String): LinkedList<Any>? {
            WProc.render(DSL)
            return WProc.getResultinLinkedList(DepId)
        }
    }
    lateinit var pathtoimgs_ : String
    lateinit var addresstoresend_: String
    var UseDepsMap = FALSE_ATOM
    var DepsMap = mutableMapOf<String, String>()
    var testmode_: Boolean = false
    var exampleListFile = "linked.bin"
    var client: HttpClient = HttpClient.newHttpClient()
    val i_ ="_"
    val appendix = ".jpg"
    lateinit var dbconnector: DBConnector
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        return "OK"
    }

    fun getDepIdViaName(input: String): String{
        var param = ArrayList<Any?>()
        param.add(input)
        val res: ResultSet = dbconnector.executor!!.executePreparedSelect("SELECT * FROM `department` WHERE `name` = ?;", param)
            if (res.next()) {
                return res.getString("id")
            };
            return ""
    }

    fun getW(DepId: String): ResultSet? {
        var dep = DepId
        if (dep.length>2)
            dep = getDepIdViaName(DepId)
        var param = ArrayList<Any?>()
        param.add(dep)
        val cal   = Calendar.getInstance();
        val D_now = cal.getTime();
        cal.add(Calendar.DATE, -3);
        val D_3 = cal.getTime();
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val D_3Date =  dateFormat.format(D_3)
        val D_nowDate =  dateFormat.format(D_now)
        println(D_nowDate)
        println(D_3Date)
        println("PREPARED::\n"+"SELECT * FROM `transfer` WHERE (`actual_weight`= '0.00') AND (`dest_department_id`=${dep}) AND (`date` between '${D_3Date}' and '${D_nowDate}')")
        val res: ResultSet? =dbconnector.executor?.executePreparedSelect("SELECT * FROM `transfer` WHERE (`actual_weight`= '0.00') AND (`dest_department_id`=?) AND (`date` between '${D_3Date}' and '${D_nowDate}')", param)
        while (res?.next() == true){
            println(res.getInt("id"))
        }
        val res2: ResultSet? =dbconnector.executor?.executePreparedSelect("SELECT * FROM `transfer` WHERE (`actual_weight`= '0.00') AND (`dest_department_id`=?) AND (`date` between '${D_3Date}' and '${D_nowDate}')", param)
        return res2
    }

    fun getdepNameExecutor(input: String): String {
        var param = ArrayList<Any?>()
        param.add(input)
        val res: ResultSet =
            dbconnector.executor!!.executePreparedSelect("SELECT * FROM `department` WHERE `id` = ?;", param)
        if (res.next()) {
            return res.getString("name")
        };
        return ""
    }

    fun getmetalName(metal_id: String): String {
        var param = ArrayList<Any?>()
        param.add(metal_id)
        val res: ResultSet =
            dbconnector.executor!!.executePreparedSelect("SELECT * FROM `metal` WHERE `id` = ?;", param)
        if (res.next())
            return res.getString("name");
        return ""
    }

    fun getResultinLinkedList(dep: String): LinkedList<Any>{
        if (testmode_)
            return Saver.Saver.restored(Saver.Saver.readBytes(exampleListFile)) as LinkedList<Any>
        val input = getW(dep)
        var res = LinkedList<Any>()
        while (input!!.next()){
            val uuid = input.getString("uuid")
            val info = getInfo(uuid)
            if (info?.next() == true){
                val Map = mapOf("source_department_id" to input.getString("source_department_id"),
                    "depDescription" to getdepNameExecutor(input.getString("source_department_id")),
                    "metalname" to getmetalName(input.getString("metal_id")),
                    "metal_id" to input.getString("metal_id"),
                    "weight" to input.getString("weight"),
                    "uuid" to input.getString("uuid"),
                    "naklnumb" to info.getString("naklnumb"),
                    "transnumb" to info.getString("transnumb"),
                    "transport" to info.getString("transport"))
                res.add(Map)
            }
        }
        return res
    }

    fun getInfo(uuid: String): ResultSet? {
        var param = ArrayList<Any?>()
        param.add(uuid)
        return dbconnector.executor?.executePreparedSelect("SELECT * FROM `remote_sklad` WHERE `uuid`=?;", param)
    }

    fun saveImages(Arr1: ByteArray, Arr2: ByteArray, Department__: String, Date: String, WaybillID: String): Unit{
        val targetDir = pathtoimgs_+File.separator+Date
        println("TARGET DIR::$targetDir")
        if (!File(targetDir).exists())
            File(targetDir).mkdirs()
        var Department = Department__
        if (UseDepsMap.equals(TRUE_ATOM))
            Department = DepsMap.get(Department).toString()
        val filename = targetDir+File.separator+Department+i_+WaybillID+i_
        val filename1 = filename+1
        val filename2 = filename+2
        val fos1 = FileOutputStream(filename1+appendix)
        fos1.write(Arr1)
        val fos2 = FileOutputStream(filename2+appendix)
        fos2.write(Arr2)
    }

    fun resenddata(Params: HashMap<String, String>){
        println("\n\n\n\nRESENDING!!!!\n\n\n\n\n")
        HTTPClient.sendPOST(Params, addresstoresend_)
    }

    val testmode: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "testmode"){
                val param= a.key.Param as String
                if (param.equals("true"))
                    testmode_=true
            }
        }
    }

    val example: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "example")
                exampleListFile= a.key.Param as String
        }
    }

    val pathtoimgs: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "pathtoimgs") {
                pathtoimgs_ = a.key.Param as String
                if (!File(pathtoimgs_).exists())
                    File(pathtoimgs_).mkdirs()
            }
        }
    }

    val addresstoresend: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "addresstoresend")
                addresstoresend_ = a.key.Param as String
        }
    }

    val usedepsmap: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "usedepsmap") {
                DepsMap.clear()
                UseDepsMap = FALSE_ATOM
                var Arr = a.key.Param as MutableList<Any>
                Arr.forEach { a ->
                    when (a) {
                        is KeyValue -> DepsMap.put(a.Key, a.Value.toString())
                        is String -> UseDepsMap = a;
                    }
                }
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
            "pathtoimgs" -> mapper.put(R, pathtoimgs)
            "addresstoresend" -> mapper.put(R, addresstoresend)
            "enabled" -> mapper.put(R, enable)
            "testmode" -> mapper.put(R, testmode)
            "example" -> mapper.put(R, example)
            "usedepsmap" -> mapper.put(R, usedepsmap)
        }
    }
}