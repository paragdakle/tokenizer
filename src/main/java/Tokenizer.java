import filter.IFilter;
import filter.SGMLFilter;
import io.FileHandler;
import lombok.Getter;
import utils.Constants;

import java.util.*;
import java.util.stream.Collectors;

public class Tokenizer implements Constants {

    private final byte TOTAL_TOKENS_INDEX = 0;
    private final byte UNIQUE_WORDS_INDEX = 1;
    private final byte ONCE_WORDS_INDEX = 2;
    private final byte AVG_WORDS_DOC_INDEX = 3;

    @Getter
    private Map<String, List<String>> tokenMap;

    public Tokenizer() {
        this.tokenMap = new HashMap<>();
    }

    public void tokenize(String dataPath, IFilter filter, boolean doFormatting) {
        FileHandler handler = new FileHandler(dataPath, filter, doFormatting);
        Map<String, String> content = handler.read();
        content.forEach((filename, contents) -> {
            String[] contentSplit = contents.split(" ");
            tokenMap.put(filename, new ArrayList<>());
            for (String item : contentSplit) {
                if(!item.trim().equals("")) {
                    tokenMap.get(filename).add(item);
                }
            }
        });
    }

    public IFilter getFilter() {
        SGMLFilter filter = new SGMLFilter();
        filter.addRegex(SGML_TAG_REGEX, "");
        filter.addRegex(POSSESSIVE_REGEX, "");
        filter.addRegex(SPECIAL_CHARACTER_REGEX, " ");
        filter.addRegex(NUMBER_REGEX, " ");
        filter.addRegex(MULTISPACE_REGEX, " ");
        return filter;
    }

    public static void main(String[] args) {

        long time = System.currentTimeMillis();
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.tokenize("data/", tokenizer.getFilter(), true);
        Map<String, Integer> top30Tokens = new LinkedHashMap<>();
        double[] stats = tokenizer.generateTokenizerStats(tokenizer.tokenMap, top30Tokens);
        System.out.println("Number of tokens: " + stats[tokenizer.TOTAL_TOKENS_INDEX]);;
        System.out.println("Number of unique tokens: " + stats[tokenizer.UNIQUE_WORDS_INDEX]);;
        System.out.println("Number of words which occur only once: " + stats[tokenizer.ONCE_WORDS_INDEX]);
        System.out.println("Average number of word tokens: " + stats[tokenizer.AVG_WORDS_DOC_INDEX]);
        System.out.println("Top 30 frequent tokens are: ");
        top30Tokens.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println(System.currentTimeMillis() - time);
    }

    private double[] generateTokenizerStats(Map<String, List<String>> tokenMap, Map<String, Integer> top30Tokens) {
        double[] stats = {0.0, 0.0, 0.0, 0.0};
        HashMap<String, Integer> token2Frequency = new HashMap<>();
        for(String document: tokenMap.keySet()) {
            stats[TOTAL_TOKENS_INDEX] += tokenMap.get(document).size();
            for(String token: tokenMap.get(document)) {
                if(token2Frequency.containsKey(token)) {
                    token2Frequency.put(token, token2Frequency.get(token) + 1);
                }
                else {
                    token2Frequency.put(token, 1);
                }
            }
        }
        stats[AVG_WORDS_DOC_INDEX] = stats[TOTAL_TOKENS_INDEX] / tokenMap.size();
        stats[UNIQUE_WORDS_INDEX] = token2Frequency.size();
        Map<String, Integer> sortedMap =
                token2Frequency.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue())
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
        int counter = sortedMap.size();
        for(String key: sortedMap.keySet()) {
            Integer value = sortedMap.get(key);
            if(value == 1.0) {
                stats[ONCE_WORDS_INDEX] += 1;
            }
            if(counter <= 30) {
                top30Tokens.put(key, value);
            }
            counter--;
        }
        return stats;
    }
}
