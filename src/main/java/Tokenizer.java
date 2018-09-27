import filter.IFilter;
import filter.SGMLFilter;
import io.FileHandler;

import java.sql.SQLOutput;
import java.util.*;
import java.util.stream.Collectors;

public class Tokenizer {

    private final byte TOTAL_TOKENS_INDEX = 0;
    private final byte UNIQUE_WORDS_INDEX = 1;
    private final byte ONCE_WORDS_INDEX = 2;
    private final byte AVG_WORDS_DOC_INDEX = 3;

    private Map<String, List<String>> tokenMap;

    private IFilter filter;

    public Tokenizer() {
        this.tokenMap = new HashMap<>();
        filter = new SGMLFilter();
    }

    public Map<String, List<String>> getTokenMap() {
        return this.tokenMap;
    }

    public IFilter getFilter() {
        return this.filter;
    }

    public void tokenize(String dataPath, IFilter filter, boolean doFormatting) {
        if(filter == null) {
            return;
        }
        filter.construct();
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

    public static void main(String[] args) {

        long time = System.currentTimeMillis();
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.tokenize("data/", tokenizer.filter, true);
        Map<String, Integer> top30Tokens = new LinkedHashMap<>();
        double[] stats = tokenizer.generateTokenizerStats(tokenizer.tokenMap, top30Tokens);
        tokenizer.prettyPrintStatistics(stats, top30Tokens);
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
                        .sorted(Map.Entry.comparingByValue((o1, o2) -> -o1.compareTo(o2)))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
        int counter = 0;
        for(String key: sortedMap.keySet()) {
            Integer value = sortedMap.get(key);
            if(value == 1.0) {
                stats[ONCE_WORDS_INDEX] += 1;
            }
            if(counter < 30) {
                top30Tokens.put(key, value);
            }
            counter++;
        }
        return stats;
    }

    private void prettyPrintStatistics(double[] stats, Map<String, Integer> top30TokensMap) {
        System.out.println("\n\n");
        System.out.format("%60s%n", "Tokenization Statistics");
        System.out.format("%75s%n", "------------------------------------------------------");
        System.out.format("%25s%12s%36s%n", "No.", "Statistic", "Value");
        System.out.format("%25s%19s%30.0f%n", "1.", "Number of tokens", stats[TOTAL_TOKENS_INDEX]);
        System.out.format("%25s%26s%23.0f%n", "2.", "Number of unique tokens", stats[UNIQUE_WORDS_INDEX]);
        System.out.format("%25s%40s%9.0f%n", "3.", "Number of words which occur only once", stats[ONCE_WORDS_INDEX]);
        System.out.format("%25s%32s%20.2f%n", "4.", "Average number of word tokens", stats[AVG_WORDS_DOC_INDEX]);
        System.out.format("%25s%29s%n", "5.", "Top 30 frequent tokens are");
        top30TokensMap.forEach((key, value) -> System.out.format("%29s%1s%"+ (48 - ((String)key).length() - 3) + "d%n", "-", key, value));
    }
}
