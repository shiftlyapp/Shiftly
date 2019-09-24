package com.technion.shiftlyapp.shiftly;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import com.technion.shiftlyapp.shiftly.entry.SignupActivity;
import com.technion.shiftlyapp.shiftly.groupsList.GroupListsActivity;
import com.technion.shiftlyapp.shiftly.utility.MethodsForTests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class SignupClassTest {

    private static final String ALPHA = "abcdsefghijklmnopqrstuvwxys";
    @Rule
    public ActivityTestRule<SignupActivity> mActivityRule = new ActivityTestRule<>(SignupActivity.class);

    @BeforeClass
    public static void setUp() {
        Intents.init();

    }
    @Test
    public void sign_up_successfully() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            int character = (int)(Math.random()*ALPHA.length());
            builder.append(ALPHA.charAt(character));
        }
        String email = builder.toString() + "@gmail.com";

        Espresso.onView(ViewMatchers.withId(R.id.signup_firstname_edittext)).perform(ViewActions.replaceText("Chris"), closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.signup_lastname_edittext)).perform(ViewActions.replaceText("Gardener"), closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.signup_email_edittext)).perform(ViewActions.replaceText(email), closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.signup_password_edittext)).perform(ViewActions.replaceText("12345678"), closeSoftKeyboard());
        Espresso.onView(ViewMatchers.withId(R.id.signup_confirm_password_edittext)).perform(ViewActions.replaceText("12345678"), closeSoftKeyboard());
        MethodsForTests.wait_to_load();

        Espresso.onView(ViewMatchers.withId(R.id.signup_button)).perform(scrollTo(), ViewActions.click());
        MethodsForTests.wait_to_load();
        intended(hasComponent(GroupListsActivity.class.getName()));
        MethodsForTests.wait_to_load();

    }

    @Test
    public void signup_header_displays_correctly() {
        Espresso.onView(withId(R.id.signup_header)).check(matches(isDisplayed()));
    }


    @Test
    public void signup_pic_displays_correctly() {
        Espresso.onView(withId(R.id.signup_pic)).check(matches(isDisplayed()));
    }


    @Test
    public void signup_firstname_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.signup_firstname_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void signup_lastname_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.signup_lastname_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void signup_email_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.signup_email_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void signup_password_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.signup_password_edittext)).check(matches(isDisplayed()));
    }

    @Test
    public void signup_confirm_password_edittext_displays_correctly() {
        Espresso.onView(withId(R.id.signup_confirm_password_edittext)).check(matches(isDisplayed()));
    }


    @Test
    public void signup_button_displays_correctly() {
        Espresso.onView(ViewMatchers.withId(R.id.signup_button)).perform(scrollTo(), ViewActions.closeSoftKeyboard());
        Espresso.onView(withId(R.id.signup_button)).check(matches(isDisplayed()));
    }

    @AfterClass
    public static void release() {
        Intents.release();

    }
}