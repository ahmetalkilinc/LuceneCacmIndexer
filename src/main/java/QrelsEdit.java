import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class QrelsEdit {


    public static void main(String[] args) throws IOException {
        String line = "";
        String lastValue = "";
        BufferedReader br = Files.newBufferedReader(Paths.get("data/cran/cran.qrels"));

        List<String> lines = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            lastValue = ConvertLastValue(line);

            line = line.substring(0, line.length() - 2);
            line=line.trim();
            line += " " + lastValue.trim();
            lines.add(line.trim());

        }

        Files.write(Paths.get("data/cran/newCran.qrels"), lines);

        System.out.println("File format converted.");
    }


    public static String ConvertLastValue(String value) throws IOException {

        String line = value;

        line = line.substring(line.length() - 2).trim().toString();

        if (line.equals("-1")) {
            line = "5";
            return line;
        }
        if (line.equals("1")) {
            line = "4";
            return line;
        }
        if (line.equals("2")) {
            line = "3";
            return line;
        }
        if (line.equals("3")) {
            line = "2";
            return line;
        }
        if (line.equals("4")) {
            line = "1";
            return line;
        }


        return line;
    }

}
