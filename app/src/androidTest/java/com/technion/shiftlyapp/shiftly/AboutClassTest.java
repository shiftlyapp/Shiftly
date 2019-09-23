package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.miscellaneous.AboutActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class AboutClassTest {

    @Rule
    public ActivityTestRule<AboutActivity> mActivityRule = new ActivityTestRule<AboutActivity>(AboutActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, AboutActivity.class);
            return i;

        }
    };

    @BeforeClass

    public static void setUp() {
        Intents.init();
    }


    @Test
    public void contact_title_displays_correctly() {
        Espresso.onView(withId(R.id.contact_title)).check(matches(isDisplayed()));
    }


    @Test
    public void contact_email_displays_correctly() {
        Espresso.onView(withId(R.id.contact_email)).check(matches(isDisplayed()));
    }


    @Test
    public void credits_title_displays_correctly() {
        Espresso.onView(withId(R.id.credits_title)).check(matches(isDisplayed()));
    }


    @Test
    public void credits_displays_correctly() {
        Espresso.onView(withId(R.id.credits)).check(matches(isDisplayed()));
    }


    @Test
    public void privacy_policy_title_displays_correctly() {
        Espresso.onView(withId(R.id.privacy_policy_title)).check(matches(isDisplayed()));
    }


    @Test
    public void privacy_policy_displays_correctly() {
        Espresso.onView(withId(R.id.privacy_policy)).check(matches(isDisplayed()));
    }


    @AfterClass
    public static void release() {
        Intents.release();

    }
}

