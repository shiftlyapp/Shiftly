//package com.technion.shiftlyapp.shiftly;
//
//import android.content.Intent;
//
//import androidx.test.espresso.Espresso;
//import androidx.test.espresso.action.ViewActions;
//import androidx.test.espresso.intent.Intents;
//import androidx.test.espresso.matcher.ViewMatchers;
//import androidx.test.rule.ActivityTestRule;
//
//import com.technion.shiftlyapp.shiftly.groupCreation.GroupCreation2Activity;
//import com.technion.shiftlyapp.shiftly.groupCreation.GroupCreation3Activity;
//import com.technion.shiftlyapp.shiftly.utility.MethodsForTests;
//
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Rule;
//import org.junit.Test;
//
//import static androidx.test.espresso.intent.Intents.intended;
//import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
//
//public class GroupCreation2Test {
//
//    @Rule
//    public ActivityTestRule<GroupCreation2Activity> mActivityRule = new ActivityTestRule<>(GroupCreation2Activity.class);
//
//    @BeforeClass
//    public static void setUp() {
//        Intents.init();
//
//    }
//    @Test
//    public void open_group_creation_3_successfully() {
//
//        MethodsForTests.wait_to_load();
//        Intent i = new Intent();
//        i.putExtra("GROUP_ACTION", "CREATE");
//        MethodsForTests.wait_to_load();
//        mActivityRule.launchActivity(i);
//        MethodsForTests.wait_to_load();
//        Espresso.onView(ViewMatchers.withId(R.id.continue_button_group_creation_2)).perform(ViewActions.click());
//        MethodsForTests.wait_to_load();
//        intended(hasComponent(GroupCreation3Activity.class.getName()));
//        MethodsForTests.wait_to_load();
//
//    }
//    @AfterClass
//    public static void release() {
//        Intents.release();
//
//    }
//}