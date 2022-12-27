package org.example.datsanta.part2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import static org.example.datsanta.part2.Gender.female;
import static org.example.datsanta.part2.Gender.male;
import static org.example.datsanta.part2.GiftType.board_games;
import static org.example.datsanta.part2.GiftType.books;
import static org.example.datsanta.part2.GiftType.clothes;
import static org.example.datsanta.part2.GiftType.computer_games;
import static org.example.datsanta.part2.GiftType.constructors;
import static org.example.datsanta.part2.GiftType.dolls;
import static org.example.datsanta.part2.GiftType.outdoor_games;
import static org.example.datsanta.part2.GiftType.pet;
import static org.example.datsanta.part2.GiftType.playground;
import static org.example.datsanta.part2.GiftType.radio_controlled_toys;
import static org.example.datsanta.part2.GiftType.soft_toys;
import static org.example.datsanta.part2.GiftType.sweets;
import static org.example.datsanta.part2.GiftType.toy_vehicles;

public class CompactMain {
    static int sum = 0;


    @SneakyThrows
    public static void maxPrice(String[] args) {
        final Step2Map step2Map = new ObjectMapper().readValue(new File("part2_gift.json"), Step2Map.class);
//        System.out.println(step2Map);
        Map<ChildType, List<Child2>> types =
                new TreeMap<>(step2Map.children.stream().collect(Collectors.groupingBy(c -> new ChildType(c.getGender(), c.getAge()))));
//        System.out.println(types);

        List<Object[]> objects = step2Map.gifts.stream().collect(Collectors.groupingBy(g -> g.type)).entrySet().stream().map(e -> {
            return new Object[]{
                    e.getKey(),
                    e.getValue().stream().mapToInt(Gift2::getPrice).min().getAsInt(),
                    e.getValue().stream().mapToInt(Gift2::getPrice).max().getAsInt(),
            };
        }).toList();

        Map<ChildType, List<GiftType>> best = new HashMap<>();
        best.put(new ChildType(male, 0), List.of(constructors));
        best.put(new ChildType(male, 1), List.of(dolls));
        best.put(new ChildType(male, 2), List.of(radio_controlled_toys));
        best.put(new ChildType(male, 3), List.of(toy_vehicles));
        best.put(new ChildType(male, 4), List.of(board_games));
        best.put(new ChildType(male, 5), List.of(outdoor_games));
        best.put(new ChildType(male, 6), List.of(playground));
        best.put(new ChildType(male, 7), List.of(soft_toys));
        best.put(new ChildType(male, 8), List.of(computer_games));
        best.put(new ChildType(male, 9), List.of(sweets));
        best.put(new ChildType(male, 10), List.of(books));
        best.put(new ChildType(female, 0), List.of(constructors));
        best.put(new ChildType(female, 1), List.of(dolls));
        best.put(new ChildType(female, 2), List.of(radio_controlled_toys));
        best.put(new ChildType(female, 3), List.of(toy_vehicles));
        best.put(new ChildType(female, 4), List.of(board_games));
        best.put(new ChildType(female, 5), List.of(outdoor_games));
        best.put(new ChildType(female, 6), List.of(playground));
        best.put(new ChildType(female, 7), List.of(soft_toys));
        best.put(new ChildType(female, 8), List.of(computer_games));
        best.put(new ChildType(female, 9), List.of(pet));
        best.put(new ChildType(female, 10), List.of(clothes));


        int max = 100000;
        final List<Child2> unmodifiedChild = new ArrayList<>(step2Map.getChildren());
        final List<Gift2> unmodifiedGifts = new ArrayList<>(step2Map.getGifts());

        final List<Gift2> gifts = step2Map.getGifts();
        final List<Child2> children = step2Map.getChildren();
        final List<Presenting> result = new ArrayList<>();

        while (!children.isEmpty()) {
            final Child2 e = children.remove(0);
            final ChildType childType = new ChildType(e.getGender(), e.getAge());
            final List<GiftType> giftTypes = best.get(childType);

            final Gift2 gift = getMaxPriceGift(gifts, giftTypes);
            gifts.remove(gift);

            if (sum + gift.getPrice() > 100000) {
                final Presenting presenting = result.stream()
                        .max(Comparator.comparingInt(Presenting::getPrice))
                        .get();
                result.remove(presenting);
                sum = sum - unmodifiedGifts.stream().filter(w -> w.getId() == presenting.giftID).findFirst().get().getPrice();
                children.add(
                        unmodifiedChild.stream()
                                .filter(w -> w.getId() == presenting.childID)
                                .findFirst()
                                .get()
                );
            }

            final Presenting presenting = new Presenting(gift.getId(), e.getId(), gift.getPrice());
            result.add(presenting);
            sum = sum + gift.getPrice();
        }

        final int sum123 = result.stream()
                .mapToInt(Presenting::getPrice)
                .sum();

        System.out.println(sum123);
    }

