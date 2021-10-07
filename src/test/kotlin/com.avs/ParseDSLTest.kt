package com.avs

import abstractions.DSLBNF.Atom
import abstractions.DSLBNF.Expression
import abstractions.DSLRole
import abstractions.KeyValue
import abstractions.Role
import javassist.CtMethod.ConstParameter.string
import org.junit.Test
import kotlin.test.assertEquals


internal class ParseDSLTest {
    val input: String = """'requests' => ::read{}, ::write{}, ::create{}."""
    val inputwithparam: String = """'requests' => ::read{'tupple':["a","b","c"]}, ::write{<"load":12,"pay":40>}, ::create{<"number":""$90"","metal":["алюминий","сталь","никель"]>}."""
    val param: String = "12,'Добрый день', 'таблицы':['касса','склад','приход'], '12 декабря'";
    val simple = "12,'Добрый день'"
    val parser = ParseDSL();
    @Test
    fun getDSLRulestoObject() {
        val readRole: Role = Role("read","", parser)
        val writeRole: Role = Role("write","", parser)
        val createRole: Role = Role("create","", parser)
        var Roles: MutableList<Role> = mutableListOf(readRole, writeRole, createRole)
        var ObjectRules : DSLRole = DSLRole("requests", Roles)
        assertEquals(ObjectRules, parser.getDSLRulesfromString(input))
    }
    @Test
    fun testbstr(){
        val r = "substring".substring(2, 4)
        assertEquals("bs", r)

    }


    @Test
    fun testParseRole() {
        val readRole: Role = Role("read","", parser)
        assertEquals(readRole.toString(), parser.parseRole(input).toString())
    }

    @Test
    fun parsewithparams(){
        val etalon= mutableListOf<Any>();
        etalon.add(12);
        etalon.add("Добрый день");

        val param: String = "[12,'Добрый день', 'таблицы':['касса','склад','приход'], '12 декабря']";
        var list = mutableListOf("касса","склад","приход");
        val hashmap = KeyValue("таблицы", list);
        etalon.add(hashmap);
        etalon.add("12 декабря");
        assertEquals(etalon.toString(), parser.Atom(param).toString())
    }
    @Test
    fun testGetAtoms() {
        val etalon= mutableListOf<Any>();
        etalon.add("12")
        etalon.add("Добрый день")
        assertEquals(etalon.toString(), parser.Atom(simple).toString() )
    }
    @Test
    fun testGetAtom() {
        val keyvalue= KeyValue("key",12)
        //keyvalue.put("key",12)
        assertEquals("", parser.Atom(""))
        assertEquals(true, "121212".contains("12"))
        assertEquals(12,parser.Atom("12"))
        assertEquals("xyz",parser.Atom("'xyz'"))
        assertEquals(keyvalue, parser.Atom("'key':12"))
    }
    @Test
    fun testGetKey() {
        val example = "'таблица':12";
        assertEquals("'таблица'", parser.getKey_(example))
    }
    @Test
    fun testClearString() {
        val initial = "12, 'Добрый день',  122";
        val etalon = "12,'Добрый день',122";
        val param2: String = "12 ,  '    Добрый день', 'табли   цы': [  '  к  а  с с а',' скл ад',     'приход'], '12 декабря'";
        val etalon2: String = "12,'    Добрый день','табли   цы':['  к  а  с с а',' скл ад','приход'],'12 декабря'";
        assertEquals(etalon, parser.prepare_(initial))
        assertEquals(etalon2, parser.prepare_(param2))
    }
    @Test
    fun countStringdelim(){
        val etalon = "12,'Добрый день',122";
        val initial = "'12':[12,12,56]";
        val etalon2 = "[12,12,56]";
        assertEquals(2, parser.countStringDelims(etalon))
        assertEquals(2, parser.countStringDelims(initial))
        assertEquals(0, parser.countStringDelims(etalon2))
    }
    @Test
    fun extendedtest(){
        val initial = "'urldb':jdbc:mysql://192.168.0.121:3306/psa"
        assertEquals(Atom.KeyValue, parser.getType(initial))
        assertEquals("'urldb'", parser.getKey_(initial))
        assertEquals("jdbc:mysql://192.168.0.121:3306/psa", parser.getValue_(initial))
        assertEquals(Atom.None, parser.getType(parser.getValue_(initial)))

    }

    @Test
    fun testGetValue_() {
        val initial = "'12':[12,12,56]";
        val etalon = "[12,12,56]";
        assertEquals(etalon,parser.getValue_(initial))

        var arr = mutableListOf(12,12,56)
        val etalonMap = KeyValue("12", arr)
        assertEquals(etalonMap, parser.Atom(initial))
    }

    @Test
    fun testatomint() {
        val initial = "1";
        val etalon =1;
        assertEquals(etalon,parser.Atom(initial))
    }

    @Test
    fun nestedTUpple(){
        val initial = "'12':[[12,12],12]";
        assertEquals(initial, parser.Head(initial))
        assertEquals("", parser.Tail(initial))
        println(parser.getType(""))
        assertEquals(Atom.KeyValue, parser.getType(initial))
    }

    @Test
    fun getnumbercolontest(){
        val init = "'12':[[12,12],12]"
        assertEquals(false, parser.opencolon(init))
    }

