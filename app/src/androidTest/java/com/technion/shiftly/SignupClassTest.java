package com.technion.shiftly;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.technion.shiftly.entry.SignupActivity;
import com.technion.shiftly.groupsList.GroupListsActivity;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;

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
        MethodsForTests.wait_to_load();

        Espresso.onView(ViewMatchers.withId(R.id.signup_firstname_edittext)).perform(ViewActions.replaceText("Chris"), closeSoftKeyboard());
        MethodsForTests.wait_to_load();
        Espresso.onView(ViewMatchers.withId(R.id.signup_lastname_edittext)).perform(ViewActions.replaceText("Gardener"), closeSoftKeyboard());
        MethodsForTests.wait_to_load();
        Espresso.onView(ViewMatchers.withId(R.id.signup_email_edittext)).perform(ViewActions.replaceText(email), closeSoftKeyboard());
        MethodsForTests.wait_to_load();
        Espresso.onView(ViewMatchers.withId(R.id.signup_password_edittext)).perform(ViewActions.replaceText("12345678"), closeSoftKeyboard());
        MethodsForTests.wait_to_load();
        Espresso.onView(ViewMatchers.withId(R.id.signup_confirm_password_edittext)).perform(ViewActions.replaceText("12345678"), closeSoftKeyboard());
        MethodsForTests.wait_to_load();

        Espresso.onView(ViewMatchers.withId(R.id.signup_button)).perform(scrollTo(), ViewActions.click());
        MethodsForTests.wait_to_load();

        intended(hasComponent(GroupListsActivity.class.getName()));
        MethodsForTests.wait_to_load();

    }
    @AfterClass
    public static void release() {
        Intents.release();

    }
}