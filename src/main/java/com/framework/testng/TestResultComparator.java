package com.framework.testng;

import org.testng.ITestResult;

import java.util.Comparator;

class TestResultComparator implements Comparator<ITestResult>
{
    public int compare(ITestResult result1, ITestResult result2)
    {
        return (int) (result1.getStartMillis() - result2.getStartMillis());
    }
}
