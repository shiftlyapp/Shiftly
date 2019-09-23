package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.userUpdate.UserUpdateActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class UserUpdateClassTest {

    @Rule
    public ActivityTestRule<UserUpdateActivity> mActivityRule = new ActivityTestRule<UserUpdateActivity>(UserUpdateActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, UserUpdateActivity.class);
            return i;

        }
    };

    @BeforeClass

    public static void setUp() {
        Intents.init();
    }


    @Test
    public void user_update_header_displays_correctly() {
        Espresso.onView(withId(R.id.user_update_header)).check(matches(isDisplayed()));
    }


    @Test
    public void user_update_pic_displays_correctly() {
        Espresso.onView(withId(R.id.user_update_pic)).check(matches(isDisplayed()));
    }


    @Test
    public void user_update_firstname_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.user_update_firstname_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void user_update_lastname_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.user_update_lastname_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void user_update_password_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.user_update_password_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void user_update_confirm_password_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.user_update_confirm_password_edittext)).check(matches(isDisplayed()));
    }



    @Test
    public void user_update_button_displays_correctly() {
        Espresso.onView(withId(R.id.user_update_button)).check(matches(isDisplayed()));
    }


    @AfterClass
    public static void release() {
        Intents.release();

    }
}

