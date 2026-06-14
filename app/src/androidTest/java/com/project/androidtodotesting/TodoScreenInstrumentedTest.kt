package com.project.androidtodotesting

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test

class TodoScreenInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val testEmail = "test1@example.com"
    private val testPassword = "test1234"

    @Test
    fun loginScreen_isDisplayed() {
        composeTestRule.runOnIdle {
            FirebaseAuth.getInstance().signOut()
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("login_screen").assertIsDisplayed()
        composeTestRule.onNodeWithTag("email_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("password_input").assertIsDisplayed()
        composeTestRule.onNodeWithTag("login_button").assertIsDisplayed()
        composeTestRule.onNodeWithTag("register_button").assertIsDisplayed()
    }

    @Test
    fun loginAndAddTask_displaysTaskInList() {
        val taskText = "Instrumented Test Aufgabe ${System.currentTimeMillis()}"

        composeTestRule.runOnIdle {
            FirebaseAuth.getInstance().signOut()
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("email_input").performTextInput(testEmail)
        composeTestRule.onNodeWithTag("password_input").performTextInput(testPassword)
        composeTestRule.onNodeWithTag("login_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithTag("todo_screen")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("task_input").performTextInput(taskText)
        composeTestRule.onNodeWithTag("add_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithText(taskText)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText(taskText).assertIsDisplayed()
    }

    @Test
    fun emptyInput_showsErrorMessageAfterLogin() {
        composeTestRule.runOnIdle {
            FirebaseAuth.getInstance().signOut()
        }

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("login_screen")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("email_input").performTextInput(testEmail)
        composeTestRule.onNodeWithTag("password_input").performTextInput(testPassword)
        composeTestRule.onNodeWithTag("login_button").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule
                .onAllNodesWithTag("todo_screen")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithTag("add_button").performClick()

        composeTestRule.onNodeWithTag("error_message").assertIsDisplayed()
        composeTestRule.onNodeWithText("Bitte Aufgabe eingeben").assertIsDisplayed()
    }
}