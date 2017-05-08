package daris.client.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ConsoleUtils {

    public static String readStringFromConsole(String prompt) throws Throwable {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.print(prompt);
        String v = br.readLine();
        if (v != null) {
            return v.trim();
        }
        return v;
    }

    public static int readIntegerFromConsole(String prompt) throws Throwable {
        String s = readStringFromConsole(prompt);
        return Integer.parseInt(s.trim());
    }
}
