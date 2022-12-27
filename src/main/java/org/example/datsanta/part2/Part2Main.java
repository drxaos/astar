package org.example.datsanta.part2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.example.datsanta.part2.Gender.female;
import static org.example.datsanta.part2.Gender.male;
import static org.example.datsanta.part2.GiftType.*;

public class Part2Main {

    static String apiKey = "90a75999-2d77-41d9-aa22-26a85571da53";
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
////        System.out.println(objects);
//
//
//        Map<ChildType, List<GiftType>> best = new HashMap<>();
////        best.put(new ChildType(male, 0), new ArrayList<>(List.of(playground, soft_toys, sweets,clothes,outdoor_games,toy_vehicles)));
////        best.put(new ChildType(male, 1), new ArrayList<>(List.of(playground, soft_toys, sweets, toy_vehicles)));
////        best.put(new ChildType(male, 2), new ArrayList<>(List.of(playground, soft_toys, sweets, toy_vehicles)));
////        best.put(new ChildType(male, 3), new ArrayList<>(List.of(sweets, soft_toys, playground, toy_vehicles)));
////        best.put(new ChildType(male, 4), new ArrayList<>(List.of(pet, sweets, computer_games, playground, outdoor_games, board_games, toy_vehicles)));
////        best.put(new ChildType(male, 5), new ArrayList<>(List.of(pet, books, sweets, computer_games, playground, outdoor_games, board_games, toy_vehicles)));
////        best.put(new ChildType(male, 6), new ArrayList<>(List.of(pet, books, sweets, computer_games, playground, outdoor_games, board_games, constructors)));
////        best.put(new ChildType(male, 7), new ArrayList<>(List.of(pet, books, computer_games, playground, outdoor_games, board_games, constructors)));
////        best.put(new ChildType(male, 8), new ArrayList<>(List.of(pet, books, computer_games, playground, outdoor_games, board_games, constructors, radio_controlled_toys)));
////        best.put(new ChildType(male, 9), new ArrayList<>(List.of(pet, books, computer_games, outdoor_games, board_games, constructors, radio_controlled_toys)));
////        best.put(new ChildType(male, 10), new ArrayList<>(List.of(pet, computer_games, outdoor_games, board_games, constructors, radio_controlled_toys)));
////        best.put(new ChildType(female, 0), new ArrayList<>(List.of(playground, sweets, soft_toys,clothes,outdoor_games,dolls)));
////        best.put(new ChildType(female, 1), new ArrayList<>(List.of(playground, sweets, soft_toys)));
////        best.put(new ChildType(female, 2), new ArrayList<>(List.of(playground, sweets, soft_toys, dolls)));
////        best.put(new ChildType(female, 3), new ArrayList<>(List.of(sweets, soft_toys, playground, outdoor_games, dolls)));
////        best.put(new ChildType(female, 4), new ArrayList<>(List.of(clothes, pet, sweets, computer_games, soft_toys, playground, outdoor_games, board_games, dolls)));
////        best.put(new ChildType(female, 5), new ArrayList<>(List.of(clothes, pet, books, sweets, computer_games, soft_toys, playground, outdoor_games, board_games, dolls)));
////        best.put(new ChildType(female, 6), new ArrayList<>(List.of(clothes, pet, books, sweets, computer_games, soft_toys, playground, outdoor_games, board_games, dolls)));
////        best.put(new ChildType(female, 7), new ArrayList<>(List.of(clothes, pet, books, computer_games, soft_toys, playground, outdoor_games, board_games, dolls)));
////        best.put(new ChildType(female, 8), new ArrayList<>(List.of(clothes, pet, books, computer_games, soft_toys, playground, outdoor_games, board_games)));
////        best.put(new ChildType(female, 9), new ArrayList<>(List.of(clothes, pet, books, computer_games, soft_toys, outdoor_games, board_games)));
//        best.put(new ChildType(male, 4), new ArrayList<>(List.of(constructors)));
//        best.put(new ChildType(male, 5), new ArrayList<>(List.of(constructors)));
//        best.put(new ChildType(female, 7), new ArrayList<>(List.of(sweets)));
//        best.put(new ChildType(female, 8), new ArrayList<>(List.of(sweets)));
//        best.put(new ChildType(female, 9), new ArrayList<>(List.of(sweets)));
//        best.put(new ChildType(female, 10), new ArrayList<>(List.of(sweets, clothes, pet, books, computer_games, soft_toys, board_games)));
//
////        Map<ChildType, GiftType> best = new HashMap<>();
////        best.put(new ChildType(male, 0), constructors);
////        best.put(new ChildType(male, 1), dolls);
////        best.put(new ChildType(male, 2), radio_controlled_toys);
////        best.put(new ChildType(male, 3), toy_vehicles);
////        best.put(new ChildType(male, 4), board_games);
////        best.put(new ChildType(male, 5), outdoor_games);
////        best.put(new ChildType(male, 6), playground);
////        best.put(new ChildType(male, 7), soft_toys);
////        best.put(new ChildType(male, 8), computer_games);
////        best.put(new ChildType(male, 9), sweets);
////        best.put(new ChildType(male, 10), books);
////        best.put(new ChildType(female, 0), constructors);
////        best.put(new ChildType(female, 1), dolls);
////        best.put(new ChildType(female, 2), radio_controlled_toys);
////        best.put(new ChildType(female, 3), toy_vehicles);
////        best.put(new ChildType(female, 4), board_games);
////        best.put(new ChildType(female, 5), outdoor_games);
////        best.put(new ChildType(female, 6), playground);
////        best.put(new ChildType(female, 7), soft_toys);
////        best.put(new ChildType(female, 8), computer_games);
////        best.put(new ChildType(female, 9), sweets);
////        best.put(new ChildType(female, 10), books);
//
//        ChildType childType1 = new ChildType(male, 0);
//        for (GiftType giftType : List.of(dolls)) {
//            collect("" + childType1.str(), step2Map, childType1, giftType);
//        }
//        ChildType childType2 = new ChildType(male, 1);
//        for (GiftType giftType : List.of(dolls)) {
//            collect("" + childType2.str(), step2Map, childType2, giftType);
//        }
////        for (ChildType childType : best.keySet().stream().sorted(Comparator.comparing(ct -> -types.get(ct).size())).toList()) {
////            for (GiftType giftType : best.get(childType)) {
////                collect("" + childType.str(), step2Map, childType, giftType);
////            }
////        }
////        for (ChildType childType : best.keySet().stream().sorted(Comparator.comparing(ct -> -types.get(ct).size())).toList()) {
////            for (GiftType giftType : GiftType.values()) {
////                collect("" + childType.str(), step2Map, childType, giftType);
////            }
////        }
//    }
//
//    private static int collect(String name, Step2Map step2Map, ChildType tryingCt, GiftType tryingGt) throws Exception {
//
//        AtomicInteger added = new AtomicInteger();
//        String roundId = null;
//        try {
//            List<String> strings = Files.readAllLines(new File(name + ".txt").toPath(), Charset.defaultCharset());
//            List<String> found = strings.stream().filter(s -> s.startsWith("" + tryingCt.gender + ":" + tryingCt.age + ":" + tryingGt + ":")).toList();
//            if (found.size() == 2) {
//                Optional<String> max = found.stream().max(Comparator.comparing(String::length));
//                roundId = found.get(0).split(":")[3];
//                String addedStr = max.get().split(":")[4];
//                added.set(Integer.parseInt(addedStr));
//                String happy = max.get().split(":")[5];
//                System.out.println("already got " + max.get());
//                return Integer.parseInt(happy);
//            } else if (found.size() == 1) {
//                roundId = found.get(0).split(":")[3];
//                String addedStr = found.get(0).split(":")[4];
//                added.set(Integer.parseInt(addedStr));
//                System.out.println("already sent " + roundId);
//            }
//        } catch (Exception e) {
//        }
//
//        // collect
//
//        AtomicInteger sum = new AtomicInteger();
//        ArrayList<Gift2> presented = new ArrayList<>();
//        ArrayList<Presenting> presentings = new ArrayList<>();
//
//        if (roundId == null) {
//
//            System.out.println("sending " + tryingCt.gender + ":" + tryingCt.age + ":" + tryingGt);
//
//            List<Child2> remainChildren = new ArrayList<>(step2Map.getChildren());
//            for (GiftType gt : values()) {
//                Child2 c = remainChildren.remove(0);
//                Gift2 defaultGift = step2Map.getGifts().stream()
//                        .filter(g -> !presented.contains(g))
//                        .filter(g -> g.type == gt)
//                        .findFirst().get();
//                presentings.add(new Presenting(defaultGift.id, c.id, defaultGift.price, defaultGift.type, new ChildType(c.getGender(), c.getAge())));
//                presented.add(defaultGift);
//                sum.addAndGet(defaultGift.price);
//            }
//            remainChildren.stream().forEach(c -> {
//                Gift2 defaultGift = step2Map.getGifts().stream()
//                        .filter(g -> !presented.contains(g))
//                        .findFirst().get();
//                presentings.add(new Presenting(defaultGift.id, c.id, defaultGift.price, defaultGift.type, new ChildType(c.getGender(), c.getAge())));
//                presented.add(defaultGift);
//                sum.addAndGet(defaultGift.price);
//            });
//            System.out.println(sum.get());
//
//
//            for (int i = 0; i < presentings.size() && added.get() < 20000; i++) {
//                Presenting p = presentings.get(i);
//                if (p.giftType == tryingGt && p.childType.equals(tryingCt)) {
//                    Gift2 expGift = step2Map.getGifts().stream()
//                            .filter(g -> !presented.contains(g))
//                            .filter(g -> g.type == tryingGt)
//                            .max(Comparator.comparing(Gift2::getPrice)).get();
//
//                    sum.addAndGet(-p.price);
//                    added.addAndGet(-p.price);
//                    p.setPrice(expGift.price);
//                    p.setGiftID(expGift.id);
//                    presented.add(expGift);
//                    sum.addAndGet(p.price);
//                    added.addAndGet(p.price);
//                }
//            }
//
//            Part2Result part2Result = new Part2Result(mapId, presentings);
//            String json = (new ObjectMapper().writeValueAsString(part2Result));
//            System.out.println(json);
//
//            // send
//
//            AtomicBoolean sended = new AtomicBoolean(false);
//            while (!sended.get()) {
//                try {
//                    HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//                    httpRequestFactory.setConnectionRequestTimeout(50000);
//                    httpRequestFactory.setConnectTimeout(50000);
//                    HttpHeaders headers = new HttpHeaders();
//                    headers.add("X-API-Key", apiKey);
//                    headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
//                    HttpEntity<Part2Result> entity = new HttpEntity<>(part2Result, headers);
//                    final ResponseEntity<RoundPostResult> exchange = new RestTemplate(
//                            httpRequestFactory
//                    )
//                            .exchange(
//                                    "https://datsanta.dats.team/api/round2",
//                                    HttpMethod.POST,
//                                    entity,
//                                    RoundPostResult.class
//                            );
//                    RoundPostResult mapJson = exchange.getBody();
//                    System.out.println(mapJson);
//
//                    if (mapJson != null && mapJson.roundId != null) {
//                        sended.set(true);
//                        roundId = mapJson.roundId;
//
//                        try (FileOutputStream fileOutputStream = new FileOutputStream(name + ".txt", true)) {
//                            fileOutputStream.write(("" + tryingCt.gender + ":" + tryingCt.age + ":" + tryingGt + ":" + mapJson.roundId + ":" + added.get() + "\n").getBytes());
//                        }
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                Thread.sleep(5000);
//            }
//        }
//
//        AtomicBoolean received = new AtomicBoolean(false);
//        while (!received.get()) {
//            try {
//                HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
//                httpRequestFactory.setConnectionRequestTimeout(50000);
//                httpRequestFactory.setConnectTimeout(50000);
//                HttpHeaders headers = new HttpHeaders();
//                headers.add("X-API-Key", apiKey);
//                headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
//                HttpEntity<Part2Result> entity = new HttpEntity<>(null, headers);
//                final ResponseEntity<RoungGetResult> exchange = new RestTemplate(httpRequestFactory)
//                        .exchange(
//                                "https://datsanta.dats.team/api/round2/" + roundId,
//                                HttpMethod.GET,
//                                entity,
//                                RoungGetResult.class
//                        );
//                RoungGetResult mapJson = exchange.getBody();
//                System.out.println(mapJson);
//
//                if (mapJson != null && mapJson.data != null && mapJson.data.status != null && mapJson.data.status.equals("processed")) {
//                    received.set(true);
//
//                    Integer result = mapJson.data.total_happy;
//
//                    double k = (1.0 * result - 25511) / added.get();
//
//                    try (FileOutputStream fileOutputStream = new FileOutputStream(name + ".txt", true)) {
//                        fileOutputStream.write(("" + tryingCt.gender + ":" + tryingCt.age + ":" + tryingGt + ":" + roundId + ":" + added.get() + ":" + result + ":" + k + "\n").getBytes());
//                    }
//
//                    return result;
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            Thread.sleep(5000);
//        }
//        throw new RuntimeException("AAAA!");
//    }
}
