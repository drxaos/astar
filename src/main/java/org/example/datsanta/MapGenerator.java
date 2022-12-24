package org.example.datsanta;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class MapGenerator {

    public static void main(String[] args) {
        final DsMap generate = new MapGenerator().generate();
        System.out.println(generate);
    }

    private static final int CHILD_COUNT = 1000;

    private static final int GIFT_COUNT = 1000;
    private static final int GIFT_MIN_W = 1;
    private static final int GIFT_MAX_W = 10;
    private static final int GIFT_MIN_V = 1;
    private static final int GIFT_MAX_V = 15;

    private static final int SNOW_AREA_COUNT = 100;
    private static final int SNOW_AREA_MIN_R = 100;
    private static final int SNOW_AREA_MAX_R = 800;

    private static final int MAX_MAP_X = 10000;
    private static final int MAX_MAP_Y = 10000;

    public DsMap generate() {
        List<Gift> gifts = generateGifts();
        List<Child> childs = generateChilds();
        List<SnowArea> snowAreas = generateSnowAreas();

        return new DsMap(gifts, childs, snowAreas);
    }

    private List<Gift> generateGifts() {
        List<Gift> result = new ArrayList<>();

        for (int i = 0; i < GIFT_COUNT; i++) {
            result.add(
                new Gift(
                    i,
                    generateRandomInt(GIFT_MIN_W, GIFT_MAX_W),
                    generateRandomInt(GIFT_MIN_V, GIFT_MAX_V)
                )
            );
        }

        return result;
    }

    private List<Child> generateChilds() {
        List<Child> result = new ArrayList<>();

        for (int i = 0; i < CHILD_COUNT; i++) {
            result.add(
                new Child(
                    generateRandomInt(1, MAX_MAP_X),
                    generateRandomInt(1, MAX_MAP_Y)
                )
            );
        }

        return result;
    }

    private List<SnowArea> generateSnowAreas() {
        List<SnowArea> result = new ArrayList<>();

        for (int i = 0; i < SNOW_AREA_COUNT; i++) {
            result.add(
                new SnowArea(
                    generateRandomInt(SNOW_AREA_MIN_R, SNOW_AREA_MAX_R),
                    generateRandomInt(1, MAX_MAP_X),
                    generateRandomInt(1, MAX_MAP_Y)
                )
            );
        }

        return result;
    }

    private int generateRandomInt(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }

}
