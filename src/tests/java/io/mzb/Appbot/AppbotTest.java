package io.mzb.Appbot;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

public class AppbotTest {

    private int apple, orange;

    @Before
    public void setup() {
        apple = 10;
        orange = 20;
    }

    @Test
    public void testAppleIsOrange() {
        assertFalse(apple == orange);
    }

}
