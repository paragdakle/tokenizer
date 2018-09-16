package io;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class FileHandlerTest {

    @Test
    public void readFile() {
        FileHandler handler = new FileHandler("src/test/resources/simple.txt");
        Map<String, String> content = handler.read();
        content.forEach((key, value) -> {
            assertEquals("Hello world!", value);
        });
    }

    @Test
    public void readDir() {
        FileHandler handler = new FileHandler("src/test/resources");
        Map<String, String> content = handler.read();
        content.forEach((key, value) -> {
            assertEquals("Hello world!", value);
        });
    }
}
