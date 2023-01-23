package ut.ch.pricemeier;

import org.junit.Test;
import ch.pricemeier.api.MyPluginComponent;
import ch.pricemeier.impl.MyPluginComponentImpl;

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