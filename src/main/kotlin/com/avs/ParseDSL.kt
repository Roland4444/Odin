package com.avs
import abstractions.DSLBNF.Atom
import abstractions.DSLBNF.Expression
import abstractions.DSLRole
import abstractions.Role
import se.roland.util.Checker
import java.io.Serializable
class ParseDSL : Serializable {
    val checker = Checker()
    fun getDSLRulesfromString(input: String): DSLRole? {
        val objectName: String = input.substring(input.indexOf("'")+1, input.lastIndexOf("'"))
        println("""Loading rules for object <$objectName>""")
        return DSLRole(objectName, parseRoles(input))
    }
    fun parseRole(input: String): Role? {
        if (input.indexOf("{") == -1) return null
        val rolename: String= input.substring(input.indexOf("::")+2, input.indexOf("{"))
        var params: String
        if (input.indexOf("{")<input.indexOf("}")-2)
            params = input.substring(input.indexOf("{")+1, input.indexOf("}")-1)
        else params=""
        if ((rolename.length == 0) || (rolename ==null)) return null;
        return Role(rolename, params, this)
    }
    fun parseRoles(input: String): List<Role>{
        var result: MutableList<Role>  = mutableListOf()
        var initialString = input
        var role: Role? = parseRole(initialString)
        while (role != null){
            result.add(role)
            initialString = initialString.substring(initialString.indexOf("}")+1)
            role  = parseRole(initialString)
        };
        return result
    }
    fun String.toSequence():String{
        return ToSequence(this)
    }
    fun ToSequence(input__: String):String{
        val input = input__.prepare()
        if (getType(input)==Atom.Tupple)
            return input.substring(1, input.length-1)
        return input
    }
    fun getType(input_:String): Atom {
        val input = input_.prepare()
        if (input.equals(""))
            return Atom.Empty
        if ((input.Head__() != "") && (input.Tail__()!=""))
            return Atom.Sequence
        if ((input[0]=='[') && (input[input.length-1]==']'))
            return Atom.Tupple
        if ((input.indexOf("'")>=0) && (input.indexOf(":")<0))
            return Atom.String
        if ((input.indexOf("'")>=0) && (input.indexOf(":")>0) && (input.Tail__()=="")) {
            val t = input.Tail__()
            val h = input.Head__()
            println("Tail $t")
            println("Head $h")
            return Atom.KeyValue
        }
        if  (checker.isnumber(input))
            return Atom.Number
        return Atom.None
    }
    fun getTypeExpression(input: String): Expression{
        if (input.length==0) return Expression.Empty
        if ((input.Head__()!="") && (input.Tail__()=="")) return Expression.One
        if (input.Tail__()!="") return Expression.Many
        return Expression.Empty
    }
    fun getList(input: String): List<String>{
        var lst = mutableListOf<String>()
        var head = input.Head__()
        var tail = input.Tail__()
        while ((head!="") ){
            lst.add(head)
            head = tail.Head__()
            tail = tail.Tail__()
        }
        return lst
    }
    fun Atom(input:  String): Any{
        val type = getType(input)
        var map = mutableMapOf<String, Any>()
        var lst = mutableListOf<Any>()
        when (type){
            Atom.String->return (input.replace("'",""))
            Atom.Number->{
                if (!input.contains__("."))
                    return input.toInt()
                return input.toFloat()
            }
            Atom.KeyValue->{
                val key = Atom(getKey_(input)).toString()
                val value = Atom(getValue_(input))
                println("key=>$key,  value =>$value")
                if (value != null) {
                    map.put(key, value)
                }
                return map
            }
            Atom.Sequence->{
                val lst2 = getList(input)
                lst2.forEach { a -> lst.add(Atom(a)) }
                return lst
            }
            Atom.Tupple->{
                return Atom(input.toSequence())
            }
        }
        return ""
    }
    fun String.contains__(s: String): Boolean {
        if (this.indexOf(s)>=0)
            return true;
        return false;
    }
    fun getKey_(input: String): String{
        return input.getKey()
    }
    fun getValue_(input: String): String{
        return input.getValue()
    }
    fun String.getValue(): String{
        val index = this.indexOf(":");
        return this.substring(index+1, this.length).prepare()
    }
    fun prepare_(input: String):String{
        return input.prepare()
    }
    fun String.prepare():String{
        println(this)
        var buffer = StringBuilder()
        var appendWhite = false
        var currentString = this
        if (currentString.indexOf("'")<0)
            return currentString.replace(" ","")
        for ( i in 0..this.length-1){
            if ((this[i]==" "[0]) && !appendWhite)
                continue
            if ((this[i]=="'"[0]) && !appendWhite)
                appendWhite = true
            else if ((this[i]=="'"[0]) && appendWhite)
                appendWhite = false
            buffer.append(this[i])
            }
            return buffer.toString()
    }
    fun loadcolons(input: String): List<Int>{
        var colonbuff = mutableListOf<Int>()
        for (i in 0..input.length-1)
            if (input[i]==',')
                colonbuff.add(i)
        return colonbuff
    }

    fun Head(input: String): String{
        val p = getnumberopencolon(input)
        if (p>0)
            return input.substring(0, p)
        return input
    }

    fun Tail(input: String): String{
        val p =getnumberopencolon(input)
        if (p>0)
            return input.substring(p+1, input.length)
        return ""
    }

    fun String.Tail__(): String{
        return Tail(this)
    }

    fun String.Head__(): String{
        return Head(this)
    }

    fun getnumberopencolon(input:String):Int{
        println(input)
        val colonbuf = loadcolons(input)
        if (colonbuf.size<=0)
            return -1
        colonbuf.forEach { a->
            var index = a
            var closetupple = 0
            var opentupple = 0
            while (index>=0){
                if (input[index]==']'){
                    closetupple++
                }
                if (input[index]=='['){
                    opentupple++
                }
                index--
            }
            if (opentupple==closetupple)
                return  a
        }
        return -1
    }

    fun opencolon(input:String):Boolean{
        println(input)
        val colonbuf = loadcolons(input)
        if (colonbuf.size<=0)
            return false
        colonbuf.forEach { a->
            var index = a
            var closetupple = 0
            var opentupple = 0
            while (index>=0){
                if (input[index]==']'){
                    closetupple++
                }
                if (input[index]=='['){
                    opentupple++
                }
                index--
            }
            if (opentupple==closetupple)
                return  true
        }
        return false
    }
    fun String.getKey(): String{
        val index = this.indexOf(":");
        val key = this.substring(0, index).replace(" ","")
        return key
    }
}