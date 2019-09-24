package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.groupJoin.JoinGroupActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class JoinGroupClassTest {

    @Rule
    public ActivityTestRule<JoinGroupActivity> mActivityRule = new ActivityTestRule<JoinGroupActivity>(JoinGroupActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, JoinGroupActivity.class);
            return i;

        }
    };

    @BeforeClass

    public static void setUp() {
        Intents.init();
    }


    @Test
    public void join_group_tag_displays_correctly() {
        Espresso.onView(withId(R.id.join_group_tag)).check(matches(isDisplayed()));
    }


    @Test
    public void join_group_hint_displays_correctly() {
        Espresso.onView(withId(R.id.join_group_hint)).check(matches(isDisplayed()));
    }


    @Test
    public void join_group_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.join_group_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void join_button_displays_correctly() {
        Espresso.onView(withId(R.id.join_button)).check(matches(isDisplayed()));
    }


    @AfterClass
    public static void release() {
        Intents.release();

    }
}

