package com.technion.shiftlyapp.shiftly;

import com.technion.shiftlyapp.shiftly.dataTypes.User;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UserClassTest {
    @Test
    public void default_groupsCount_is_correct() {
        User user = new User("Ada", "Yonat", "ada@gmail.com");
        assertEquals("0", user.getGroups_count().toString());
    }
}