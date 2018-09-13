package io;

import org.junit.Test;

import java.util.Collection;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class FileHandlerTest {

    @Test
    public void readFile() {
        FileHandler handler = new FileHandler("src/test/resources/test.txt");
        Collection<String> content = handler.read();
        Iterator<String> iterator = content.iterator();
        while (iterator.hasNext()) {
            assertEquals("Hello world!", iterator.next());
        }
    }
}
