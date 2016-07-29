package com.framework.testng;

import org.apache.log4j.Logger;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.nio.file.Paths;
import java.util.*;

public class SuiteListener implements ISuiteListener, ITestListener, IConfigurationListener {
    private static final Logger LOGGER = Logger.getLogger(SuiteListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
    }

    @Override
    public void onTestStart(ITestResult result) {
    }

    @Override
    public void onTestSuccess(ITestResult result) {
    }

    @Override
    public void onTestSkipped(ITestResult result) {
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onFinish(ITestContext context) {
        Iterator<ITestResult> listOfFailedTests = context.getFailedTests().getAllResults().iterator();
    }

    @Override
    public void onStart(ISuite suite) {
    }

    @Override
    public void onFinish(ISuite suite) {
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
    }

}
