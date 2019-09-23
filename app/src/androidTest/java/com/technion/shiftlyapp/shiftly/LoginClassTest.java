package com.technion.shiftlyapp.shiftly;

import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.entry.LoginActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class LoginClassTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityRule = new ActivityTestRule<LoginActivity>(LoginActivity.class) {
        @Override
        protected Intent getActivityIntent() {
            Context targetContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
            Intent i = new Intent(targetContext, LoginActivity.class);
            return i;

        }
    };

    @BeforeClass

    public static void setUp() {
        Intents.init();
    }


    @Test
    public void clock_anim_displays_correctly() {
        Espresso.onView(withId(R.id.clock_anim)).check(matches(isDisplayed()));
    }


    @Test
    public void shiftly_logo_displays_correctly() {
        Espresso.onView(withId(R.id.shiftly_logo)).check(matches(isDisplayed()));
    }


    @Test
    public void user_pic_displays_correctly() {
        Espresso.onView(withId(R.id.user_pic)).check(matches(isDisplayed()));
    }


    @Test
    public void email_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.email_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void password_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.password_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void forgot_pass_text_displays_correctly() {
        Espresso.onView(withId(R.id.forgot_pass_text)).check(matches(isDisplayed()));
    }

    @Test
    public void login_button_displays_correctly() {
        Espresso.onView(withId(R.id.login_button)).check(matches(isDisplayed()));
    }


    @Test
    public void google_login_button_displays_correctly() {
        Espresso.onView(withId(R.id.google_login_button)).check(matches(isDisplayed()));
    }

    @Test
    public void facebook_login_button_displays_correctly() {
        Espresso.onView(withId(R.id.facebook_login_button)).check(matches(isDisplayed()));
    }


    @Test
    public void sign_in_with_textview_displays_correctly() {
        Espresso.onView(withId(R.id.sign_in_with_textview)).check(matches(isDisplayed()));
    }


    @Test
    public void remember_login_checkbox_displays_correctly() {
        Espresso.onView(withId(R.id.remember_login_checkbox)).check(matches(isDisplayed()));
    }

    @AfterClass
    public static void release() {
        Intents.release();

    }
}

