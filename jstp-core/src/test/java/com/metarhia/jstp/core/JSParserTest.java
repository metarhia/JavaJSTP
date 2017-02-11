package com.metarhia.jstp.core;

import com.metarhia.jstp.core.JSTypes.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JSParserTest {

    private static final TestUtils.TestData[] escapeTestData = new TestUtils.TestData[]{
            new TestUtils.TestData("{}", "'{}'"),
            new TestUtils.TestData("имя, оно самоеё~:)", "'имя, оно самоеё~:)'"),
            new TestUtils.TestData("fff\u0000\u1111g\u0020gg\u007f",
                    "'fff\\u0000\u1111g\u0020gg\\u007F'")
    };

    private static final TestUtils.TestData[] parseUnescapeTestData = new TestUtils.TestData[]{
            new TestUtils.TestData("'abc\\n\\tf\\0ff\\u4455ggg\\u0011'",
                    "abc\n\tf\0ff\u4455ggg\u0011")
    };

    private static final TestUtils.TestData[] stringifyTestData = new TestUtils.TestData[]{
            new TestUtils.TestData("{}", "{}"),
            new TestUtils.TestData("'abv\\\"gggg\\\"dd'", "'abv\"gggg\"dd'"),
            new TestUtils.TestData("'abv\"gggg\"dd'", "'abv\"gggg\"dd'"),
            new TestUtils.TestData("['outer', ['inner']]", "[\'outer\',[\'inner\']]"),
            new TestUtils.TestData("\'\\u{1F49A}ttt\\u{1F49B}\'", "'💚ttt💛'"),
            new TestUtils.TestData("'\\x20'", "' '")
    };

    @Test
    public void parseArray() throws Exception {
        String input = "[1, 2, '5555']";
        JSArray expected = new JSArray(Arrays.<Object>asList(1, 2, "5555"));

        JSParser parser = new JSParser(input);
        JSArray actual = parser.parseArray();

        assertEquals(expected, actual);
    }

    @Test
    public void parseObject() throws Exception {
        String input = "{a: 1, b: 2.0, c: '5555'}";
        JSObject expected = new JSObject();
        expected.put("a", new JSNumber(1));
        expected.put("b", new JSNumber(2.0));
        expected.put("c", new JSString("5555"));

        JSParser parser = new JSParser(input);
        JSObject actual = parser.parseObject();

        assertEquals(expected, actual);
    }

    @Test
    public void parseKeyValuePair() throws Exception {
        String[] inputs = {"a: 4", "55 : ['abc']"};

        JSObject.Entry[] expecteds = {
                new JSObject.Entry("a", 4),
                new JSObject.Entry("55",
                        new JSArray(Collections.<Object>singletonList("abc")))
        };

        JSParser parser = new JSParser();
        for (int i = 0; i < inputs.length; i++) {
            parser.setInput(inputs[i]);

            final JSObject.Entry expected = expecteds[i];
            final JSObject.Entry actual = parser.parseKeyValuePair();

            assertEquals(expected.toString(), actual.toString());
        }
    }

    @Test
    public void setInput() throws Exception {
        String input = "true";
        String anotherInput = "false";

        JSParser parser = new JSParser(input);

        parser.setInput(anotherInput);

        assertEquals(parser.parse(), new JSBool(false));
    }

    @Test
    public void parse() throws Exception {
        String input = "true false 10 63.52 undefined null";
        JSParser parser = new JSParser(input);
        try {
            JSValue value = parser.parse();
            assertEquals("Parsing 'true' failed", new JSBool(true), value);

            value = parser.parse();
            assertEquals("Parsing 'false' failed", new JSBool(false), value);

            value = parser.parse();
            assertEquals("Parsing decimal failed", new JSNumber(10), value);

            value = parser.parse();
            assertEquals("Parsing double failed", new JSNumber(63.52), value);

            value = parser.parse();
            assertEquals("Parsing 'undefined' failed", JSUndefined.get(), value);

            value = parser.parse();
            assertEquals("Parsing 'null' failed", JSNull.get(), value);

            //test array
            input = "[ 'abs', 'smth else', \" or like this \", ['inside', 'elsein']]";
            JSArray nestedArray = new JSArray();
            nestedArray.add(new JSString("inside"));
            nestedArray.add(new JSString("elsein"));

            JSArray expected = new JSArray();
            expected.add(new JSString("abs"));
            expected.add(new JSString("smth else"));
            expected.add(new JSString(" or like this "));
            expected.add(nestedArray);

            parser.setInput(input);
            value = parser.parse();
            assertTrue("Parsing array failed", value instanceof JSArray);
            assertEquals("Parsing array failed", expected, value);
        } catch (JSParsingException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testPacketSample() throws Exception {
        String input = "{\n" +
                "  name: 'Marcus Aurelius',\n" +
                "  passport: 'AE127095',\n" +
                "  birth: {\n" +
                "    date: '1990-02-15',\n" +
                "    place: 'Rome'\n" +
                "  },\n" +
                "  contacts: {\n" +
                "    email: 'marcus@aurelius.it',\n" +
                "    phone: '+380505551234',\n" +
                "    address: {\n" +
                "      country: 'Ukraine',\n" +
                "      city: 'Kiev',\n" +
                "      zip: '03056',\n" +
                "      street: 'Pobedy',\n" +
                "      building: '37',\n" +
                "      floor: '1',\n" +
                "      room: ['158', '111', '555']\n" +
                "    }\n" +
                "  }\n" +
                "}";

        JSObject expected = new JSObject();
        expected.put("name", new JSString("Marcus Aurelius"));
        expected.put("passport", new JSString("AE127095"));
        JSObject nestedBirth = new JSObject();
        nestedBirth.put("date", new JSString("1990-02-15"));
        nestedBirth.put("place", new JSString("Rome"));
        expected.put("birth", nestedBirth);
        JSObject nestedContacts = new JSObject();
        nestedContacts.put("email", new JSString("marcus@aurelius.it"));
        nestedContacts.put("phone", new JSString("+380505551234"));
        JSObject nnAddress = new JSObject();
        nnAddress.put("country", new JSString("Ukraine"));
        nnAddress.put("city", new JSString("Kiev"));
        nnAddress.put("zip", new JSString("03056"));
        nnAddress.put("street", new JSString("Pobedy"));
        nnAddress.put("building", new JSString("37"));
        nnAddress.put("floor", new JSString("1"));
        JSArray roomArray = new JSArray();
        roomArray.add(new JSString("158"));
        roomArray.add(new JSString("111"));
        roomArray.add(new JSString("555"));
        nnAddress.put("room", roomArray);
        nestedContacts.put("address", nnAddress);
        expected.put("contacts", nestedContacts);


        JSParser parser = new JSParser(input);
        JSObject actual = parser.parseObject();

        assertEquals(expected, actual);
    }

    @Test
    public void testInvalidObject() throws Exception {
        String input = "{he : llo : 123}";

        boolean result = false;
        try {
            new JSParser(input).parse();
        } catch (JSParsingException e) {
            result = true;
        }

        assertTrue("must throw exception", result);
    }

    @Test
    public void testConsoleConfig() throws Exception {
        String input = "  {\n" +
                "    login: {\n" +
                "      control: 'screen',\n" +
                "      controls: {\n" +
                "        login: {\n" +
                "          control: 'edit',\n" +
                "          filter: 'login',\n" +
                "          top: 10, left: 10, right: 10,\n" +
                "          height: 10,\n" +
                "          label: 'login'\n" +
                "        },\n" +
                "        password: {\n" +
                "          control: 'edit',\n" +
                "          mode: 'password',\n" +
                "          top: 25, left: 10, right: 10,\n" +
                "          height: 10,\n" +
                "          label: 'password'\n" +
                "        },\n" +
                "        cancel: {\n" +
                "          control: 'button',\n" +
                "          top: 40, right: 70,\n" +
                "          width: 25, height: 10,\n" +
                "          text: 'Cancel'\n" +
                "        },\n" +
                "        signin: {\n" +
                "          control: 'button',\n" +
                "          top: 40, right: 10,\n" +
                "          width: 25, height: 10,\n" +
                "          text: 'Sign in'\n" +
                "        },\n" +
                "        social: {\n" +
                "          control: 'panel',\n" +
                "          top: 55, bottom: 10, left: 10, right: 10,\n" +
                "          controls: {\n" +
                "            googlePlus: {\n" +
                "              control: 'button',\n" +
                "              top: 0, left: 0,\n" +
                "              height: 10, width: 10,\n" +
                "              image: 'googlePlus'\n" +
                "            },\n" +
                "            facebook: {\n" +
                "              control: 'button',\n" +
                "              top: 0, left: 10,\n" +
                "              height: 10, width: 10,\n" +
                "              image: 'facebook'\n" +
                "            },\n" +
                "            vk: {\n" +
                "              control: 'button',\n" +
                "              top: 0, left: 10,\n" +
                "              height: 10, width: 10,\n" +
                "              image: 'vk'\n" +
                "            },\n" +
                "            twitter: {\n" +
                "              control: 'button',\n" +
                "              top: 0, left: 20,\n" +
                "              height: 10, width: 10,\n" +
                "              image: 'twitter'\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    main: {\n" +
                "      control: 'screen',\n" +
                "      controls: {\n" +
                "        message: {\n" +
                "          control: 'label',\n" +
                "          top: 10, left: 10, right: 10,\n" +
                "          height: 10,\n" +
                "          text: 'You are logged in'\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "}";

        JSParser parser = new JSParser(input);
        JSObject actual = parser.parseObject();
    }

    @Test
    public void arrayEmptyPlace() throws Exception {
        String input = "[1,,300]";

        JSArray expected = new JSArray();
        expected.add(new JSNumber(1));
        expected.add(JSUndefined.get());
        expected.add(new JSNumber(300));

        JSArray actual = (JSArray) new JSParser(input).parse();

        assertEquals(expected, actual);
    }

    @Test
    public void longParsing() throws Exception {
        String input = "{birth: -2051225940000}";

        String actual = ((JSObject) new JSParser(input).parse()).get("birth").toString();
        String expected = String.valueOf(-2051225940000L);

        assertEquals(expected, actual);
    }

    @Test
    public void arrayEmptyPlaceFirst() throws Exception {
        String input = "[,,0]";

        JSArray expected = new JSArray();
        expected.add(JSUndefined.get());
        expected.add(JSUndefined.get());
        expected.add(new JSNumber(0));

        JSArray actual = (JSArray) new JSParser(input).parse();

        assertEquals(expected, actual);
    }

    @Test
    public void parseWithEscapingDouble() throws Exception {
        String input = "{nickname:\"\\n\\tnyaaaaaa'aaa'[((:’ –( :-)) :-| :~ =:O)],\"}";
        JSObject actual = new JSParser(input).parseObject();

        JSObject expected = new JSObject();
        expected.put("nickname", "\n\tnyaaaaaa\'aaa\'[((:’ –( :-)) :-| :~ =:O)],");

        assertEquals(expected, actual);
    }

    @Test
    public void parseWithEscapingSingle() throws Exception {
        String input = "{nickname: '\\n\\tnyaaaaaa\\'aaa\\'[((:’ –( :-)) :-| :~ =:O)],'}";
        JSObject actual = new JSParser(input).parseObject();

        JSObject expected = new JSObject();
        expected.put("nickname", "\n\tnyaaaaaa\'aaa\'[((:’ –( :-)) :-| :~ =:O)],");

        assertEquals(expected, actual);
    }

    @Test
    public void stringify() throws Exception {
        for (TestUtils.TestData td : escapeTestData) {
            JSString jsString = new JSString(td.input);
            assertEquals(td.expected, jsString.toString());
        }
    }

    @Test
    public void parseUnescape() throws Exception {
        for (TestUtils.TestData td : parseUnescapeTestData) {
            JSValue actual = new JSParser(td.input).parse();
            assertEquals(td.expected, actual.getGeneralizedValue());
        }
    }

    @Test
    public void stringifyTestData() throws Exception {
        for (TestUtils.TestData td : stringifyTestData) {
            JSValue actual = new JSParser(td.input).parse();
            assertEquals(td.expected, actual.toString());
        }
    }
}
