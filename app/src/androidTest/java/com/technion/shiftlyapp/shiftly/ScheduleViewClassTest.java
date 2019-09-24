package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.scheduleView.ScheduleViewActivity;
import com.technion.shiftlyapp.shiftly.utility.MethodsForTests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ScheduleViewClassTest {

    @Rule
    public ActivityTestRule<ScheduleViewActivity> mActivityRule = new ActivityTestRule<ScheduleViewActivity>(ScheduleViewActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, ScheduleViewActivity.class);
            i.putExtra("GROUP_ID", "-Lc1RwizTFHpWuJIVBm-");
            return i;
        }
    };

    @BeforeClass
    public static void setUp() {
        Intents.init();
    }


    @Test
    public void schedule_tooltip_displays_correctly() {
        MethodsForTests.wait_to_load();
        Espresso.onView(withId(R.id.schedule_tooltip)).check(matches(isDisplayed()));
    }


    @Test
    public void schedule_fab_displays_correctly() {
        MethodsForTests.wait_to_load();
        Espresso.onView(withId(R.id.schedule_fab)).check(matches(isDisplayed()));
    }


    @Test
    public void edit_schedule_fab_tooltip_displays_correctly() {
        MethodsForTests.wait_to_load();
        Espresso.onView(withId(R.id.edit_schedule_fab_tooltip)).check(matches(isDisplayed()));
    }


    @Test
    public void edit_schedule_fab_displays_correctly() {
        MethodsForTests.wait_to_load();
        Espresso.onView(withId(R.id.edit_schedule_fab)).check(matches(isDisplayed()));
    }


    @Test
    public void view_options_tooltip_displays_correctly() {
        MethodsForTests.wait_to_load();
        Espresso.onView(withId(R.id.view_options_tooltip)).check(matches(isDisplayed()));
    }


    @Test
    public void view_options_fab_displays_correctly() {
        MethodsForTests.wait_to_load();
        Espresso.onView(withId(R.id.view_options_fab)).check(matches(isDisplayed()));
    }


    @AfterClass
    public static void release() {
        Intents.release();

    }
}

