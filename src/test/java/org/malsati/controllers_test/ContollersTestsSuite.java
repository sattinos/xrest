package org.malsati.controllers_test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(
        {
                T01_AuthorControllerTest.class,
                T02_BookControllerTest.class,
                T03_AuthorAndBookControllersTest.class,
                T04_JSONConditionTest.class
        }
)
public class ContollersTestsSuite {
}
