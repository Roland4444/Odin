package com.avs

import abstractions.DSLRole
import abstractions.Role
import org.junit.Test
import kotlin.test.assertEquals

internal class ParseDSLTest {
    val input: String = """'requests' => ::read{}, ::write{}, ::create{}."""
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
}


