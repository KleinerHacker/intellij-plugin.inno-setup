package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.util.TextRange;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssRegexUtilsTest {

    @Test
    public void testInternalConstantsSimple() {
        final List<TextRange> textRangeList = new ArrayList<>();
        IssRegexUtils.findInternalConstants("ab{123}de", textRangeList::add);

        Assert.assertEquals(1, textRangeList.size());
        Assert.assertEquals(new TextRange(2,7), textRangeList.get(0));
    }

    @Test
    public void testInternalConstantsComplex() {
        final List<TextRange> textRangeList = new ArrayList<>();
        IssRegexUtils.findInternalConstants("{123}ab{345}de{890}", textRangeList::add);

        Assert.assertEquals(3, textRangeList.size());
        Assert.assertEquals(new TextRange(0,5), textRangeList.get(0));
        Assert.assertEquals(new TextRange(7,12), textRangeList.get(1));
        Assert.assertEquals(new TextRange(14,19), textRangeList.get(2));
    }
}
