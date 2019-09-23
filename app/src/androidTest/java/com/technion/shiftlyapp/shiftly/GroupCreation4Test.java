package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.dataTypes.Group;
import com.technion.shiftlyapp.shiftly.groupCreation.GroupCreation4Activity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

@RunWith(AndroidJUnit4.class)
public class GroupCreation4Test {

    @Rule
    public ActivityTestRule<GroupCreation4Activity> mActivityRule = new ActivityTestRule<GroupCreation4Activity>(GroupCreation4Activity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, GroupCreation4Activity.class);
            i.putExtra("GROUP_ACTION", "CREATE");
            Group group = new Group("admin", "group_name", 0L, 1L, 1L, 1L, "8", 1L, "none");
            i.putExtra("GROUP", group);
            return i;
        }
    };

    @BeforeClass
    public static void setUp() {
        Intents.init();

    }


    @Test
    public void group_code_contains_dash() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.group_code)).check(matches(withText(containsString("-"))));
    }

    @Test
    public void share_with_friends_text_correct() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Espresso.onView(withId(R.id.share_with_friends_txt)).check(matches(withText(R.string.group_share_hint)));
    }

    @AfterClass
    public static void release() {
        Intents.release();

    }
}

