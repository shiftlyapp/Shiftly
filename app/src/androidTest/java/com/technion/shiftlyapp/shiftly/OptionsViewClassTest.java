package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.optionsView.OptionsViewActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class OptionsViewClassTest {

    @Rule
    public ActivityTestRule<OptionsViewActivity> mActivityRule = new ActivityTestRule<OptionsViewActivity>(OptionsViewActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, OptionsViewActivity.class);
            i.putExtra("GROUP_ACTION", "CREATE");
            i.putExtra("GROUP_NAME", "CREATE");
            i.putExtra("STARTING_TIME", "CREATE");
            i.putExtra("SHIFT_LENGTH", "CREATE");
            i.putExtra("SHIFTS_PER_DAY", "CREATE");
            i.putExtra("DAYS_NUM", "CREATE");
            i.putExtra("WORKERS_IN_SHIFT", "CREATE");
            i.putExtra("WORKERS_WITHOUT_OPTIONS", "CREATE");
            HashMap<String, String> hm = new HashMap<>();
            i.putExtra("OPTIONS", hm);
            return i;
        }
    };

    @BeforeClass

    public static void setUp() {
        Intents.init();
    }


    @Test
    public void options_view_title_displays_correctly() {
        Espresso.onView(withId(R.id.options_view_title)).check(matches(isDisplayed()));
    }


    @Test
    public void options_table_displays_correctly() {
        Espresso.onView(withId(R.id.options_table)).check(matches(isDisplayed()));
    }


    @Test
    public void options_view_subtitle_displays_correctly() {
        Espresso.onView(withId(R.id.options_view_subtitle)).check(matches(isDisplayed()));
    }


    @Test
    public void options_workers_without_options_displays_correctly() {
        Espresso.onView(withId(R.id.options_workers_without_options)).check(matches(isDisplayed()));
    }


    @Test
    public void flexibility_graph_title_displays_correctly() {
        Espresso.onView(withId(R.id.flexibility_graph_title)).check(matches(isDisplayed()));
    }


    @Test
    public void flexibility_graph_displays_correctly() {
        Espresso.onView(withId(R.id.flexibility_graph)).check(matches(isDisplayed()));
    }


    @AfterClass
    public static void release() {
        Intents.release();

    }
}

