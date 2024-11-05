
package DAL;

import java.util.HashMap;
import java.util.Map;

public class Transliteration {


    private static final Map<Character, String> transliterationMap = new HashMap<>();

    static {
        transliterationMap.put('ا', "a");
        transliterationMap.put('آ', "aa");
        transliterationMap.put('ب', "b");
        transliterationMap.put('ت', "t");
        transliterationMap.put('ث', "th");
        transliterationMap.put('ج', "j"); 
        transliterationMap.put('ح', "H"); 
        transliterationMap.put('خ', "kh");
        transliterationMap.put('د', "d");
        transliterationMap.put('ذ', "dh");
        transliterationMap.put('ر', "r");
        transliterationMap.put('ز', "z");
        transliterationMap.put('س', "s");
        transliterationMap.put('ش', "sh");
        transliterationMap.put('ص', "S"); 
        transliterationMap.put('ض', "D"); 
        transliterationMap.put('ط', "T"); 
        transliterationMap.put('ظ', "DH"); 
        transliterationMap.put('ع', "3"); 
        transliterationMap.put('غ', "gh");
        transliterationMap.put('ف', "f");
        transliterationMap.put('ق', "q");
        transliterationMap.put('ك', "k");
        transliterationMap.put('ل', "l");
        transliterationMap.put('م', "m");
        transliterationMap.put('ن', "n");
        transliterationMap.put('ه', "h");
        transliterationMap.put('و', "w"); 
        transliterationMap.put('ي', "y"); 
        transliterationMap.put('ء', "'");
        // Adding vowels for pronunciation
        transliterationMap.put('َ', "a");
        transliterationMap.put('ُ', "u");
        transliterationMap.put('ِ', "i");
    }


    public String transliterate(String arabicText) {
        StringBuilder romanText = new StringBuilder();
        boolean lastCharWasSpace = false;

        for (char ch : arabicText.toCharArray()) {
            if (ch == ' ') {
                if (!lastCharWasSpace) {
                    romanText.append(" ");
                    lastCharWasSpace = true;
                }
                continue;
            }

            if (!transliterationMap.containsKey(ch))
                continue;

            String romanEquivalent = transliterationMap.get(ch);
            romanText.append(romanEquivalent);
            lastCharWasSpace = false;
        }

        // Formatting words and capitalizing each word's first letter
        String[] words = romanText.toString().split(" ");
        StringBuilder formattedText = new StringBuilder();

        for (String word : words) {
            if (word.isEmpty())
                continue;
            formattedText.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1)).append(" ");
        }

        return formattedText.toString().trim();
    }
}
