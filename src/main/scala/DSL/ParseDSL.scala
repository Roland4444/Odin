package DSL
import abstractions.DSLBNF.Atom
import abstractions.DSLBNF.Expression
import abstractions.DSLRole
import abstractions.KeyValue
import abstractions.Role

import se.roland.util.Checker
import java.io.Serializable
class ParseDSL  {
    val checker: Checker = Checker()

    def add(A: Int, B: Int): Int= A+ B

}