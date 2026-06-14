package com.project.androidtodotesting

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoValidatorTest {

    @Test
    fun validTask_returnsTrue() {
        val result = TodoValidator.isValidTask("Einkaufen")
        assertTrue(result)
    }

    @Test
    fun emptyTask_returnsFalse() {
        val result = TodoValidator.isValidTask("")
        assertFalse(result)
    }

    @Test
    fun blankTask_returnsFalse() {
        val result = TodoValidator.isValidTask("   ")
        assertFalse(result)
    }
}