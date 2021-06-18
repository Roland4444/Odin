package DSLGuided.requestsx.EcoProcessor

import DSLGuided.requestsx.DSLProcessor
import DSLGuided.requestsx.PSA.PSAConnector
import DSLGuided.requestsx.RoleHandler
import abstractions.KeyValue
import abstractions.Role
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.DataFormat
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.util.CellRangeAddress
import java.io.FileOutputStream
import java.util.*

/////"'eco'=>::generatefor{'quarter':4,'year':2019,'department':6},::enabled{'false'}."
class EcoProcessor:  DSLProcessor() {
    var quarter = 0
    var year = 0
    var department = -1
    lateinit var PSAConnector: PSAConnector
    override fun render(DSL: String): Any {
        parseRoles(DSL)
        loadRoles(parseRoles(DSL))
        mapper.forEach { it.value.invoke(it.key)  }
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
                        "department" -> department = it.Value as Int
                    }
                }
            }
        }
    }

//    fun createRows()

    fun writeToDocumentPSA(Data: String, Position: Int, Sheet: Sheet, Arr: List<KeyValue> ): Int{
//        val row: Row = Sheet.(Position)
//        sheet.autoSizeColumn(1)
//        val firstRow = 0
//        val lastRow = 255
//        val firstCol = 0
//        val lastCol = 255
//        sheet.addMergedRegion(CellRangeAddress(firstRow, lastRow, firstCol, lastCol))
//
//        val name = row.createCell(0)
//        name.setCellValue("John")
//
//        val birthdate = row.createCell(1)
//
//        val format: DataFormat = book.createDataFormat()
//        val dateStyle: CellStyle = book.createCellStyle()
//        dateStyle.dataFormat = format.getFormat("dd.mm.yyyy")
//        birthdate.cellStyle = dateStyle
//        // Нумерация лет начинается с 1900-го
//        // Нумерация лет начинается с 1900-го
//        birthdate.setCellValue(Date(110, 10, 10))
//
//        // Меняем размер столбца
//
//        // Записываем всё в файл
//
//        // Меняем размер столбца
//
//        // Записываем всё в файл
//        book.write(FileOutputStream(file))
//        book.close()
        return 0;
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