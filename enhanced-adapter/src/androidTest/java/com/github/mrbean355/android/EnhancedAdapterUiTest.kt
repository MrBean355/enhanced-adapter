package com.github.mrbean355.android

import android.content.Intent
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EnhancedAdapterUiTest {
    @get:Rule
    val activityRule = ActivityTestRule(TestActivity::class.java, false, false)

    @Test
    fun testThatItemsGetSortedAndDisplayed() {
        activityRule.launchActivity(null)

        onView(withText("a")).check(matches(isDisplayed()))
        onView(withText("b")).check(matches(isDisplayed()))
            .check(isCompletelyBelow(withText("a")))
        onView(withText("c")).check(matches(isDisplayed()))
            .check(isCompletelyBelow(withText("b")))
        onView(withText("d")).check(matches(isDisplayed()))
            .check(isCompletelyBelow(withText("c")))
        onView(withText("e")).check(matches(isDisplayed()))
            .check(isCompletelyBelow(withText("d")))
        onView(withText("f")).check(matches(isDisplayed()))
            .check(isCompletelyBelow(withText("e")))
    }

    @Test
    fun testThatSelectedItemsAreIndicated() {
        activityRule.launchActivity(Intent().putExtra(TestActivity.KEY_SELECTED_ITEM, "e"))

        onView(withText("a")).check(matches(isDisplayed()))
        onView(withText("b")).check(matches(isDisplayed()))
        onView(withText("c")).check(matches(isDisplayed()))
        onView(withText("d")).check(matches(isDisplayed()))
        onView(withText("e SELECTED")).check(matches(isDisplayed()))
        onView(withText("f")).check(matches(isDisplayed()))
    }

    @Test
    fun testThatClickingADeselectedItemSelectsIt() {
        activityRule.launchActivity(null)

        onView(withText("b"))
            .perform(click())

        onView(withText("a")).check(matches(isDisplayed()))
        onView(withText("b SELECTED")).check(matches(isDisplayed()))
        onView(withText("c")).check(matches(isDisplayed()))
        onView(withText("d")).check(matches(isDisplayed()))
        onView(withText("e")).check(matches(isDisplayed()))
        onView(withText("f")).check(matches(isDisplayed()))
    }

    @Test
    fun testThatClickingASelectedItemDeselectsIt() {
        activityRule.launchActivity(Intent().putExtra(TestActivity.KEY_SELECTED_ITEM, "b"))

        onView(withText("b SELECTED"))
            .perform(click())

        onView(withText("a")).check(matches(isDisplayed()))
        onView(withText("b")).check(matches(isDisplayed()))
        onView(withText("c")).check(matches(isDisplayed()))
        onView(withText("d")).check(matches(isDisplayed()))
        onView(withText("e")).check(matches(isDisplayed()))
        onView(withText("f")).check(matches(isDisplayed()))
    }

    @Test
    fun testThatSelectingMoreItemsThanTheMaxIsNotPossible() {
        activityRule.launchActivity(null)

        onView(withText("a"))
            .perform(click())
        onView(withText("b"))
            .perform(click())
        onView(withText("c"))
            .perform(click())

        onView(withText("a SELECTED")).check(matches(isDisplayed()))
        onView(withText("b SELECTED")).check(matches(isDisplayed()))
        onView(withText("c")).check(matches(isDisplayed()))
        onView(withText("d")).check(matches(isDisplayed()))
        onView(withText("e")).check(matches(isDisplayed()))
        onView(withText("f")).check(matches(isDisplayed()))
    }

    @Test
    fun testThatFilteringTheListOnlyDisplaysMatchingItems() {
        activityRule.launchActivity(null)

        onView(withText("FILTER"))
            .perform(click())

        onView(withText("a")).check(doesNotExist())
        onView(withText("b")).check(doesNotExist())
        onView(withText("c")).check(matches(isDisplayed()))
        onView(withText("d")).check(doesNotExist())
        onView(withText("e")).check(doesNotExist())
        onView(withText("f")).check(doesNotExist())
    }

    @Test
    fun testThatResettingTheFilterDisplaysAllItems() {
        activityRule.launchActivity(null)

        onView(withText("FILTER"))
            .perform(click())
        onView(withText("RESET"))
            .perform(click())

        onView(withText("a")).check(matches(isDisplayed()))
        onView(withText("b")).check(matches(isDisplayed()))
        onView(withText("c")).check(matches(isDisplayed()))
        onView(withText("d")).check(matches(isDisplayed()))
        onView(withText("e")).check(matches(isDisplayed()))
        onView(withText("f")).check(matches(isDisplayed()))
    }
}