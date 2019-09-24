package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.scheduleView.ScheduleEditActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ScheduleEditClassTest {

    @Rule
    public ActivityTestRule<ScheduleEditActivity> mActivityRule = new ActivityTestRule<ScheduleEditActivity>(ScheduleEditActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, ScheduleEditActivity.class);
            // Rambam group id
            i.putExtra("GROUP_ID", "-Lc1RwizTFHpWuJIVBm-");
            return i;
        }
    };

    @BeforeClass

    public static void setUp() {
        Intents.init();
    }


    @Test
    public void group_shifts_displays_correctly() {
        Espresso.onView(withId(R.id.group_shifts)).check(matches(isDisplayed()));
    }


    @Test
    public void schedule_edit_shifts_tooltip_displays_correctly() {
        Espresso.onView(withId(R.id.schedule_edit_shifts_tooltip)).check(matches(isDisplayed()));
    }


    @Test
    public void save_button_displays_correctly() {
        Espresso.onView(withId(R.id.save_button)).check(matches(isDisplayed()));
    }


    @Test
    public void cancel_button_displays_correctly() {
        Espresso.onView(withId(R.id.cancel_button)).check(matches(isDisplayed()));
    }


    @AfterClass
    public static void release() {
        Intents.release();

    }
}

