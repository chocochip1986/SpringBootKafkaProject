package spring_kafka.utilities;

import com.github.javafaker.Faker;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@Slf4j
public class PhakerNric {
    private final String[] NRIC_FIRST_CHAR = {"S", "T"};
    private final Map<String, String> NRIC_MAP = createNricMap();

    private Set<String> usedValidNrics = new HashSet<>();
    private final Random rand = new Random();
    private final Faker faker = new Faker();
    private int count = 0;
    private int maxCount = 10000000;
    private int NRIC_FIRST_CHAR_POINTER = 0;

    public static PhakerNric instance() {
        return new PhakerNric();
    }

    public PhakerNric() {
        reset();
    }

    public void reset() {
        this.usedValidNrics = new HashSet<>();
        this.count = 0;
        this.NRIC_FIRST_CHAR_POINTER = 0;
    }

    public String validNric() {
        int count = 0;
        int retryCount = 50000000;
        String generatedNric = null;
        while (count < retryCount) {
            String nric = followNric();
            if (nric != null &&
                    nric.matches("[ST]\\d{7}[A-Z]") &&
                    nric.length() != 9 &&
                    !(nric.startsWith("S555") || nric.startsWith("S888")) &&
                    !usedValidNrics.contains(nric)) {
                usedValidNrics.add(nric);
                generatedNric = nric;
                break;
            }
            count++;
        }
        if (generatedNric == null) {
            log.error("Unable to generate a unique valid nric!!!");
            return null;
        }
        return generatedNric;
    }

    public String nric() {
        String firstChar = randomItemFromArray(NRIC_FIRST_CHAR);
        String midNum = faker.number().digits(7);
        return firstChar + midNum + validChecksum(firstChar, midNum, NRIC_MAP);
    }

    public String followNric() {
        if ( this.NRIC_FIRST_CHAR[this.NRIC_FIRST_CHAR_POINTER].equals("T") && this.count == 9999999 ) {
            return "";
        } else {
            String firstChar = suggestFirstChar();
            String midNum = suggestMidNum();
            return firstChar + midNum + validChecksum(firstChar, midNum, NRIC_MAP);
        }
    }

    public String suggestMidNum() {
        return StringUtils.leftPad(String.valueOf(++this.count), 7, "0");
    }

    public String suggestFirstChar() {
        if ( shouldSwitchFirstChar() ) {
            this.NRIC_FIRST_CHAR_POINTER+=1;
        }
        return this.NRIC_FIRST_CHAR[this.NRIC_FIRST_CHAR_POINTER];
    }

    public boolean shouldSwitchFirstChar() {
        if ( this.NRIC_FIRST_CHAR_POINTER < NRIC_FIRST_CHAR.length-1 && this.count >= this.maxCount ) {
            return true;
        } else
        {
            return false;
        }
    }

    public <E> E randomItemFromArray(E[] input) {
        return input[rand.nextInt(input.length)];
    }

    private Map<String, String> createNricMap() {
        Map<String, String> map = new HashMap<>();
        map.put("0", "A");
        map.put("1", "B");
        map.put("2", "C");
        map.put("3", "D");
        map.put("4", "E");
        map.put("5", "F");
        map.put("6", "G");
        map.put("7", "H");
        map.put("8", "I");
        map.put("9", "Z");
        map.put("10", "J");

        return map;
    }

    private String validChecksum(String firstChar, String digits, Map<String, String> map) {
        int checksum =
                (2 * Character.getNumericValue(digits.charAt(0))
                        + 7 * Character.getNumericValue(digits.charAt(1))
                        + 6 * Character.getNumericValue(digits.charAt(2))
                        + 5 * Character.getNumericValue(digits.charAt(3))
                        + 4 * Character.getNumericValue(digits.charAt(4))
                        + 3 * Character.getNumericValue(digits.charAt(5))
                        + 2 * Character.getNumericValue(digits.charAt(6)));

        if ("TG".contains(firstChar)) {
            checksum += 4;
        }

        int remainder = 10 - checksum % 11;
        return map.get(String.valueOf(remainder));
    }
}
