package com.project.androidtodotesting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Rule
import org.junit.Test

class TodoScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addTask_displaysTaskInList() {
        composeTestRule
            .onNodeWithTag("task_input")
            .performTextInput("Test Aufgabe")

        composeTestRule
            .onNodeWithTag("add_button")
            .performClick()

        composeTestRule
            .onNodeWithText("Test Aufgabe")
            .assertIsDisplayed()
    }

    @Test
    fun emptyInput_showsErrorMessage() {
        composeTestRule
            .onNodeWithTag("add_button")
            .performClick()

        composeTestRule
            .onNodeWithTag("error_message")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Bitte Aufgabe eingeben")
            .assertIsDisplayed()
    }

    @Test
    fun deleteTask_removesTaskFromList() {
        composeTestRule
            .onNodeWithTag("task_input")
            .performTextInput("Aufgabe löschen")

        composeTestRule
            .onNodeWithTag("add_button")
            .performClick()

        composeTestRule
            .onNodeWithText("Aufgabe löschen")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithTag("delete_button_1")
            .performClick()

        composeTestRule
            .onNodeWithTag("empty_state")
            .assertIsDisplayed()
    }
}