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
import static org.example.datsanta.part2.GiftType.*;

public class Part2Main2 {

    static String apiKey = "90a75999-2d77-41d9-aa22-26a85571da53";
    static String mapId = "a8e01288-28f8-45ee-9db4-f74fc4ff02c8";
//
//    @SneakyThrows
//    public static void main(String[] args) {
//        final Step2Map step2Map = new ObjectMapper().readValue(new File("part2_gift.json"), Step2Map.class);
//
//        ChildType childType1 = new ChildType(female, 8);
//
//        Map<ChildType, List<Child2>> types =
//                new TreeMap<>(step2Map.children.stream().collect(Collectors.groupingBy(c -> new ChildType(c.getGender(), c.getAge()))));
//        types.keySet().forEach(childType -> types.compute(childType, (k, v) -> new ArrayList<>(v)));
//
//        AtomicInteger sum = new AtomicInteger();
//        ArrayList<Gift2> presented = new ArrayList<>();
//        ArrayList<Presenting> presentings = new ArrayList<>();
//
//        Presenting changing = null;
//        for (GiftType giftType : values()) {
//            Child2 child1 = types.get(childType1).remove(0);
//            Gift2 gift1 = step2Map.getGifts().stream()
//                    .filter(g -> g.type == giftType)
//                    .min(Comparator.comparing(Gift2::getPrice)).get();
//            Presenting presenting = new Presenting(gift1.id, child1.id, gift1.price, gift1.type, childType1);
//            presentings.add(presenting);
//            if (giftType == board_games) {
//                changing = presenting;
//            }
//            presented.add(gift1);
//            sum.addAndGet(gift1.price);
//        }
//
//        Presenting finalChanging = changing;
//        int basePrice = changing.price;
//
//        types.values().stream().flatMap(Collection::stream).forEach(c -> {
//            Gift2 gift = step2Map.getGifts().stream()
//                    .filter(g -> !presented.contains(g))
//                    .filter(g -> g.price != basePrice)
//                    .filter(g -> g.price != basePrice + 10)
//                    .filter(g -> g.price != basePrice + 50)
//                    .min(Comparator.comparing(Gift2::getPrice)).get();
//
//            presentings.add(new Presenting(gift.id, c.id));
//            presented.add(gift);
//            sum.addAndGet(gift.price);
//        });
//
//        Part2Result part2Result = new Part2Result(mapId, presentings);
//        System.out.println(part2Result);
//        String json = (new ObjectMapper().writeValueAsString(part2Result));
//        System.out.println(json);
//
//        Gift2 newGift = step2Map.getGifts().stream()
//                .filter(g -> !presented.contains(g))
//                .filter(g -> g.type == finalChanging.giftType)
//                .filter(g -> g.price == basePrice + 10)
//                .min(Comparator.comparing(Gift2::getPrice)).get();
//        changing.setGiftID(newGift.id);
//        changing.setPrice(newGift.price);
//
//        Part2Result part2Result10 = new Part2Result(mapId, presentings);
//        System.out.println(part2Result10);
//        String json10 = (new ObjectMapper().writeValueAsString(part2Result10));
//        System.out.println(json10);
//
//        Gift2 newGift100 = step2Map.getGifts().stream()
//                .filter(g -> !presented.contains(g))
//                .filter(g -> g.type == finalChanging.giftType)
//                .filter(g -> g.price == basePrice + 50)
//                .min(Comparator.comparing(Gift2::getPrice)).get();
//        changing.setGiftID(newGift100.id);
//        changing.setPrice(newGift100.price);
//
//        Part2Result part2Result100 = new Part2Result(mapId, presentings);
//        System.out.println(part2Result100);
//        String json100 = (new ObjectMapper().writeValueAsString(part2Result100));
//        System.out.println(json100);
//    }
//
//    private static int collect(String name, Step2Map step2Map, Map<ChildType, GiftType> best, ChildType tryingCt, GiftType tryingGt) throws Exception {
//
//        String roundId = null;
//        List<String> strings = Files.readAllLines(new File(name + ".txt").toPath(), Charset.defaultCharset());
//        List<String> found = strings.stream().filter(s -> s.startsWith("" + tryingCt.gender + ":" + tryingCt.age + ":" + tryingGt + ":")).toList();
//        if (found.size() == 2) {
//            Optional<String> max = found.stream().max(Comparator.comparing(String::length));
//            String happy = max.get().split(":")[4];
//            System.out.println("already got " + max.get());
//            return Integer.parseInt(happy);
//        } else if (found.size() == 1) {
//            roundId = found.get(0).split(":")[3];
//            System.out.println("already sent " + roundId);
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
//            step2Map.getChildren().stream().forEach(c -> {
//                GiftType bestType = best.get(new ChildType(c.getGender(), c.getAge()));
//
//                Gift2 tryGift = step2Map.getGifts().stream()
//                        .filter(g -> !presented.contains(g))
//                        .filter(g -> g.type == tryingGt)
//                        .min(Comparator.comparing(Gift2::getPrice)).get();
//                Gift2 bestGift = step2Map.getGifts().stream()
//                        .filter(g -> !presented.contains(g))
//                        .filter(g -> g.type == bestType)
//                        .min(Comparator.comparing(Gift2::getPrice)).get();
//
//                Gift2 doGift = (c.gender == tryingCt.gender && c.age == tryingCt.age) ? tryGift : bestGift;
//
//                presentings.add(new Presenting(doGift.id, c.id));
//                presented.add(doGift);
//                sum.addAndGet(doGift.price);
//            });
//            System.out.println(sum.get());
//
//            Part2Result part2Result = new Part2Result(mapId, presentings);
//            String json = (new ObjectMapper().writeValueAsString(part2Result));
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
//                            fileOutputStream.write(("" + tryingCt.gender + ":" + tryingCt.age + ":" + tryingGt + ":" + mapJson.roundId + "\n").getBytes());
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
//                    try (FileOutputStream fileOutputStream = new FileOutputStream(name + ".txt", true)) {
//                        fileOutputStream.write(("" + tryingCt.gender + ":" + tryingCt.age + ":" + tryingGt + ":" + roundId + ":" + result + "\n").getBytes());
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
