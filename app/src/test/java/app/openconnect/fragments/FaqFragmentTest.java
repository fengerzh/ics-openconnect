package app.openconnect.fragments;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import app.openconnect.TestApplication;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 35, application = TestApplication.class)
public class FaqFragmentTest {

    private FaqFragment fragment = new FaqFragment();

    @Test
    public void htmlEncodePlainText() {
        assertEquals("hello", fragment.htmlEncode("hello"));
    }

    @Test
    public void htmlEncodeWithNewline() {
        assertEquals("hello<br>world", fragment.htmlEncode("hello\nworld"));
    }

    @Test
    public void htmlEncodeWithLink() {
        String result = fragment.htmlEncode("click [here](http://example.com) now");
        assertTrue(result.contains("<a href=\"http://example.com\">here</a>"));
        assertTrue(result.contains("click "));
        assertTrue(result.contains(" now"));
    }

    @Test
    public void htmlEncodeWithMultipleLinks() {
        String result = fragment.htmlEncode("[a](http://a.com) and [b](http://b.com)");
        assertTrue(result.contains("<a href=\"http://a.com\">a</a>"));
        assertTrue(result.contains("<a href=\"http://b.com\">b</a>"));
    }

    @Test
    public void htmlEncodeWithSpecialChars() {
        // TextUtils.htmlEncode should escape < > & "
        String result = fragment.htmlEncode("a < b & c > d");
        assertTrue(result.contains("&lt;"));
        assertTrue(result.contains("&amp;"));
        assertTrue(result.contains("&gt;"));
    }

    @Test
    public void htmlEncodeEmptyString() {
        assertEquals("", fragment.htmlEncode(""));
    }

    @Test
    public void htmlEncodeLinkOnly() {
        String result = fragment.htmlEncode("[link](http://test.com)");
        assertEquals("<a href=\"http://test.com\">link</a>", result);
    }
}