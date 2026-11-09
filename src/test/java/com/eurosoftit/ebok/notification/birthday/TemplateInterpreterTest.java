package com.eurosoftit.ebok.notification.birthday;

import lombok.Setter;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class TemplateInterpreterTest {

    @Test
    public void interpretOnePattern() {
        final String testMessage = "Test {name}";
        final String expected = "Test test";

        TestObj testObj =  new TestObj();
        testObj.setName("test");
        final String result = TemplateInterpreter.process(testObj, testMessage);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void interpretTwoPattern() {
        final String testMessage = "Test {name} {name1}";
        final String expected = "Test test 1";

        TestObj testObj =  new TestObj();
        testObj.setName("test");
        testObj.setName1("1");
        final String result = TemplateInterpreter.process(testObj, testMessage);

        Assertions.assertEquals(expected, result);
    }

    @Test
    public void interpretNulObj() {
        final String testMessage = "Test {name} {name1}";

        final String result = TemplateInterpreter.process(null, testMessage);

        Assertions.assertEquals(testMessage, result);
    }

    @Test
    public void interpretWrongFieldFilled() {
        final String testMessage = "Test {name}";
        final String expected = "Test 1";

        TestObj testObj =  new TestObj();
        testObj.setName1("1");

        final String result = TemplateInterpreter.process(testObj, testMessage);

        Assertions.assertNotEquals(expected, result);
    }

    @Test
    public void interpretWrongField() {
        final String testMessage = "Test {name12}";
        final String expected = "Test 1";

        TestObj testObj =  new TestObj();
        testObj.setName1("1");

        final String result = TemplateInterpreter.process(testObj, testMessage);

        Assertions.assertNotEquals(expected, result);
    }

    @Setter
    class TestObj {
        private String name, name1, name2;
    }
}
