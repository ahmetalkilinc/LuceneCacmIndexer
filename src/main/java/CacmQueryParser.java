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

        String lines = "";
        char state = 0;

        BufferedReader breader = Files.newBufferedReader(Paths.get(queryFile), StandardCharsets.UTF_8);
        int i=0;
        while ((lines = breader.readLine()) != null) {

            if ((lines = lines.trim()).isEmpty()) {
                continue;
            }
            if (lines.charAt(0) == Fields.PREFIX) {
                state = lines.charAt(1);
                if (state == Fields.ID) {
                    if (id.length() > 0 && queryText.length() > 0) {
                        i++;
                        allQuery.put(String.valueOf(i), queryText);
                        queryText = "";
                    }
                    
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
