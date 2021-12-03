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
import org.apache.poi.xssf.usermodel.XSSFFont
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.sql.ResultSet
import java.util.*


/////"'eco'=>::generatefor{'quarter':4,'year':2019,'department':6},::enabled{'false'}."
typealias MapUpdater = (Map: Map<String, Float>, Value:Float) -> Unit
class EcoProcessor:  DSLProcessor() {
    val HeaderLst= mapOf(
                    0 to "Дата приема лома",
                    1 to "Наименование отхода",
                    2 to "ФККО",
                    3 to "Класс опасности",
                    4 to "Количество принятых отходов",
                    5 to "Клиент"
    )
    val HeaderLstSummary= mapOf(
        0 to "№ строки",
        1 to "Наименование вида отхода",
        2 to "Код по ФККО",
        3 to "Класс опасности отхода",
        4 to "Наличие отходов(кг)",
        5 to "Клиент"
    )
    val QuarterMap = mutableMapOf<Int, String>(1 to "'year-01-01':'year-03-31'",
                           2 to "'year-04-01':'year-06-30'",
                           3 to "'year-07-01':'year-9-30'",
                           4 to "'year-10-01':'year-12-31'")
    var TotalMap = mutableMapOf<String, Float>()
    var Book: Workbook = HSSFWorkbook()

    var borderStyle: CellStyle = Book.createCellStyle()
    var BoldTextStyle: CellStyle = Book.createCellStyle()
    var _CursiveTextStyle: CellStyle = Book.createCellStyle()
    var Filename: String = "temp.xlsx"
    var quarter = 0
    var year = 0
    var department: Any = ""
    var DateRange: String = ""
    var CacheMetalInfo: HashMap<String, LinkedList<String>> = HashMap()
    lateinit var PSASearchProcessor: PSASearchProcessor
    override fun render(DSL: String): Any {
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
        DateRange = QuarterMap.get(quarter)!!.replace("year", year.toString(), true)
        Book = HSSFWorkbook()
        borderStyle = Book.createCellStyle()
        borderStyle.borderBottom = CellStyle.BORDER_THIN
        borderStyle.borderLeft = CellStyle.BORDER_THIN
        borderStyle.borderRight = CellStyle.BORDER_THIN
        borderStyle.borderTop = CellStyle.BORDER_THIN
        borderStyle.alignment = CellStyle.ALIGN_CENTER

        val Boldfont = Book.createFont()
        Boldfont.fontHeightInPoints = 13.toShort()
        Boldfont.fontName = "Arial"
        Boldfont.color = IndexedColors.WHITE.getIndex()
        Boldfont.bold = true
        Boldfont.italic = false

        val _CursiveFont = Book.createFont()
        Boldfont.fontHeightInPoints = 10.toShort()
        Boldfont.fontName = "Arial"
        Boldfont.color = IndexedColors.WHITE.getIndex()
        Boldfont.bold = true
        Boldfont.italic = true



        _CursiveTextStyle.setFont(_CursiveFont)

        BoldTextStyle.setFont(Boldfont)
        return "OK"
    }

    override fun parseRoles(DSL: String): List<Role> {
        return parser.parseRoles(DSL!!)
    }

