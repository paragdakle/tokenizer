import lombok.Getter;
import opennlp.tools.stemmer.PorterStemmer;

import java.util.*;
import java.util.stream.Collectors;

public class Stemmer {

    private final byte TOTAL_STEMS_INDEX = 0;
    private final byte UNIQUE_STEMS_INDEX = 1;
    private final byte ONCE_STEMS_INDEX = 2;
    private final byte AVG_STEMS_DOC_INDEX = 3;

    @Getter
    private Map<String, List<String>> stemMap;

    private PorterStemmer porterStemmer;

    public Stemmer() {
        this.stemMap = new HashMap<>();
        porterStemmer = new PorterStemmer();
    }

    public void stem(Map<String, List<String>> tokenMap) {
        tokenMap.forEach((document, tokenList) -> {
            stemMap.put(document, new ArrayList<>());
            tokenList.stream()
                    .map((item) -> porterStemmer.stem(item))
                    .filter(Objects::nonNull)
                    .filter((item) -> !item.isEmpty())
                    .forEach(stemMap.get(document)::add);
        });
    }

    private double[] generateStemmerStats(Map<String, List<String>> tokenMap, Map<String, Integer> top30Tokens) {
        double[] stats = {0.0, 0.0, 0.0, 0.0};
        HashMap<String, Integer> token2Frequency = new HashMap<>();
        for(String document: tokenMap.keySet()) {
            stats[TOTAL_STEMS_INDEX] += tokenMap.get(document).size();
            for(String token: tokenMap.get(document)) {
                if(token2Frequency.containsKey(token)) {
                    token2Frequency.put(token, token2Frequency.get(token) + 1);
                }
                else {
                    token2Frequency.put(token, 1);
                }
            }
        }
        stats[AVG_STEMS_DOC_INDEX] = stats[TOTAL_STEMS_INDEX] / tokenMap.size();
        stats[UNIQUE_STEMS_INDEX] = token2Frequency.size();
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
                stats[ONCE_STEMS_INDEX] += 1;
            }
            if(counter <= 30) {
                top30Tokens.put(key, value);
            }
            counter--;
        }
        return stats;
    }

    public static void main(String[] args) {

        Stemmer stemmer = new Stemmer();
        Tokenizer tokenizer = new Tokenizer();
        tokenizer.tokenize("data/", tokenizer.getFilter(), true);
        Map<String, List<String>> tokenMap = tokenizer.getTokenMap();
        stemmer.stem(tokenMap);
        Map<String, Integer> top30Stems = new LinkedHashMap<>();
        double[] stats = stemmer.generateStemmerStats(stemmer.stemMap, top30Stems);
        System.out.println("Number of tokens: " + stats[stemmer.TOTAL_STEMS_INDEX]);;
        System.out.println("Number of unique tokens: " + stats[stemmer.UNIQUE_STEMS_INDEX]);;
        System.out.println("Number of words which occur only once: " + stats[stemmer.ONCE_STEMS_INDEX]);
        System.out.println("Average number of word tokens: " + stats[stemmer.AVG_STEMS_DOC_INDEX]);
        System.out.println("Top 30 frequent tokens are: ");
        top30Stems.forEach((key, value) -> System.out.println(key + ": " + value));
    }
}
