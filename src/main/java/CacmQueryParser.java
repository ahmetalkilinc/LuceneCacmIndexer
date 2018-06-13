import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CacmQueryParser {

    private static Map<String, String> allQuery = new HashMap<>();

    public CacmQueryParser() {
        System.out.println("Query Parser...");
    }


    public Map<String, String> QueryParse(String queryFile) throws IOException {
        String result = "";
        String id = "";
        String queryText = "";
        String allQueryText = "";
        int queryCount = 0;

        String lines = "";
        char state = 0;

        BufferedReader breader = Files.newBufferedReader(Paths.get(queryFile), StandardCharsets.UTF_8);
        int i = 0;


        while ((lines = breader.readLine()) != null) {
            if ((lines = lines.trim()).isEmpty() && queryText.length() == 0) {
                continue;
            }
            if ((lines = lines.trim()).isEmpty() && queryText.length() > 0) {
                lines = ".W";
            }
            if (lines.charAt(0) == Fields.PREFIX) {
                state = lines.charAt(1);

                if (id.length() > 0 && queryText.length() > 0) {
                    i++;
                    allQuery.put(String.valueOf(i), queryText);
                    queryCount++;
                    allQueryText += queryText;

                    queryText = "";
                }

                if (state == Fields.ID) {
                    id = lines.substring(2).trim();
                }


            } else {
                if (state == Fields.QUERY) {
                    queryText += " " + lines;
                }


            }


               /*
                if (state == Fields.AUTHORS) {
                    queryText += " " + lines;

                }
                if (state == Fields.SOURCE) {
                    queryText += " " + lines;

                }*/


        }


        breader.close();


        //Query statistics

        String[] wordArray = allQueryText.trim().split("\\s+");
        int wordCount = wordArray.length;
        long avg_query_length = wordCount / queryCount;

        System.out.println("Word count is = " + wordCount + " \nquery count is = " + queryCount + " \navg_query_length is = " + avg_query_length);


        allQuery = allQuery.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        return allQuery;
    }


    private class Fields {

        private static final char PREFIX = '.';
        private static final char QUERY = 'W';
        private static final char ID = 'I';
        private static final char AUTHORS = 'A';
        private static final char SOURCE = 'N';
    }
}
