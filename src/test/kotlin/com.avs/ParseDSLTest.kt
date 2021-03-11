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
    fun testParseRole() {
        val readRole: Role = Role("read","", parser)
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
        val keyvalue= mutableMapOf<String, Any>()
        keyvalue.put("key",12)
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
    fun testGetValue_() {
        val initial = "'12':[12,12,56]";
        val etalon = "[12,12,56]";
        assertEquals(etalon,parser.getValue_(initial))
        val etalonMap = mutableMapOf<String, Any>()
        var arr = mutableListOf(12,12,56)
        etalonMap.put("12", arr)
        assertEquals(etalonMap, parser.Atom(initial))
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
    fun testSequencetoList(){
        val initial = "12,'5','f',56";
        var lst = mutableListOf<String>()
        lst.add("12")
        lst.add("'5'")
        lst.add("'f'")
        lst.add("56")
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
        var keyvalue= mutableMapOf<String, Any>()
        keyvalue.put("f",56)
        etalon.add(keyvalue)
        assertEquals(etalon, parser.Atom(initial))
    }


}




