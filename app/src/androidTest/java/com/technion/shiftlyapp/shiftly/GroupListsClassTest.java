package com.technion.shiftlyapp.shiftly;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.scheduleView.ScheduleViewActivity;
import com.technion.shiftlyapp.shiftly.utility.MethodsForTests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;

public class GroupListsClassTest {

    @Rule
    public ActivityTestRule<GroupListsActivity> mActivityRule = new ActivityTestRule<>(GroupListsActivity.class);

    @BeforeClass
    public static void setUp() {
        Intents.init();

    }
    @Test
    public void open_schedule_view_successfully() {

        MethodsForTests.wait_to_load();

        Espresso.onView(ViewMatchers.withId(R.id.groups_i_belong)).perform(ViewActions.click());
        MethodsForTests.wait_to_load();

        intended(hasComponent(ScheduleViewActivity.class.getName()));
        MethodsForTests.wait_to_load();

    }
    @AfterClass
    public static void release() {
        Intents.release();

    }
}