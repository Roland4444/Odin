package com.avs

import abstractions.DSLBNF.Atom
import abstractions.DSLBNF.Expression
import abstractions.DSLRole
import abstractions.Role
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.collections.List as List1

internal class ParseDSLTest {
    val input: String = """'requests' => ::read{}, ::write{}, ::create{}."""
    val inputwithparam: String = """'requests' => ::read{'tupple':["a","b","c"]}, ::write{<"load":12,"pay":40>}, ::create{<"number":""$90"","metal":["алюминий","сталь","никель"]>}."""
    val param: String = "12,'Добрый день', 'таблицы':['касса','склад','приход'], '12 декабря'";
    val simple = "12, 'Добрый день'"
    val parser = ParseDSL();
    @Test
    fun getDSLRulestoObject() {
        val readRole: Role = Role("read")
        val writeRole: Role = Role("write")
        val createRole: Role = Role("create")
        var Roles: MutableList<Role> = mutableListOf(readRole, writeRole, createRole)
        var ObjectRules : DSLRole = DSLRole("requests", Roles)
        assertEquals(ObjectRules, parser.getDSLRulesfromString(input))
    }
    @Test
    fun testParseRole() {
        val readRole: Role = Role("read")
        assertEquals(readRole.toString(), parser.parseRole(input).toString())
    }

    @Test
    fun parsewithparams(){
        val etalon= mutableListOf<Any>();
        etalon.add(12);
        etalon.add("Добрый день");
        val hashmap = mutableMapOf<String, List1<Any>>();
        val param: String = "[12,'Добрый день', 'таблицы':['касса','склад','приход'], '12 декабря']";

        var list = mutableListOf("касса","склад","приход");
        hashmap.put("таблицы", list);
        etalon.add(hashmap);
        etalon.add("12 декабря");
        assertEquals(etalon.size, parser.getTupple(param).size)
    }
    fun testParseParam() {}
  //  @Test
    fun testGetAtoms() {
        val etalon= mutableListOf<Any>();
        etalon.add("12")
        etalon.add("'Добрый день'")
        assertEquals(parser.getAtoms(simple), etalon)
    }
    @Test
    fun testGetAtom() {
        val keyvalue= mutableMapOf<String, Any>()
        keyvalue.put("key",12)
        assertEquals("", parser.getAtom(""))
        assertEquals(true, "121212".contains("12"))
        assertEquals(12,parser.getAtom("12"))
        assertEquals("xyz",parser.getAtom("'xyz'"))
        assertEquals(keyvalue, parser.getAtom("'key':12"))
    }
    @Test
    fun testGetKey() {
        val example = "'таблица':12";
        assertEquals("'таблица'", parser.getKey_(example))
    }
    @Test
    fun testRemoveWhites() {
        val initial = "12, 'Добрый день',  122";
        val etalon = "12,'Добрый день',122";
        val param2: String = "12 ,  '    Добрый день', 'табли   цы': [  '  к  а  с с а',' скл ад',     'приход'], '12 декабря'";
        val etalon2: String = "12,'    Добрый день','табли   цы':['  к  а  с с а',' скл ад','приход'],'12 декабря'";
        assertEquals(etalon, parser.removeWhites_(initial))
        assertEquals(etalon2, parser.removeWhites_(param2))
    }
    @Test
    fun testGetValue_() {
        val initial = "'12':[12,12,56]";
        val etalon = "[12,12,56]";
        assertEquals(etalon,parser.getValue_(initial))
        val etalonMap = mutableMapOf<String, Any>()
        var arr = mutableListOf(12,12,56)
        etalonMap.put("12", arr)
        assertEquals(etalonMap, parser.getAtom(initial))
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
        assertEquals(Atom.KeyValue, parser.getType(initial))
    }


    @Test
    fun nestedtupple() {
        val initial = "'12':[[12,12],12]";
        val initial2 = "['12':[[12,12],'12':22],44]";
        val etalonMap = mutableMapOf<String, Any>()
        var arr = mutableListOf(12,12)
        var arr2 = mutableListOf<Any>()
        arr2.add(arr)
        arr2.add(12)
        etalonMap.put("12", arr2)
        assertEquals("", parser.Tail(initial))

    ////    assertEquals(etalonMap, parser.getAtom(initial))
        assertEquals(Atom.KeyValue, parser.getType(initial))
        assertEquals(Atom.Tupple, parser.getType(parser.getValue_(initial)))
        assertEquals(Atom.Tupple, parser.getType(parser.getValue_(initial2)))
  //      assertEquals("[[12,12],12]",parser.getValue_(initial))
  //      assertEquals("[[12,12],'12':22]",parser.getValue_(initial2))
        assertEquals("'12':[[12,12],12]", parser.Head(initial))   ////<====uncorrect;  need process open braces

        assertEquals(Atom.KeyValue, parser.getType(parser.Head(initial)))
        assertEquals(Expression.One, parser.getTypeExpression(initial))
    }
    @Test
    fun testGetTupple() {
        val initial = "[12,'aaaa','f':56]";
        var etalon = mutableListOf<Any>()
        etalon.add(12)
        etalon.add("aaaa")
        var keyvalue= mutableMapOf<String, Any>()
        keyvalue.put("f",56)
        etalon.add(keyvalue)
        assertEquals(etalon, parser.getTupple(initial))
    }
    @Test
    fun testGetTuppleStr() {
        val initial = "[12,'aaaa','f':56]";
        val initial2 = "['12   ','aaaa','f'   :      56]";
        var etalon = mutableListOf<String>()
        var etalon2 = mutableListOf<String>()
        etalon.add("12")
        etalon.add("'aaaa'")
        etalon.add("'f':56")
        etalon2.add("'12   '");
        etalon2.add("'aaaa'");
        etalon2.add("'f':56")
        assertEquals(etalon, parser.getTuppleStr(initial))
        assertEquals(etalon2, parser.getTuppleStr(initial2))

    }


    @Test
    fun testGetTuppletreeStr() {
        val initial = "[12,'aaaa',[[[12]]]]]";
        var etalon = mutableListOf<String>()
        etalon.add("12")
        etalon.add("'aaaa'")
        etalon.add("'f':56")
        assertEquals(etalon, parser.getTuppleStr(initial))
    }

    @Test
    fun testhead(){
        val initial = "[12], 57";
        val initial2 ="12,33,44";
        val initial3 = "'12':12, '44'"
        val initial4 = "[], '44'"
        val initial5 = "'12':[],'12':12, '44'"
        val initial6 = "'12', 12,55,55"
        val tailtest = "'12':[12],12:[[12,12]]"
        val tailtest2 = "['12':12]"
        assertEquals("[12]", parser.head_(initial))
        assertEquals("12", parser.head_(initial2))
        assertEquals("'12':12", parser.head_(initial3))
        assertEquals("[]", parser.head_(initial4))
        assertEquals("'12':[]", parser.head_(initial5))

        assertEquals("57", parser.tail_(initial))
        assertEquals("33,44", parser.tail_(initial2))
        assertEquals("'44'", parser.tail_(initial3))
        assertEquals("'44'", parser.tail_(initial4))
        assertEquals("'12':12,'44'", parser.tail_(initial5))
        var tail2 = parser.tail_(initial6)
        var tail3 = parser.tail_(tail2)
        assertEquals("55,55", tail3)
        assertEquals("'12'", parser.head_(initial6))
        assertEquals("12:[[12,12]]", parser.tail_(tailtest))

    }



}




