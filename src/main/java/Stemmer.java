import opennlp.tools.stemmer.PorterStemmer;

import java.util.*;
import java.util.stream.Collectors;

public class Stemmer {

    private final byte TOTAL_STEMS_INDEX = 0;
    private final byte UNIQUE_STEMS_INDEX = 1;
    private final byte ONCE_STEMS_INDEX = 2;
    private final byte AVG_STEMS_DOC_INDEX = 3;

    private Map<String, List<String>> stemMap;

    private PorterStemmer porterStemmer;

    public Stemmer() {
        this.stemMap = new HashMap<>();
        porterStemmer = new PorterStemmer();
    }

    public Map<String, List<String>> getStemMap() {
        return this.stemMap;
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

    private double[] generateStemmerStats(Map<String, List<String>> tokenMap, Map<String, Integer> top30Stems) {
        double[] stats = {0.0, 0.0, 0.0, 0.0};
        HashMap<String, Integer> stem2Frequency = new HashMap<>();
        for(String document: tokenMap.keySet()) {
            stats[TOTAL_STEMS_INDEX] += tokenMap.get(document).size();
            for(String token: tokenMap.get(document)) {
                if(stem2Frequency.containsKey(token)) {
                    stem2Frequency.put(token, stem2Frequency.get(token) + 1);
                }
                else {
                    stem2Frequency.put(token, 1);
                }
            }
        }
        stats[AVG_STEMS_DOC_INDEX] = stats[TOTAL_STEMS_INDEX] / tokenMap.size();
        stats[UNIQUE_STEMS_INDEX] = stem2Frequency.size();
        Map<String, Integer> sortedMap =
                stem2Frequency.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue((o1, o2) -> -o1.compareTo(o2)))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                                (e1, e2) -> e1, LinkedHashMap::new));
        int counter = 0;
        for(String key: sortedMap.keySet()) {
            Integer value = sortedMap.get(key);
            if(value == 1.0) {
                stats[ONCE_STEMS_INDEX] += 1;
            }
            if(counter < 30) {
                top30Stems.put(key, value);
            }
            counter++;
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
        stemmer.prettyPrintStatistics(stats, top30Stems);
    }

    private void prettyPrintStatistics(double[] stats, Map<String, Integer> top30StemsMap) {
        System.out.println("\n\n");
        System.out.format("%60s%n", "Stemming Statistics");
        System.out.format("%75s%n", "------------------------------------------------------");
        System.out.format("%25s%13s%35s%n", "No.", "Statistic", "Value");
        System.out.format("%25s%19s%30.0f%n", "1.", "Number of stems", stats[TOTAL_STEMS_INDEX]);
        System.out.format("%25s%26s%23.0f%n", "2.", "Number of unique stems", stats[UNIQUE_STEMS_INDEX]);
        System.out.format("%25s%41s%8.0f%n", "3.", "Number of stems which occur only once", stats[ONCE_STEMS_INDEX]);
        System.out.format("%25s%32s%20.2f%n", "4.", "Average number of word stems", stats[AVG_STEMS_DOC_INDEX]);
        System.out.format("%25s%29s%n", "5.", "Top 30 frequent stems are");
        top30StemsMap.forEach((key, value) -> System.out.format("%30s%1s%"+ (47 - ((String)key).length() - 3) + "d%n", "-", key, value));
    }
}
