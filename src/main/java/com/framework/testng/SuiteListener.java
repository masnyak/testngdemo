package com.framework.testng;

import org.apache.log4j.Logger;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.nio.file.Paths;
import java.util.*;

public class SuiteListener implements ISuiteListener, ITestListener, IConfigurationListener {
    private static int allTestCounter = 0;
    private static final Logger LOGGER = Logger.getLogger(SuiteListener.class);
    private String suiteName;
    private List<ITestNGMethod> testList;

    @Override
    public void onTestFailure(ITestResult result) {

        Throwable throwable = result.getThrowable();

//        LOGGER.error("<font color=\"red\" ><b>" + throwable + "</b></font>");

//        LOGGER.info("===== TEST \"" + result.getTestClass().getName() + "." + result.getName() + "\" FAILED =====");
    }

    @Override
    public void onTestStart(ITestResult result) {
        int testIndex = testList.indexOf(result.getMethod()) + 1;
        allTestCounter++;
//        LOGGER.info("===== TEST \"" + result.getTestClass().getName() + "." + result.getName() + "\" [suite '" + suiteName + "'][" + testIndex + "/" + testList.size() + "][#" + allTestCounter + "] STARTED =====");
    }

    @Override
    public void onTestSuccess(ITestResult result) {
//        LOGGER.info("===== TEST \"" + result.getTestClass().getName() + "." + result.getName() + "\" SUCCESS =====");
    }

    @Override
    public void onTestSkipped(ITestResult result) {

//        Throwable throwable = result.getThrowable();
//        if (throwable != null) {
//            LOGGER.error(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
//            LOGGER.error(throwable);
//        }
//
//        LOGGER.info("===== TEST \"" + result.getTestClass().getName() + "." + result.getName() + "\" SKIPPED =====");


    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
//        result.getThrowable().printStackTrace();
    }

    @Override
    public void onFinish(ITestContext context) {
    }

    @Override
    public void onStart(ISuite suite) {
        suite.setAttribute("timestamp", System.currentTimeMillis());
        suiteName = suite.getName();
        testList = suite.getAllMethods();

//        LOGGER.info("===== SUITE " + suiteName + " [" + testList.size() + " tests] STARTED =====");
    }

    @Override
    public void onFinish(ISuite suite) {

//        LOGGER.info("===== SUITE " + suiteName + " [" + testList.size() + " tests] FINISHED =====");
    }

    @Override
    public void onStart(ITestContext context) {
//        Thread.currentThread().setName(context.getCurrentXmlTest().getClasses().get(0).getName());
//        LOGGER.info("===== TEST \"" + context.getName() + "\" STARTED =====");
    }

    @Override
    public void onConfigurationSuccess(ITestResult itr) {
    }

    @Override
    public void onConfigurationFailure(ITestResult itr) {
//        Throwable throwable = itr.getThrowable();
//        String throwableMsg = (throwable.getMessage() == null) ? " (Message not defined)" : " - " + throwable.getMessage();
//        if (throwable instanceof AssertionError) {
//            LOGGER.error("Assertion failure: " + throwableMsg);
//        } else {
//            LOGGER.error("Error: " + throwable.getClass().getSimpleName() + throwableMsg);
//        }

//        LOGGER.info("===== CONFIG " + itr.getName() + " FAILED =====");
    }

    @Override
    public void onConfigurationSkip(ITestResult itr) {
//        Throwable throwable = itr.getThrowable();
//        if (throwable != null) {
//            LOGGER.error(throwable.getClass().getSimpleName() + ": " + throwable.getMessage());
//
//            LOGGER.info("===== CONFIG " + itr.getName() + " SKIPPED =====");
//        }
    }

}
