package org.malsati.controllers_test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses(
        {
                T01AuthorControllerTest.class,
                T02BookControllerTest.class,
                T03AuthorAndBookControllersTest.class,
                T04JSONConditionTest.class
        }
)
public class ContollersTestsSuite {
}
