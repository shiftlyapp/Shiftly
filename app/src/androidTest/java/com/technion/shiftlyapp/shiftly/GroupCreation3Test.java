//package com.technion.shiftlyapp.shiftly;
//
//import android.support.test.espresso.Espresso;
//import android.support.test.espresso.action.ViewActions;
//import android.support.test.espresso.intent.Intents;
//import android.support.test.espresso.matcher.ViewMatchers;
//import android.support.test.rule.ActivityTestRule;
//import android.support.test.runner.AndroidJUnit4;
//
//import com.technion.shiftlyapp.shiftly.groupCreation.GroupCreation3Activity;
//import com.technion.shiftlyapp.shiftly.groupCreation.GroupCreation4Activity;
//import com.technion.shiftlyapp.shiftly.utility.MethodsForTests;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static android.support.test.espresso.intent.Intents.intended;
//import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//
//@RunWith(AndroidJUnit4.class)
//public class GroupCreation3Test {
//
//    @Rule
//    public ActivityTestRule<GroupCreation3Activity> mActivityRule = new ActivityTestRule<>(GroupCreation3Activity.class);
//
//    @BeforeClass
//    public static void setUp() {
//        Intents.init();
//
//    }
//    @Test
//    public void open_group_creation_4_successfully() {
//        MethodsForTests.wait_to_load();
//
//        Espresso.onView(ViewMatchers.withId(R.id.continue_button)).perform(ViewActions.click());
//        MethodsForTests.wait_to_load();
//
//        intended(hasComponent(GroupCreation4Activity.class.getName()));
//        MethodsForTests.wait_to_load();
//
//    }
//    @AfterClass
//    public static void release() {
//        Intents.release();
//
//    }
//}