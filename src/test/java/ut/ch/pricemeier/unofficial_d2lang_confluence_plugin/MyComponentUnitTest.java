package ut.ch.pricemeier.unofficial_d2lang_confluence_plugin;

import org.junit.Test;
import ch.pricemeier.unofficial_d2lang_confluence_plugin.api.MyPluginComponent;
import ch.pricemeier.unofficial_d2lang_confluence_plugin.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl(null);
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}