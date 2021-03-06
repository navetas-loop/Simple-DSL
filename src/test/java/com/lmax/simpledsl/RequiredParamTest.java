/*
 * Copyright 2011 LMAX Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.lmax.simpledsl;

import org.junit.Assert;
import org.junit.Test;

public class RequiredParamTest
{
    @Test
    public void aRequiredParamShouldIdentifyItselfAsSuch()
    {
        DslParam param = new RequiredParam("foo");
        Assert.assertSame(param, param.getAsRequiredParam());
        Assert.assertTrue(param.isRequired());
        Assert.assertNull(param.getAsOptionalParam());
    }

    @Test
    public void testConsumeAssignsAnArgumentToTheParameter()
    {
        RequiredParam param = new RequiredParam("foo");
        int position = param.consume(1, "goo = 0", "foo = 1", "bar = 2");
        Assert.assertEquals(2, position);
        Assert.assertEquals("1", param.getValue());
    }

    @Test
    public void testConsumeWorksCaseInsensitively()
    {
        RequiredParam param = new RequiredParam("foo");
        int position = param.consume(1, "goo = 0", "FOO = 1", "bar = 2");
        Assert.assertEquals(2, position);
        Assert.assertEquals("1", param.getValue());
    }

    @Test
    public void testRequiredParametersAreExtractedByPositionAndDoNotNeedToBeNamed()
    {
        RequiredParam param = new RequiredParam("foo");
        int position = param.consume(1, "0", "1", "2");
        Assert.assertEquals(2, position);
        Assert.assertEquals("1", param.getValue());
    }

    @Test
    public void testEdgeCaseOfConsumingTheFirstArgInTheList()
    {
        RequiredParam param = new RequiredParam("goo");
        int position = param.consume(0, "goo = 0", "bar = 1", "foo = 2");
        Assert.assertEquals(1, position);
        Assert.assertEquals("0", param.getValue());
    }

    @Test
    public void testEdgeCaseOfConsumingTheLastArgInTheList()
    {
        RequiredParam param = new RequiredParam("foo");
        int position = param.consume(2, "goo = 0", "bar = 1", "foo = 2");
        Assert.assertEquals(3, position);
        Assert.assertEquals("2", param.getValue());
    }

    @Test
    public void testConsumeThrowsAnExceptionIfThereAreNotEnoughParams()
    {
        RequiredParam param = new RequiredParam("foo");
        try
        {
            param.consume(1, "goo = 0");
            Assert.fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void testConsumeThrowsAnExceptionIfADifferentNamedParamIsThere()
    {
        RequiredParam param = new RequiredParam("foo");
        try
        {
            param.consume(1, "goo = 5", "bar = 3");
            Assert.fail("Expected IllegalArgumentException");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }

    @Test
    public void consumeConsumesMultipleParamsUpToTheFirstParamNamedDifferently()
    {
        RequiredParam param = new RequiredParam("foo").setAllowMultipleValues().getAsRequiredParam();
        int position = param.consume(1, "first param", "foo = 1", "2", "foo = 3", "4", "something else = 5");
        Assert.assertEquals(5, position);
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }

    @Test
    public void consumeCanConsumeMultipleParamsBySplittingValuesByCommaDelimiter()
    {
        RequiredParam param = new RequiredParam("foo").setAllowMultipleValues().getAsRequiredParam();
        int position = param.consume(1, "first param", "foo = 1, 2", "foo = 3", "4", "something else = 5");
        Assert.assertEquals(4, position);
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }

    @Test
    public void consumeCanConsumeMultipleParamsBySplittingValuesByUserSuppliedDelimiter()
    {
        RequiredParam param = new RequiredParam("foo").setAllowMultipleValues("\\|").getAsRequiredParam();
        int position = param.consume(1, "first param", "foo = 1| 2", "foo = 3", "4", "something else = 5");
        Assert.assertEquals(4, position);
        Assert.assertArrayEquals(new String[]{"1", "2", "3", "4"}, param.getValues());
    }
}
