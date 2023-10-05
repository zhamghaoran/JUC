import junit.framework.TestCase;
import org.jjking.Main;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

public class TestMainTest {

    @Test
    public void testAddPositiveNumbers() {
        ArrayList<String> list = new ArrayList<>();
        list.add("1");
        list.add("3");
        list.add("2");
        list.add("4");
        list.add("5");
        System.out.println("正向遍历结果:");
        // 正向遍历
        ListIterator<String> listIterator = list.listIterator();
        while (listIterator.hasNext()) {
            System.out.println(listIterator.next());
        }
        System.out.println("反向遍历结果:");
        // 反向遍历
        while (listIterator.hasPrevious()) {
            System.out.println(listIterator.previous());
        }

        System.out.println("插入元素：");
        //插入元素
        ListIterator<String> listIterator1 = list.listIterator();
        listIterator1.add("7");
        while (listIterator1.hasPrevious()) {
            System.out.println(listIterator1.previous());

        }
        System.out.println(list);
        System.out.println();

        ListIterator<String> listIterator2 = list.listIterator();

        System.out.println("替换元素:");
        // 替换元素
        while (listIterator2.hasNext()) {
            String next = listIterator2.next();
            if ("5".equals(next)) {
                listIterator2.set("155");
            }
        }
        System.out.println(list);
    }

    @Test
    public void testAddNegativeNumbers() {
        String result = Main.add("Hello", "123");
        assertEquals("Hello123", result);
    }
}