    @Test
    fun testgettype(){
        val initial = "'12':[[12,12],12]";
        val initial2 = "[[12,12],12]";
        val initial3 = "'12':[[12,12],12],'12':12";
        assertEquals(Atom.KeyValue, parser.getType(initial))
        assertEquals(Atom.Tupple, parser.getType(initial2))
        assertEquals(Atom.Sequence, parser.getType(initial3))
        assertEquals(Expression.Many, parser.getTypeExpression(initial3))
        assertEquals(Expression.One, parser.getTypeExpression(initial2))
        print(parser.Head(initial2))
        print("tail>>"+parser.Tail(initial3))
    }
    @Test
    fun convertTuppletoSeq(){
        val initial2 = "[[12,12],12]";
        val initial3= "['12':[12,12],'12':22,44]";
        assertEquals("[12,12],12", parser.ToSequence(initial2))
        assertEquals(Atom.Tupple, parser.getType(initial3))
        assertEquals("'12':[12,12],'12':22,44", parser.ToSequence(initial3))
    }
    @Test
    fun nestedtupple() {
        val initial = "'12':[[12,12],12]";
        val initial3 = "'12':[[12,12],12],'12':12";
        val tupple = "[$initial3]"
        val etalonMap = mutableMapOf<String, Any>()
        var arr = mutableListOf(12,12)
        var arr2 = mutableListOf<Any>()
        arr2.add(arr)
        arr2.add(12)
        etalonMap.put("12", arr2)
        assertEquals("", parser.Tail(initial))

        assertEquals(Atom.KeyValue, parser.getType(initial))
        assertEquals(Atom.Tupple, parser.getType(parser.getValue_(initial)))
        assertEquals("'12':[[12,12],12]", parser.Head(initial))

        assertEquals(Atom.KeyValue, parser.getType(parser.Head(initial)))
        assertEquals(Expression.One, parser.getTypeExpression(initial))
        assertEquals("'12':[[12,12],12]", parser.Head(initial3))
        assertEquals("'12':12", parser.Tail(initial3))
        assertEquals("", parser.Tail(tupple))
        assertEquals("[$initial3]", parser.Head(tupple))

    }

    @Test
    fun testhead(){
        var initial = "12,'aaaa','f':56";
        parser.Head(initial);
        assertEquals("12", parser.Head(initial));

    }

    @Test
    fun testSequencetoList(){
        val initial = "12,'5','f',56";
        var lst = mutableListOf<String>()
        lst.add("12")
        lst.add("'5'")
        lst.add("'f'")
        lst.add("56")
        val LST = parser.getList(initial)
        LST.forEach { a -> println(a) }
        assertEquals(lst, parser.getList(initial))
    }

    @Test
    fun testSequence(){
        val initial = "12,'aaaa','f':56";
        val initial2 = "'12','aaaa','f':56";
        assertEquals(Atom.Sequence, parser.getType(initial))
        assertEquals(Atom.Number, parser.getType(parser.Head(initial)))
        assertEquals(Atom.String, parser.getType(parser.Head(initial2)))
    }
    @Test
    fun testGetTupple() {
        val initial = "[12,'aaaa','f':56]";
        var etalon = mutableListOf<Any>()
        etalon.add(12)
        etalon.add("aaaa")
        var keyvalue= KeyValue("f",56)
        etalon.add(keyvalue)
        assertEquals(etalon, parser.Atom(initial))
    }

    fun testCountStringDelims() {}
    @Test
    fun testRemoveRolefromStringDSL() {

    val InitDB = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val RemoveRole = "psa";
    val RemoveRole2 = "enabled";
    val RemoveRole3 = "db";
    val Etalon = "'psadb'=>::db{jdbc:mysql://192.168.0.121:3306/psa},::enabled{'true'}."
    val Etalon2 = "'psadb'=>::psa{'login':'root','pass':'123'},::db{jdbc:mysql://192.168.0.121:3306/psa}."
    val Etalon3 = "'psadb'=>::psa{'login':'root','pass':'123'},::enabled{'true'}."
    val Result = parser.removeRolefromStringDSL(InitDB, RemoveRole)
    val Result2 = parser.removeRolefromStringDSL(InitDB, RemoveRole2)
    val Result3 = parser.removeRolefromStringDSL(InitDB, RemoveRole3)
    assertEquals(Etalon, Result)
    assertEquals(Etalon2, Result2)
    assertEquals(Etalon3, Result3)
    val inputwithparam__: String = """'requests'=>::read{'tupple':["a","b","c"]},::write{<"load":12,"pay":40>},::create{<"number":""$90"","metal":["алюминий","сталь","никель"]>}."""
    val Etalon4 ="""'requests'=>::read{'tupple':["a","b","c"]},::create{<"number":""$90"","metal":["алюминий","сталь","никель"]>}."""
    val Remove = "write"
    assertEquals(Etalon4, parser.removeRolefromStringDSL(inputwithparam__, Remove))




    }

    @Test
    fun testGetRawDSLForRole() {
        val str =  """'requests' => ::read{'tupple':["a","b","c"]}, ::write{<"load":12,"pay":40>}, ::create{<"number":""$90"","metal":["алюминий","сталь","никель"]>}."""
        val rawdsl = parser.getRawDSLForRole(str, "read")
        assertEquals("""'tupple':["a","b","c"]""", rawdsl)

    }


}




