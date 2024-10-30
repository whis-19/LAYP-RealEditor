package DAL;

import java.util.HashMap;
import java.util.Map;

public class Transliteration {

    // Updated mapping for Arabic letters to Roman Arabic letters
    private static final Map<Character, String> transliterationMap = new HashMap<>();

    static {
        transliterationMap.put('ا', "a");
        transliterationMap.put('آ', "aa");
        transliterationMap.put('ب', "b");
        transliterationMap.put('ت', "t");
        transliterationMap.put('ث', "th");
        transliterationMap.put('ج', "j"); // Can also be "g" depending on dialect
        transliterationMap.put('ح', "H"); // Aspirated "h"
        transliterationMap.put('خ', "kh");
        transliterationMap.put('د', "d");
        transliterationMap.put('ذ', "dh"); // "th" as in "this"
        transliterationMap.put('ر', "r");
        transliterationMap.put('ز', "z");
        transliterationMap.put('س', "s");
        transliterationMap.put('ش', "sh");
        transliterationMap.put('ص', "S"); // "S" with a darkened sound
        transliterationMap.put('ض', "D"); // "D" with a darkened sound
        transliterationMap.put('ط', "T"); // "T" with a darkened sound
        transliterationMap.put('ظ', "DH"); // Darkened "th" as in "the"
        transliterationMap.put('ع', "3"); // Represents "ع" sound
        transliterationMap.put('غ', "gh");
        transliterationMap.put('ف', "f");
        transliterationMap.put('ق', "q");
        transliterationMap.put('ك', "k");
        transliterationMap.put('ل', "l");
        transliterationMap.put('م', "m");
        transliterationMap.put('ن', "n");
        transliterationMap.put('ه', "h");
        transliterationMap.put('و', "w"); // Can be "uu" or "aw" in different contexts
        transliterationMap.put('ي', "y"); // Can be "ii" for long vowel
        transliterationMap.put('ء', "'"); // Glottal stop
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