import junit.framework.TestCase;
import org.jjking.Main;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestMainTest {

    @Test
    public void testAddPositiveNumbers() {
        String result = Main.add("Hello", "World");
        assertEquals("HelloWorld", result);
    }

    @Test
    public void testAddNegativeNumbers() {
        String result = Main.add("Hello", "123");
        assertEquals("Hello123", result);
    }
}