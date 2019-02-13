package com.github.mrbean355.android

import android.content.Intent
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.assertion.PositionAssertions.isCompletelyBelow
import android.support.test.espresso.assertion.ViewAssertions.doesNotExist
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EnhancedAdapterUiTest {
    @get:Rule
    val activityRule = ActivityTestRule<TestActivity>(TestActivity::class.java, false, false)

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
    fun testThatClickingAnDeselectedItemSelectsIt() {
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