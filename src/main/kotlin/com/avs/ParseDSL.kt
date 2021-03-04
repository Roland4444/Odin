package com.avs
import abstractions.DSLBNF.Atom
import abstractions.DSLRole
import abstractions.Role
import se.roland.util.Checker
class ParseDSL {
    val checker = Checker()
    fun getDSLRulesfromString(input: String): DSLRole? {
        val objectName: String = input.substring(input.indexOf("'")+1, input.lastIndexOf("'"))
        println("""Loading rules for object <$objectName>""")
        return DSLRole(objectName, parseRoles(input))
    }
    fun parseRole(input: String): Role? {
        if (input.indexOf("{") == -1) return null
        val rolename: String= input.substring(input.indexOf("::")+2, input.indexOf("{"))
        if ((rolename.length == 0) || (rolename ==null)) return null;
        return Role(rolename)
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
    fun parseParams(input: String): List<Any>{
        var result: MutableList<Any>  = mutableListOf()
        return result
    }

    fun getAtoms(input: String): List<Any>{
        var result: MutableList<Any>  = mutableListOf()
        var workingstring = input
        while (workingstring.length>0) {
            val firstdelim = input.indexOf(',');
            workingstring = workingstring.substring(0, firstdelim)
            result.add(workingstring)
        }
        return result
    }

    fun getType(input:String): Atom {
        if (input.equals("")) return Atom.Empty
        if ((input.indexOf("'")>=0) && (input.indexOf(":")<0)) return Atom.String
        if ((input.indexOf("'")>=0) && (input.indexOf(":")>0)) return Atom.KeyValue
        if  (checker.isnumber(input)) return Atom.Number
        if (input.indexOf("[")>=0 && input.indexOf("]")>=0) return Atom.Tupple
        return Atom.None
    }

    fun getTupple(input: String): List<Any>{
        var result = mutableListOf<Any>()
        return result
    }

    fun getAtom(input: String): Any {
        val type = getType(input)
        var map = mutableMapOf<String, Any>()
        when (type){
            Atom.String->return (input.replace("'",""))
            Atom.Number->{
                if (!input.contains__("."))
                    return input.toInt()
                return input.toFloat()
            }
            Atom.KeyValue->{
                val key = getAtom(getKey_(input)).toString()
                val value = getAtom(getValue_(input))
                println("key=>$key,  value =>$value")
                if (value != null) {
                    map.put(key, value)
                }
                return map
            }
          //  Atom.Tupple->

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
        return this.substring(index+1, this.length).removeWhites()
    }

    fun removeWhites_(input: String):String{
        return input.removeWhites()
    }

    fun String.removeWhites():String{
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


    fun String.getKey(): String{
        val index = this.indexOf(":");
        val key = this.substring(0, index).replace(" ","")
        return key
    }

    fun processAtom(input: String): Any{
        return 2;
    }

}