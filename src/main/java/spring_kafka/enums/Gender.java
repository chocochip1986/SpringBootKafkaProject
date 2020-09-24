package spring_kafka.enums;

import java.util.Random;

public enum Gender {
    MALE("MALE"),
    FEMALE("FEMALE");

    private String name;

    Gender(String name) {
        this.name = name;
    }

    public static Gender pick() {
        return Gender.values()[new Random().nextInt(Gender.values().length)];
    }
}
