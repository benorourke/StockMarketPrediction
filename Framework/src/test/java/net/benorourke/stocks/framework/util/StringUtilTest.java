package net.benorourke.stocks.framework.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


public class StringUtilTest
{

    @Test
    public void camelCaseToWordsUnformatted_WordLengths1_ShouldEqualExpected()
    {
        String value = "aBCD";

        List<String> actual = StringUtil.camelCaseToWords(value, false);
        List<String> expected = Arrays.asList("a", "B", "C", "D");

        assertThat(actual, is(expected));
    }

    @Test
    public void camelCaseToWordsFormatted_WordLengths1_ShouldEqualExpected()
    {
        String value = "aBCD";

        List<String> actual = StringUtil.camelCaseToWords(value, true);
        List<String> expected = Arrays.asList("A", "B", "C", "D");

        assertThat(actual, is(expected));
    }

    @Test
    public void camelCaseToWordsUnformatted_WordLengthsGreaterThan1_ShouldEqualExpected()
    {
        String value = "thisIsATest";

        List<String> actual = StringUtil.camelCaseToWords(value, false);
        List<String> expected = Arrays.asList("this", "Is", "A", "Test");

        assertThat(actual, is(expected));
    }

    @Test
    public void camelCaseToWordsFormatted_WordLengthsGreaterThan1_ShouldEqualExpected()
    {
        String value = "thisIsATest";

        List<String> actual = StringUtil.camelCaseToWords(value, true);
        List<String> expected = Arrays.asList("This", "Is", "A", "Test");

        assertThat(actual, is(expected));
    }

    @Test
    public void camelCaseToWordsUnformatted_ContainsNumbers_ShouldEqualExpected()
    {
        String value = "thisIsATest1";

        List<String> actual = StringUtil.camelCaseToWords(value, false);
        List<String> expected = Arrays.asList("this", "Is", "A", "Test1");

        assertThat(actual, is(expected));
    }

    @Test
    public void camelCaseToWordsFormatted_ContainsNumbers_ShouldEqualExpected()
    {
        String value = "thisIsATest2";

        List<String> actual = StringUtil.camelCaseToWords(value, true);
        List<String> expected = Arrays.asList("This", "Is", "A", "Test2");

        assertThat(actual, is(expected));
    }

}
