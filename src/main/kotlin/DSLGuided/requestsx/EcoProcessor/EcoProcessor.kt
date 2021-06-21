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

    fun process(){
        var departMatch = StringBuilder()
        val department = department as ArrayList<*>
        department.forEach { departMatch.append("'$it',") }
        departMatch.append("''")
        val search6 =  "'search'=>::sql{'SELECT * FROM psa '},::department{$departMatch},::datarange{$DateRange}."
        PSASearchProcessor.render(search6)
        val res = PSASearchProcessor.getPSA()
        while (res!!.next()){
            val id = res.getString("id")
            println("FOUND ID::$id")
        }
    }

    fun writeW(Cells: LinkedList<Cell>, Rows: LinkedList<Row>, Arr: List<KeyValue>){
        var counter = 0
        Rows.forEach {
            for (i in 1..4){
                val cell = it.getCell(i)
                println("writing in $counter::$i")
                cell.setCellValue("Boom")
            }
        }
    }

    fun writeData(Data: String, Cells: LinkedList<Cell>, Rows: LinkedList<Row>, Arr: List<KeyValue>){
        val Row_ = Rows.get(0)
        val cell = Row_.getCell(0)
        cell.setCellValue(Data)
    }

    fun writeClient(Cells: LinkedList<Cell>, Rows: LinkedList<Row>, Arr: List<KeyValue>){
        val Row_ = Rows.get(0)
        val cell = Row_.getCell(5)
        cell.setCellValue("CLIENT")
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

    fun writeToDocumentPSA(Data: String, Position: Int, Sheet: Sheet, Arr: List<KeyValue> ): Int{   //KeyValue: MetalName: Weigth
        var Rows = LinkedList<Row>()
        var Cells = LinkedList<Cell>()
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
        println("ROWS SIZE:: ${Rows.size}")
        writeW(Cells, Rows, Arr)
        writeClient(Cells, Rows, Arr)
        writeData(Data, Cells, Rows, Arr)
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