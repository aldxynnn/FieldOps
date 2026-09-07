package com.example.fieldops

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.example.fieldops.data.model.demoWorkOrders
import com.example.fieldops.ui.theme.FieldOpsTheme
import com.example.fieldops.ui.workorder.WorkOrdersScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class WorkOrdersScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun workOrdersScreenDisplaysWorkOrders() {
        composeTestRule.setContent {
            FieldOpsTheme {
                WorkOrdersScreen(
                    workOrders = demoWorkOrders,
                    onWorkOrderClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Work Orders")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("WO-001234")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("PT. Maju Bersama")
            .assertIsDisplayed()
    }

    @Test
    fun searchFindsMatchingWorkOrder() {
        composeTestRule.setContent {
            FieldOpsTheme {
                WorkOrdersScreen(
                    workOrders = demoWorkOrders,
                    onWorkOrderClick = {}
                )
            }
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("WO-001234")

        composeTestRule
            .onAllNodesWithText("WO-001234")
            .assertCountEquals(2)

        composeTestRule
            .onNodeWithText("PT. Maju Bersama")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("WO-001230")
            .assertDoesNotExist()
    }

    @Test
    fun pendingFilterShowsPendingWorkOrders() {
        composeTestRule.setContent {
            FieldOpsTheme {
                WorkOrdersScreen(
                    workOrders = demoWorkOrders,
                    onWorkOrderClick = {}
                )
            }
        }

        composeTestRule
            .onNodeWithText("Pending")
            .performClick()

        composeTestRule
            .onNodeWithText("1 Work Order")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("WO-001230")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("PT. Mega Karya")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("WO-001234")
            .assertDoesNotExist()
    }

    @Test
    fun searchWithUnknownKeywordShowsEmptyState() {
        composeTestRule.setContent {
            FieldOpsTheme {
                WorkOrdersScreen(
                    workOrders = demoWorkOrders,
                    onWorkOrderClick = {}
                )
            }
        }

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("TIDAK-ADA-WORK-ORDER")

        composeTestRule
            .onNodeWithText(
                "Work Order tidak ditemukan"
            )
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText(
                "Coba ubah kata kunci atau filter Anda."
            )
            .assertIsDisplayed()
    }

    @Test
    fun clickingWorkOrderCallsCallback() {
        var clickedWorkOrderId: String? = null

        composeTestRule.setContent {
            FieldOpsTheme {
                WorkOrdersScreen(
                    workOrders = demoWorkOrders,
                    onWorkOrderClick = {
                        clickedWorkOrderId = it.id
                    }
                )
            }
        }

        composeTestRule
            .onNodeWithText("WO-001234")
            .performClick()

        assertEquals(
            "WO-001234",
            clickedWorkOrderId
        )
    }
}