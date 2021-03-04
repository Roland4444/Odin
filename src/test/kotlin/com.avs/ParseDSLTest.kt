package com.avs

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
        val param: String = "12,'Добрый день', 'таблицы':['касса','склад','приход'], '12 декабря'";

        var list = mutableListOf("касса","склад","приход");
        hashmap.put("таблицы", list);
        etalon.add(hashmap);
        etalon.add("12 декабря");
        assertEquals(etalon.size, parser.parseParams(param))
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

    fun testContains() {}

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
    }

    @Test
    fun testGetTupple() {
        val initial = "[12,'aaaa','f':56]";
        var etalon = mutableListOf<Any>()
        etalon.add(12.0.toFloat())

    }
}




