package com.tests;

import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;


public class Test1 {
    private final Logger LOGGER = Logger.getLogger(this.getClass());

    @Test(description = "Verification error")
    public void testVerificationError() {
        int arg1 = 1;
        int arg2 = 2;
        int expectedResult = 4;

        int actualResult = arg1 + arg2;
        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Obscure error")
    public void testObscureError() {
        int arg1 = 1;
        Integer arg2 = null;
        int expectedResult = 4;

        int actualResult = arg1 + arg2;
        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Skipped test")
    public void testSkipped() {
        int arg1 = 1;
        int arg2 = 2;
        int expectedResult = 4;

        if (arg2 > 1)
            throw new SkipException("arg2 was too large.");

        int actualResult = arg1 + arg2;
        Assert.assertEquals(actualResult, expectedResult);
    }

    @Test(description = "Passed test")
    public void testPassed() {
        int arg1 = 1;
        int arg2 = 2;
        int expectedResult = 3;

        int actualResult = arg1 + arg2;
        Assert.assertEquals(actualResult, expectedResult);
    }

    @DataProvider
    public Object[][] getData() {
        return new Object[][]{{1, 2, 3}, {1, 2, 4}};
    }
}
