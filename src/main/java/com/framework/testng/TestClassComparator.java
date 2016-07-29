package com.framework.testng;

import org.testng.IClass;

import java.util.Comparator;

class TestClassComparator implements Comparator<IClass>
{
    public int compare(IClass class1, IClass class2)
    {
        return class1.getName().compareTo(class2.getName());
    }
}
