package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.groupCreation.GroupCreation3Activity;
import com.technion.shiftlyapp.shiftly.groupCreation.GroupCreation4Activity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class GroupCreation3Test {

    @Rule
    public ActivityTestRule<GroupCreation3Activity> mActivityRule = new ActivityTestRule<GroupCreation3Activity>(GroupCreation3Activity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, GroupCreation3Activity.class);
            i.putExtra("GROUP_ACTION", "CREATE");
            Group group = new Group("admin", "group_name", 0L, 1L, 1L, 1L, "8", 1L, null);
            i.putExtra("GROUP", group);
            return i;
        }
    };

    @BeforeClass
    public static void setUp() {
        Intents.init();

    }
    @Test
    public void open_group_creation_4_successfully() {
        onView(withId(R.id.continue_button_group_creation_3)).perform(click());
        intended(hasComponent(GroupCreation4Activity.class.getName()));

    }
    @AfterClass
    public static void release() {
        Intents.release();

    }
}