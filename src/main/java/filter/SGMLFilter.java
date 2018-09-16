package filter;

import java.util.HashMap;

public class SGMLFilter implements IFilter {

    HashMap<String, String> filterRegexMap;

    public SGMLFilter() {
        this.filterRegexMap = new HashMap<String, String>();
    }

    public void addRegex(String regex, String replaceText) {
        filterRegexMap.put(regex, replaceText);
    }

    public String filter(String text) {
        for(String key: filterRegexMap.keySet()) {
            text = text.replaceAll(key, filterRegexMap.get(key));
        }
        return text.trim();
    }
}
