package com.technion.shiftly;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.technion.shiftly.groupCreation.GroupCreation1Activity;
import com.technion.shiftly.groupCreation.GroupCreation2Activity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

@RunWith(AndroidJUnit4.class)
public class GroupCreation1Test {

    @Rule
    public ActivityTestRule<GroupCreation1Activity> mActivityRule = new ActivityTestRule<>(GroupCreation1Activity.class);

    @BeforeClass
    public static void setUp() {
        Intents.init();

    }
    @Test
    public void open_group_creation_2_successfully() {
        MethodsForTests.wait_to_load();

        Espresso.onView(ViewMatchers.withId(R.id.group_name_edittext)).perform(ViewActions.replaceText("TEST"), closeSoftKeyboard());
        MethodsForTests.wait_to_load();

        Espresso.onView(ViewMatchers.withId(R.id.create_button)).perform(ViewActions.click());
        MethodsForTests.wait_to_load();

        intended(hasComponent(GroupCreation2Activity.class.getName()));
        MethodsForTests.wait_to_load();

    }

    @AfterClass
    public static void release() {
        Intents.release();

    }
}