package filter;

import io.FileHandler;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static utils.Constants.*;

public class FilterTest {

    @Test
    public void readFile() {
        SGMLFilter filter = new SGMLFilter();
        filter.addRegex(SGML_TAG_REGEX, "");
        filter.addRegex(SPECIAL_CHARACTER_REGEX, " ");
        filter.addRegex(NUMBER_REGEX, " ");
        FileHandler handler = new FileHandler("src/test/resources/filter.txt", filter);
        Map<String, String> content = handler.read();
        for(String key: content.keySet()) {
            assertEquals("experimental investigation of the aerodynamics of a wing in a slipstream", content.get(key));
            break;
        }
    }
}
