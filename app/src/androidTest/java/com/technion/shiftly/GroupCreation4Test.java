package com.technion.shiftly;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.technion.shiftly.groupCreation.GroupCreation4Activity;
import com.technion.shiftly.groupsList.GroupListsActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class GroupCreation4Test {

    @Rule
    public ActivityTestRule<GroupCreation4Activity> mActivityRule = new ActivityTestRule<>(GroupCreation4Activity.class);

    @BeforeClass
    public static void setUp() {
        Intents.init();

    }
    @Test
    public void group_code_includes_dash() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.share_with_friends_txt)).check(matches(withText(R.string.group_share_hint)));
    }

    @Test
    public void open_group_creation_4_successfully() {
        MethodsForTests.wait_to_load();

        Espresso.pressBack();

        MethodsForTests.wait_to_load();

        intended(hasComponent(GroupListsActivity.class.getName()));
        MethodsForTests.wait_to_load();

    }
    @AfterClass
    public static void release() {
        Intents.release();

    }
}