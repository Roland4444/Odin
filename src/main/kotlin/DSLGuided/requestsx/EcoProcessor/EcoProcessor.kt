package DSLGuided.requestsx.EcoProcessor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.PSA.PSASearchProcessor
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.ss.usermodel.*
import org.apache.poi.ss.util.CellRangeAddress
import org.apache.poi.ss.util.CellUtil
import java.io.FileOutputStream
import java.sql.ResultSet
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/////"'eco'=>::generatefor{'quarter':4,'year':2019,'department':6},::enabled{'false'}."
class EcoProcessor:  DSLProcessor() {
    val QuarterMap = mapOf(1 to "'year-01-01':'year-03-31'",
                           2 to "'year-04-01':'year-06-30'",
                           3 to "'year-07-01':'year-9-30'",
                           4 to "'year-10-01':'year-12-31'")
    var Book: Workbook = HSSFWorkbook()
    var Sheet = Book.createSheet()
    var Filename: String = "temp.xlsx"
    var quarter = 0
    var year = 0
    var department: Any = ""
    var DateRange: String = ""
    var CacheMetalInfo: HashMap<String, LinkedList<String>> = HashMap()
    lateinit var PSASearchProcessor: PSASearchProcessor
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        DateRange = QuarterMap.get(quarter)!!.replace("year", year.toString(), true)
        Book = HSSFWorkbook()
        Sheet = Book.createSheet()
        return "OK"
    }

    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }

    val generatefor: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "generatefor") {
                val Lst = a.key.Param as MutableList<KeyValue>
                Lst.forEach {
                    when (it.Key) {
                        "quarter" -> quarter = it.Value as Int
                        "year" -> year = it.Value as Int
                        "department" -> department = it.Value
                    }
                }
            }
        }
    }

//    fun createRows()

    fun genKeyValue(input: ResultSet): MutableList<KeyValue> {
        var out = mutableListOf<KeyValue>()
        while (input.next()){
            var keyValue = KeyValue(input.getString("metal_id"), input.getString("brutto"))
            out.add(keyValue)
        }
        return out
    }

    fun loadCacheMetalInfo() {
        val res = PSASearchProcessor.getMetalInfo()
        while (res.next()) {
            var Lst = LinkedList<String>()
            Lst.add(res.getString("waste"))
            Lst.add(res.getString("fkko"))
            Lst.add(res.getString("dangerclass"))
            CacheMetalInfo.putIfAbsent(res.getString("id"), Lst)
        }
    }

    fun process(){
        loadCacheMetalInfo()
        var departMatch = StringBuilder()
        val department = department as ArrayList<*>
        department.forEach { departMatch.append("'$it',") }
        departMatch.append("''")
        val search6 =  "'search'=>::sql{'SELECT * FROM psa '},::department{$departMatch},::datarange{$DateRange}."
        PSASearchProcessor.render(search6)
        val res = PSASearchProcessor.getPSA()
        var position = 0
        while (res!!.next()){
            val id = res.getString("id")
            val date = res.getString("date")
            println("FOUND ID::$id")
            val WBlock = genKeyValue(PSASearchProcessor.getWViaPSAId(id))
            val Client = res.getString("client")
            println("CLIENT::$Client")
            ////Data: String, Position: Int, Sheet: Sheet, Arr: List<KeyValue>
            position = writeToDocumentPSA(date, Client, position, Sheet, WBlock)
        }
        finalizeBook()

    }

    fun writeW(Rows: ArrayList<Row>, Arr: List<KeyValue>){
        var counter = 0
        Rows.forEach {
            val metalId = Arr.get(counter).Key
            for (i in 0..2)
                it.getCell(i+1).setCellValue(CacheMetalInfo.get(metalId)?.get(i))
            it.getCell(4).setCellValue(Arr.get(counter).Value as String)
            counter++
        }
    }


    fun writeData(Data: String, Rows: ArrayList<Row>, ){
        val Row_ = Rows.get(0)
        val cell = Row_.getCell(0)
        cell.setCellValue(Data)
    }

    fun writeClient(Client: String, Rows: ArrayList<Row>){
        val Row_ = Rows.get(0)
        val cell = Row_.getCell(5)
        println("WRITING CLIENT==>$Client")
        cell.setCellValue(Client)
    }

    fun mergingAreas(Position: Int, sheet: Sheet, Arr: List<KeyValue>){
        sheet.addMergedRegion(CellRangeAddress(Position, Position+Arr.size-1, 0, 0))
        sheet.addMergedRegion(CellRangeAddress(Position, Position+Arr.size-1, 5, 5))
    }


    //desc, fkko, dangerclass
    fun getMetalInfo(Name: String): LinkedList<String> {
        var Res = LinkedList<String>()
        var prepared =  PSASearchProcessor.executor!!.conn.prepareStatement("SELECT * FROM `psa`.`metal` WHERE `title` = ?;")
        prepared.setString(1, Name)
        val rs: ResultSet? = prepared?.executeQuery()
        if (rs!!.next()) {
            Res.add(rs.getString("waste"))
            Res.add(rs.getString("fkko"))
            Res.add(rs.getString("dangerclass"))
        };
        CacheMetalInfo.putIfAbsent(Name, Res)
        return Res
    }

    fun writeToDocumentPSA(Data: String, Client: String, Position: Int, Sheet: Sheet, Arr: List<KeyValue> ): Int{   //KeyValue: MetalName: Weigth
        var Rows = ArrayList<Row>()
        var Cells = ArrayList<Cell>()
        for (i in 1..Arr.size){
            val row: Row = Sheet.createRow(i+Position-1)
            for (j in 0..6){
                val cell = row.createCell(j)
                if ((j == 0) or (j==5))
                    CellUtil.setAlignment(cell, Book, CellStyle.ALIGN_CENTER_SELECTION)
                Cells.add(cell)
            }
            Rows.add(row)
        }
    ///    println("ROWS SIZE:: ${Rows.size}")
        writeW(Rows, Arr)
        writeClient(Client, Rows)
        writeData(Data,  Rows)
        mergingAreas(Position, Sheet, Arr)

        Sheet.autoSizeColumn(1)
        return Position+Arr.size;
    }

    fun finalizeBook(){
        Book?.write(FileOutputStream(Filename))
        Book?.close()
    }

    fun loadRoles(D: List<Role>): Unit{
        mapper.clear()
        D.forEach { appendRole(it) }
    }

    fun appendRole(R: Role){
        when (R?.Name){
            "generatefor" -> mapper.put(R, generatefor)
            "enabled" -> mapper.put(R, enable)
        }
    }
}