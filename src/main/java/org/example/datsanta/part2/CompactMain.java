package org.example.datsanta.part2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import static org.example.datsanta.part2.Gender.female;
import static org.example.datsanta.part2.Gender.male;
import static org.example.datsanta.part2.GiftType.*;

public class CompactMain {
    static int sum = 0;
    static int happy = 0;
    static String mapId = "a8e01288-28f8-45ee-9db4-f74fc4ff02c8";

//
//    @SneakyThrows
//    public static void main(String[] args) {
//        final Step2Map step2Map = new ObjectMapper().readValue(new File("part2_gift.json"), Step2Map.class);
////        System.out.println(step2Map);
//        Map<ChildType, List<Child2>> types =
//                new TreeMap<>(step2Map.children.stream().collect(Collectors.groupingBy(c -> new ChildType(c.getGender(), c.getAge()))));
////        System.out.println(types);
//
//        List<Object[]> objects = step2Map.gifts.stream().collect(Collectors.groupingBy(g -> g.type)).entrySet().stream().map(e -> {
//            return new Object[]{
//                    e.getKey(),
//                    e.getValue().stream().mapToInt(Gift2::getPrice).min().getAsInt(),
//                    e.getValue().stream().mapToInt(Gift2::getPrice).max().getAsInt(),
//            };
//        }).toList();
//
//        Map<ChildType, Map<GiftType, Double>> best = new HashMap<>();
//        best.put(new ChildType(male, 0), Map.of(toy_vehicles, 0.130, playground, 0.063, outdoor_games, 0.054, soft_toys, 0.051, sweets, 0.041, clothes, 0.0));
//        best.put(new ChildType(male, 1), Map.of(sweets, 0.441, toy_vehicles, 0.4, playground, 0.185, soft_toys, 0.054));
//        best.put(new ChildType(male, 2), Map.of(radio_controlled_toys, 0.408, sweets, 0.4, playground, 0.2, toy_vehicles, 0.408));
//        best.put(new ChildType(male, 3), Map.of(sweets, 0.578, toy_vehicles, 0.4, playground, 0.253, soft_toys, 0.106));
//        best.put(new ChildType(male, 4), Map.of(computer_games, 0.602, toy_vehicles, 0.403, sweets, 0.5, constructors, 0.106, radio_controlled_toys, 0.054, board_games, 0.161, outdoor_games, 0.210, playground, 0.247, soft_toys, 0.108, books, 0.21));
//        best.put(new ChildType(male, 5), Map.of(computer_games, 0.798, sweets, 0.566, toy_vehicles, 0.530, outdoor_games, 0.445, constructors, 0.21, board_games, 0.316, playground, 0.183, books, 0.34));
//        best.put(new ChildType(male, 6), Map.of(computer_games, 0.803, sweets, 0.571, outdoor_games, 0.451, constructors, 0.211, board_games, 0.32, playground, 0.187, books, 0.329));
//        best.put(new ChildType(male, 7), Map.of(computer_games, 0.8, board_games, 0.311, constructors, 0.210, books, 0.333, playground, 0.189));
//        best.put(new ChildType(male, 8), Map.of(computer_games, 0.804, board_games, 0.424, radio_controlled_toys, 0.223, outdoor_games, 0.215));
//        best.put(new ChildType(male, 9), Map.of(computer_games, 0.803, board_games, 0.421, outdoor_games, 0.228));
//        best.put(new ChildType(male, 10), Map.of(computer_games, 0.813, board_games, 0.424, outdoor_games, 0.227, radio_controlled_toys, 0.226));
//
//        best.put(new ChildType(female, 0), Map.of(playground, 0.065, soft_toys, 0.054));
//        best.put(new ChildType(female, 1), Map.of(sweets, 0.434, playground, 0.185, soft_toys, 0.106));
//        best.put(new ChildType(female, 2), Map.of(sweets, 0.439, playground, 0.188, dolls, 0.13));
//        best.put(new ChildType(female, 3), Map.of(outdoor_games, 0.453, sweets, 0.432, dolls, 0.403, playground, 0.245, soft_toys, 0.212));
//        best.put(new ChildType(female, 4), Map.of(outdoor_games, 0.463, sweets, 0.434, dolls, 0.4, playground, 0.251, soft_toys, 0.215, computer_games, 0.206));
//        best.put(new ChildType(female, 5), Map.of(sweets, 0.571, computer_games, 0.412, books, 0.453, outdoor_games, 0.333));
//        best.put(new ChildType(female, 6), Map.of(dolls, 0.531, sweets, 0.568, books, 0.441, board_games, 0.315, outdoor_games, 0.329, computer_games, 0.411));
//        best.put(new ChildType(female, 7), Map.of(sweets, 0.562, dolls, 0.531, books, 0.445, constructors, 0.397, board_games, 0.319, outdoor_games, 0.335, computer_games, 0.397));
//        best.put(new ChildType(female, 8), Map.of(books, 0.454, computer_games, 0.402, board_games, 0.421, outdoor_games, 0.224, sweets, 0.283, pet, 0.251, clothes, 0.212));
//        best.put(new ChildType(female, 9), Map.of(books, 0.45, board_games, 0.44, pet, 0.249, outdoor_games, 0.221, computer_games, 0.4, sweets, 0.277, clothes, 0.21));
//        best.put(new ChildType(female, 10), Map.of(pet, 0.246, books, 0.435, board_games, 0.422, computer_games, 0.397, sweets, 0.301, outdoor_games, 0.228));
//
//
//        final List<Presenting> result = getPresentings(step2Map, best);
//
//        final int sum123 = result.stream()
//                .mapToInt(Presenting::getPrice)
//                .sum();
//
//        System.out.println("sum " + sum123);
//        System.out.println("happy " + happy);
//
//        Part2Result part2Result = new Part2Result(mapId, result);
//        String json = (new ObjectMapper().writeValueAsString(part2Result));
//        System.out.println(json);
//    }
//
//    private static List<Presenting> getPresentings(Step2Map step2Map, Map<ChildType, Map<GiftType, Double>> best) {
//        int max = 100000;
//        final List<Child2> unmodifiedChild = new ArrayList<>(step2Map.getChildren());
//        final List<Gift2> unmodifiedGifts = new ArrayList<>(step2Map.getGifts());
//
//        final List<Gift2> gifts = step2Map.getGifts();
//        final List<Child2> children = step2Map.getChildren();
//        final List<Presenting> result = new ArrayList<>();
//
//        while (!children.isEmpty()) {
//            final Child2 e = children.remove(0);
//            final ChildType childType = new ChildType(e.getGender(), e.getAge());
//            final List<Map.Entry<GiftType, Double>> entries = best.get(childType)
//                    .entrySet()
//                    .stream()
//                    .sorted(Map.Entry.comparingByValue())
//                    .toList();
//
//            final Gift2 gift = getMaxHappyGiftWithEntries(gifts, entries);
//            gifts.remove(gift);
//
//            if (sum + gift.getPrice() > 100000) {
//                final Presenting presenting = result.stream()
//                        .max(Comparator.comparingInt(Presenting::getPrice))
//                        .get();
//                result.remove(presenting);
//                sum = sum - unmodifiedGifts.stream().filter(w -> w.getId() == presenting.giftID).findFirst().get().getPrice();
//                happy = happy - (int) (presenting.getPrice() * best.getOrDefault(childType, Map.of()).getOrDefault(presenting.giftType, 0.0));
//                children.add(
//                        unmodifiedChild.stream()
//                                .filter(w -> w.getId() == presenting.childID)
//                                .findFirst()
//                                .get()
//                );
//            }
//
//            final Presenting presenting = new Presenting(gift.getId(), e.getId(), gift.getPrice(), gift.type, new ChildType(e.getGender(), e.getAge()));
//            result.add(presenting);
//            sum = sum + gift.getPrice();
//            happy = happy + (int) (gift.getPrice() * best.getOrDefault(childType, Map.of()).getOrDefault(gift.type, 0.0));
//        }
//        return result;
//    }
//
//
//    static int tail = 100;
//    static int maxpricesplit = 100;
//
//    public static void main22(String[] args) {
//        loopi:
//        for (int i = 280; i < 400; i+=10) {
//            System.out.println("#" + i);
//
//            Integer sum1 = null, sum0 = null, sum2 = null;
//            try {
//                sum = 0;
//                happy = 0;
//                tail = i;
//                maxpricesplit = i / 5;
//                sum0 = main2(args);
//            } catch (Exception e) {
//                //System.out.println("");
//            }
//            try {
//                sum = 0;
//                happy = 0;
//                tail = i;
//                maxpricesplit = i;
//                sum1 = main2(args);
//            } catch (Exception e) {
//               // System.out.println("");
//            }
//            try {
//                sum = 0;
//                happy = 0;
//                tail = i;
//                maxpricesplit = i * 2;
//                sum2 = main2(args);
//            } catch (Exception e) {
//                //System.out.println("");
//            }
//
//            int from, to, d;
//            if (sum0 != null) {
//                from = i / 5;
//                to = 0;
//                d = -15;
//            } else if (sum1 != null) {
//                from = i;
//                to = 0;
//                d = -15;
//            } else {
//                from = i * 2;
//                to = 0;
//                d = -15;
//            }
//
//            System.out.println(""+sum0+" "+sum1+" "+sum2);
//
//            for (int j = from; j > to; j += d) {
//                sum = 0;
//                happy = 0;
//                tail = i;
//                maxpricesplit = j;
//                try {
//                    Integer res = main2(args);
//                } catch (Exception e) {
//                    continue loopi;
//                }
//            }
//        }
//    }
//
//    @SneakyThrows
//    public static Integer main2(String[] args) {
//        final Step2Map step2Map = new ObjectMapper().readValue(new File("part2_gift.json"), Step2Map.class);
////        System.out.println(step2Map);
//        Map<ChildType, List<Child2>> types =
//                new TreeMap<>(step2Map.children.stream().collect(Collectors.groupingBy(c -> new ChildType(c.getGender(), c.getAge()))));
////        System.out.println(types);
//
//        List<Object[]> objects = step2Map.gifts.stream().collect(Collectors.groupingBy(g -> g.type)).entrySet().stream().map(e -> {
//            return new Object[]{
//                    e.getKey(),
//                    e.getValue().stream().mapToInt(Gift2::getPrice).min().getAsInt(),
//                    e.getValue().stream().mapToInt(Gift2::getPrice).max().getAsInt(),
//            };
//        }).toList();
//
//        Map<ChildType, Map<GiftType, Double>> best = new HashMap<>();
//        best.put(new ChildType(male, 0), Map.of(toy_vehicles, 0.130, playground, 0.063, outdoor_games, 0.054, soft_toys, 0.051, sweets, 0.041, clothes, 0.0));
//        best.put(new ChildType(male, 1), Map.of(sweets, 0.441, toy_vehicles, 0.4, playground, 0.185, soft_toys, 0.054));
//        best.put(new ChildType(male, 2), Map.of(radio_controlled_toys, 0.408, sweets, 0.4, playground, 0.2, toy_vehicles, 0.408));
//        best.put(new ChildType(male, 3), Map.of(sweets, 0.578, toy_vehicles, 0.4, playground, 0.253, soft_toys, 0.106));
//        best.put(new ChildType(male, 4), Map.of(computer_games, 0.602, toy_vehicles, 0.403, sweets, 0.5, constructors, 0.106, radio_controlled_toys, 0.054, board_games, 0.161, outdoor_games, 0.210, playground, 0.247, soft_toys, 0.108, books, 0.21));
//        best.put(new ChildType(male, 5), Map.of(computer_games, 0.798, sweets, 0.566, toy_vehicles, 0.530, outdoor_games, 0.445, constructors, 0.21, board_games, 0.316, playground, 0.183, books, 0.34));
//        best.put(new ChildType(male, 6), Map.of(computer_games, 0.803, sweets, 0.571, outdoor_games, 0.451, constructors, 0.211, board_games, 0.32, playground, 0.187, books, 0.329));
//        best.put(new ChildType(male, 7), Map.of(computer_games, 0.8, board_games, 0.311, constructors, 0.210, books, 0.333, playground, 0.189));
//        best.put(new ChildType(male, 8), Map.of(computer_games, 0.804, board_games, 0.424, radio_controlled_toys, 0.223, outdoor_games, 0.215));
//        best.put(new ChildType(male, 9), Map.of(computer_games, 0.803, board_games, 0.421, outdoor_games, 0.228));
//        best.put(new ChildType(male, 10), Map.of(computer_games, 0.813, board_games, 0.424, outdoor_games, 0.227, radio_controlled_toys, 0.226));
//
//        best.put(new ChildType(female, 0), Map.of(playground, 0.065, soft_toys, 0.054));
//        best.put(new ChildType(female, 1), Map.of(sweets, 0.434, playground, 0.185, soft_toys, 0.106));
//        best.put(new ChildType(female, 2), Map.of(sweets, 0.439, playground, 0.188, dolls, 0.13));
//        best.put(new ChildType(female, 3), Map.of(outdoor_games, 0.453, sweets, 0.432, dolls, 0.403, playground, 0.245, soft_toys, 0.212));
//        best.put(new ChildType(female, 4), Map.of(outdoor_games, 0.463, sweets, 0.434, dolls, 0.4, playground, 0.251, soft_toys, 0.215, computer_games, 0.206));
//        best.put(new ChildType(female, 5), Map.of(sweets, 0.571, computer_games, 0.412, books, 0.453, outdoor_games, 0.333));
//        best.put(new ChildType(female, 6), Map.of(dolls, 0.531, sweets, 0.568, books, 0.441, board_games, 0.315, outdoor_games, 0.329, computer_games, 0.411));
//        best.put(new ChildType(female, 7), Map.of(sweets, 0.562, dolls, 0.531, books, 0.445, constructors, 0.397, board_games, 0.319, outdoor_games, 0.335, computer_games, 0.397));
//        best.put(new ChildType(female, 8), Map.of(books, 0.454, computer_games, 0.402, board_games, 0.421, outdoor_games, 0.224, sweets, 0.283, pet, 0.251, clothes, 0.212));
//        best.put(new ChildType(female, 9), Map.of(books, 0.45, board_games, 0.44, pet, 0.249, outdoor_games, 0.221, computer_games, 0.4, sweets, 0.277, clothes, 0.21));
//        best.put(new ChildType(female, 10), Map.of(pet, 0.246, books, 0.435, board_games, 0.422, computer_games, 0.397, sweets, 0.301, outdoor_games, 0.228));
//
//
//        int max = 100000;
//        final List<Child2> unmodifiedChild = new ArrayList<>(step2Map.getChildren());
//        final List<Gift2> unmodifiedGifts = new ArrayList<>(step2Map.getGifts());
//
//        final List<Gift2> gifts = step2Map.getGifts();
//        final List<Child2> children = new ArrayList<>(
//                step2Map.getChildren()
//                        .stream()
//                        .sorted(Comparator.comparing(e -> best.get(new ChildType(e.getGender(), e.getAge()))
//                                .values()
//                                .stream()
//                                .mapToDouble(w -> w)
//                                .max()
//                                .getAsDouble() * -1)
//                        ).toList()
//        );
//        final List<Presenting> result1 = new ArrayList<>();
//        final List<Presenting> result2 = new ArrayList<>();
//
//        boolean isFirstFull = false;
//        while (!children.isEmpty()) {
//            final Child2 child2 = children.remove(0);
//            final ChildType childType = new ChildType(child2.getGender(), child2.getAge());
//            final List<Map.Entry<GiftType, Double>> entries = best.get(childType)
//                    .entrySet()
//                    .stream()
//                    .sorted(Map.Entry.comparingByValue())
//                    .toList();
//
//            final Gift2 gift = isFirstFull
//                    ? getMaxHappyGiftWithEntriesWithMaxPrice(gifts, entries, 100000 - sum)
//                    : getMaxHappyGiftWithEntries(gifts, entries);
//
//            gifts.remove(gift);
//
//            if (sum + gift.getPrice() > 100000) {
//                for (int i = 0; i < tail; i++) {
//                    final Presenting presenting = result1.stream().min(Comparator.comparing(presenting1 ->
//                            best.get(presenting1.childType).get(presenting1.giftType) * presenting1.getPrice())
//                    ).get();
//                    result1.remove(presenting);
//                    sum = sum - presenting.getPrice();
//                    happy = happy - (int) (presenting.getPrice() * best.getOrDefault(childType, Map.of()).getOrDefault(presenting.giftType, 0.0));
//                    children.add(unmodifiedChild.stream().filter(e -> e.getId() == presenting.getChildID()).findFirst().get());
//                }
//
//                isFirstFull = true;
//            }
//
//            final Presenting presenting = new Presenting(gift.getId(), child2.getId(), gift.getPrice(), gift.type, new ChildType(child2.getGender(), child2.age));
//            if (isFirstFull) {
//                result2.add(presenting);
//            } else {
//                result1.add(presenting);
//            }
//            sum = sum + gift.getPrice();
//            happy = happy + (int) (gift.getPrice() * best.getOrDefault(childType, Map.of()).getOrDefault(gift.type, 0.0));
//        }
//
//        final List<Presenting> result = new ArrayList<>();
//        result.addAll(result1);
//        result.addAll(result2);
//
//        final int sum123 = result.stream()
//                .mapToInt(Presenting::getPrice)
//                .sum();
//
////        if (sum123 < 99000) {
////            return sum123;
////        }
//
//        System.out.println("sum " + sum123);
//        System.out.println("happy " + happy);
//
//        Part2Result part2Result = new Part2Result(mapId, result);
//        String json = (new ObjectMapper().writeValueAsString(part2Result));
//        System.out.println(json);
//
//        return sum123;
//    }
//
//    private static Gift2 getMaxPriceGift(final List<Gift2> gifts, final List<GiftType> giftTypes) {
//        return gifts.stream()
//                .filter(e -> giftTypes.contains(e.getType()))
////                .sorted(Comparator.comparing(Gift2::getPrice).thenComparing(e -> e.g))
//                .max(Comparator.comparing(Gift2::getPrice))
//                .get();
//    }
//
//    private static Gift2 getMaxHappyGiftWithEntries(final List<Gift2> gifts, final List<Map.Entry<GiftType, Double>> giftTypes) {
//        final Gift2 gift2 = gifts.stream()
//                .filter(e -> giftTypes.stream().map(Map.Entry::getKey).toList().contains(e.getType()))
//                .max(
//                        Comparator.comparing((Gift2 e) -> giftTypes.stream()
//                                .filter(r -> r.getKey() == e.getType())
//                                .findFirst()
//                                .map(Map.Entry::getValue)
//                                .get() * e.getPrice()
//                        ))
//                .get();
//
//        return gift2;
//    }
//
//    private static Gift2 getMaxHappyGiftWithEntriesWithMaxPrice(final List<Gift2> gifts, final List<Map.Entry<GiftType, Double>> giftTypes, int maxPrice) {
//        final Gift2 gift2 = gifts.stream()
//                .filter(e -> giftTypes.stream().map(Map.Entry::getKey).toList().contains(e.getType()))
//                .filter(e -> e.price <= maxPrice / maxpricesplit)
//                .max(
//                        Comparator.comparing((Gift2 e) -> giftTypes.stream()
//                                .filter(r -> r.getKey() == e.getType())
//                                .findFirst()
//                                .map(Map.Entry::getValue)
//                                .get() * e.getPrice()
//                        ))
//                .orElse(null);
//
//        return gift2;
//    }
//
//    private static Gift2 getMinPriceGift(final List<Gift2> gifts, List<Map.Entry<GiftType, Double>> giftTypes) {
//        final Gift2 gift2 = gifts.stream()
//                .filter(e -> giftTypes.stream().map(Map.Entry::getKey).toList().contains(e.getType()))
//                .min(Comparator.comparing(Gift2::getPrice))
//                .get();
//
//        return gift2;
//    }
}