    val quartermap: RoleHandler = {
        mapper.forEach { a ->
            if (a.key.Name == "quartermap") {
                val Lst = a.key.Param as MutableList<KeyValue>
                QuarterMap.clear()
                Lst.forEach {
                    QuarterMap.put(it.Key.toInt(), it.Value.toString().replace("/",":"))
                }
            }
        }
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

    fun writeHeader(Sheet: Sheet, SheetSummary: Sheet){
        val row: Row = Sheet.createRow(0)
        val row2: Row = SheetSummary.createRow(0)
        val borderStyle: CellStyle = Book.createCellStyle()
        for (i in 0..5) {
            val a = row.createCell(i)
            a.setCellValue(HeaderLst.get(i))
            a.cellStyle=borderStyle

            val a2 = row2.createCell(i)
            a2.setCellValue(HeaderLstSummary.get(i))
            a2.cellStyle=borderStyle
        }

    }

    fun writeResult(Sheet: Sheet){
        var Position = 1;
        TotalMap.forEach { t, u ->
            Position++
            val row = Sheet.createRow(Position)
            val cell0 = row.createCell(0)
            val cell1 = row.createCell(1)
            val cell2 = row.createCell(2)
            val cell3 = row.createCell(3)
            val cell4 = row.createCell(4)
            val cell5 = row.createCell(5)
            cell2.setCellValue(t.toString())
            val T: Double = (u.toDouble()/1000)
            cell4.setCellValue(u.toDouble())////"%.3f".format(T))
        }

    }

    fun clone(Source_FileName: String, Target_FileName: String, SheetName: String){
        val Source__ = XSSFWorkbook(FileInputStream(File(Source_FileName)))
        println("#sheet ${Source__.getNameIndex(SheetName)}")
        for (i in 0..2)
            println("#::"+Source__.getSheetName(i))
        var sheet_src = Source__.getSheet(SheetName)
        val Target = XSSFWorkbook()
        var sheet = Target.createSheet(SheetName)
        var counter = 0
        for (i in 0..35){
            val r = sheet.createRow(i)
            for (j in 0..60) {
                val cell_ = r.createCell(j)
                if (sheet_src.getRow(i)!=null)
                    if (sheet_src.getRow(i).getCell(j)!=null)
                        cell_.setCellValue(sheet_src.getRow(i).getCell(j).stringCellValue)
            }
        }

//        sheet_src.forEach { a->
//            run {
//                val r = sheet.createRow(counter)
//                counter++
//                var counter_cell = 0
//                a.forEach {
//                    val cell_ = r.createCell(counter_cell)
//                    if (a.getCell(counter_cell) != null) {
//                        cell_.setCellValue(a.getCell(counter_cell).stringCellValue)
//                        println("VALUE::${a.getCell(counter_cell).stringCellValue}")
//                    }
//                    println("COUNTER_CELL::$counter_cell  @counter =$counter   ")
//                    counter_cell++
//                }
//            }
//        }
        Target?.write(FileOutputStream(Target_FileName))
        Target?.close()

    }

    fun appendDescriptionLiost(){
        var DS = Book.createSheet("Description")

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
        var position = 1
        var psacounter = 0
        var Sheet = Book.createSheet()
        var SheetSummary = Book.createSheet("Summary")
        writeHeader(Sheet, SheetSummary)
        while (res!!.next()){
            val id = res.getString("id")
            val date = res.getString("date")
            val WBlock = genKeyValue(PSASearchProcessor.getWViaPSAId(id))
            val Client = res.getString("client")
            println("PROCESS PSA#${psacounter++}")
            position = writeToDocumentPSA(date, Client, position, Sheet, WBlock)
        }
        writeResult(SheetSummary)
        finalizeBook()
    }

    fun mergingAreas(Position: Int, sheet: Sheet, Arr: List<KeyValue>){
        sheet.addMergedRegion(CellRangeAddress(Position, Position+Arr.size-1, 0, 0))
        sheet.addMergedRegion(CellRangeAddress(Position, Position+Arr.size-1, 5, 5))
    }


    //desc, fkko, dangerclass
    fun getMetalInfo(Name: String): LinkedList<String> {
        var Res = LinkedList<String>()
        var prepared =  PSASearchProcessor.psaconnector.executor!!.conn.prepareStatement("SELECT * FROM `psa`.`metal` WHERE `title` = ?;")
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
        var i = 0
//        val r = Sheet.createRow(Position)
//        r.createCell(2).setCellValue("test")
        Arr.forEach {
            val metalId = it.Key
            val row: Row = Sheet.createRow(i+Position)
            val cell0 =  row.createCell(0)
            cell0.setCellValue(Data)
            CellUtil.setAlignment(cell0, Book, CellStyle.ALIGN_CENTER_SELECTION)
            val FKKO = CacheMetalInfo.get(metalId)?.get(1)
            val W: Float = (it.Value as String).toFloat()
            val get = TotalMap.get(FKKO)
            if (get != null){
                TotalMap.put(FKKO!!, get+W)
            }
            else
                TotalMap.put(FKKO!!, W)
            val cell1=row.createCell(1); cell1.setCellValue(CacheMetalInfo.get(metalId)?.get(0))
            val cell2=row.createCell(2); cell2.setCellValue(CacheMetalInfo.get(metalId)?.get(1))
            val cell3=row.createCell(3); cell3.setCellValue(CacheMetalInfo.get(metalId)?.get(2))
            val cell4=row.createCell(4); cell4.setCellValue(it.Value as String)
            val cell5 =  row.createCell(5)
            CellUtil.setAlignment(cell5, Book, CellStyle.ALIGN_CENTER_SELECTION)
            cell5.setCellValue(Client)
            cell0.cellStyle=borderStyle
            cell1.cellStyle=borderStyle
            cell2.cellStyle=borderStyle
            cell3.cellStyle=borderStyle
            cell4.cellStyle=borderStyle
            cell5.cellStyle=borderStyle
            i++
        }
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
            "quartermap" -> mapper.put(R, quartermap)
        }
    }
}