    @SneakyThrows
    public static void main(String[] args) {
        final Step2Map step2Map = new ObjectMapper().readValue(new File("part2_gift.json"), Step2Map.class);
//        System.out.println(step2Map);
        Map<ChildType, List<Child2>> types =
                new TreeMap<>(step2Map.children.stream().collect(Collectors.groupingBy(c -> new ChildType(c.getGender(), c.getAge()))));
//        System.out.println(types);

        List<Object[]> objects = step2Map.gifts.stream().collect(Collectors.groupingBy(g -> g.type)).entrySet().stream().map(e -> {
            return new Object[]{
                    e.getKey(),
                    e.getValue().stream().mapToInt(Gift2::getPrice).min().getAsInt(),
                    e.getValue().stream().mapToInt(Gift2::getPrice).max().getAsInt(),
            };
        }).toList();

        Map<ChildType, Map<GiftType, Double>> best = new HashMap<>();
        best.put(new ChildType(male, 0), Map.of(toy_vehicles, 0.130));
        best.put(new ChildType(male, 1), Map.of(sweets, 0.441));
        best.put(new ChildType(male, 2), Map.of(radio_controlled_toys, 0.408));
        best.put(new ChildType(male, 3), Map.of(sweets, 0.578));
        best.put(new ChildType(male, 4), Map.of(computer_games, 0.602));
        best.put(new ChildType(male, 5), Map.of(computer_games, 0.798));
        best.put(new ChildType(male, 6), Map.of(computer_games, 0.803));
        best.put(new ChildType(male, 7), Map.of(computer_games, 0.8));
        best.put(new ChildType(male, 8), Map.of(computer_games, 0.804));
        best.put(new ChildType(male, 9), Map.of(computer_games, 0.803));
        best.put(new ChildType(male, 10), Map.of(computer_games, 0.813));
        best.put(new ChildType(female, 0), Map.of(playground, 0.065));
        best.put(new ChildType(female, 1), Map.of(sweets, 0.434));
        best.put(new ChildType(female, 2), Map.of(sweets, 0.439));
        best.put(new ChildType(female, 3), Map.of(outdoor_games, 0.453));
        best.put(new ChildType(female, 4), Map.of(outdoor_games, 0.463));
        best.put(new ChildType(female, 5), Map.of(sweets, 0.571));
        best.put(new ChildType(female, 6), Map.of(dolls, 0.531));
        best.put(new ChildType(female, 7), Map.of(dolls, 0.531));
        best.put(new ChildType(female, 8), Map.of(books, 0.454));
        best.put(new ChildType(female, 9), Map.of(books, 0.45));
        best.put(new ChildType(female, 10), Map.of(pet, 0.246));


        int max = 100000;
        final List<Child2> unmodifiedChild = new ArrayList<>(step2Map.getChildren());
        final List<Gift2> unmodifiedGifts = new ArrayList<>(step2Map.getGifts());

        final List<Gift2> gifts = step2Map.getGifts();
        final List<Child2> children = new ArrayList<>(
                step2Map.getChildren()
                        .stream()
                        .sorted(Comparator.comparing(e -> best.get(new ChildType(e.getGender(), e.getAge()))
                                .values()
                                .stream()
                                .mapToDouble(w -> w)
                                .max()
                                .getAsDouble() * -1)
                        ).toList()
        );
        final List<Presenting> result1 = new ArrayList<>();
        final List<Presenting> result2 = new ArrayList<>();

        boolean isFirstFull = false;
        while (!children.isEmpty()) {
            final Child2 child2 = children.remove(0);
            final ChildType childType = new ChildType(child2.getGender(), child2.getAge());
            final List<Map.Entry<GiftType, Double>> entries = best.get(childType)
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .toList();

            final Gift2 gift = isFirstFull
                    ? getMinPriceGift(gifts, entries)
                    : getMaxPriceGiftWithEntries(gifts, entries);
            gifts.remove(gift);

            if (sum + gift.getPrice() > 100000) {
                final Presenting presenting = result1.stream().min(Comparator.comparing(Presenting::getPrice)).get();
                result1.remove(presenting);
                sum = sum - presenting.getPrice();
                children.add(unmodifiedChild.stream().filter(e -> e.getId() == presenting.getChildID()).findFirst().get());
                isFirstFull = true;
            }

            final Presenting presenting = new Presenting(gift.getId(), child2.getId(), gift.getPrice());
            if (isFirstFull) {
                result2.add(presenting);
            } else {
                result1.add(presenting);
            }
            sum = sum + gift.getPrice();
        }

        final List<Presenting> result = new ArrayList<>();
        result.addAll(result1);
        result.addAll(result2);

        final int sum123 = result.stream()
                .mapToInt(Presenting::getPrice)
                .sum();

        System.out.println(sum123);
    }

    private static Gift2 getMaxPriceGift(final List<Gift2> gifts, final List<GiftType> giftTypes) {
        return gifts.stream()
                .filter(e -> giftTypes.contains(e.getType()))
//                .sorted(Comparator.comparing(Gift2::getPrice).thenComparing(e -> e.g))
                .max(Comparator.comparing(Gift2::getPrice))
                .get();
    }

    private static Gift2 getMaxPriceGiftWithEntries(final List<Gift2> gifts, final List<Map.Entry<GiftType, Double>> giftTypes) {
        final Gift2 gift2 = gifts.stream()
                .filter(e -> giftTypes.stream().map(Map.Entry::getKey).toList().contains(e.getType()))
                .max(
                        Comparator.comparing(e -> giftTypes.stream()
                                .filter(r -> r.getKey() == e.getType())
                                .findFirst()
                                .map(Map.Entry::getValue)
                                .get() * e.getPrice()
                        ))
                .get();

        return gift2;
    }

    private static Gift2 getMinPriceGift(final List<Gift2> gifts, List<Map.Entry<GiftType, Double>> giftTypes) {
        final Gift2 gift2 = gifts.stream()
                .filter(e -> giftTypes.stream().map(Map.Entry::getKey).toList().contains(e.getType()))
                .sorted(
                        Comparator.comparing(e -> giftTypes.stream()
                                .filter(r -> r.getKey() == e.getType())
                                .findFirst()
                                .map(Map.Entry::getValue)
                                .get()
                        )
                )
                .findFirst()
                .get();

        return gift2;
    }
}