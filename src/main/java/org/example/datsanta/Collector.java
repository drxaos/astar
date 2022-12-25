package org.example.datsanta;

import lombok.SneakyThrows;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Collector {
    static String apiKey = "90a75999-2d77-41d9-aa22-26a85571da53";
    static String mapId = "faf7ef78-41b3-4a36-8423-688a61929c08";

    public static void main(String[] args) throws Exception {
        JsonLoader loader = new JsonLoader();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-API-Key", apiKey);
        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        final ResponseEntity<String> exchange = new RestTemplate()
                .exchange(
                        new URI("https://datsanta.dats.team/json/map/" + mapId + ".json"),
                        HttpMethod.GET,
                        entity,
                        String.class
                );
        String mapJson = exchange.getBody();
        Files.write(Paths.get("" + mapId + "_map.json"), mapJson.getBytes());


        //loader.load("src/main/java/org/example/datsanta/faf7ef78-41b3-4a36-8423-688a61929c08.json");
        loader.loadJson(mapJson);
        final Map<Child, Set<Child>> nodes = loader.toNodes();

        long startTime = System.currentTimeMillis();

        final List<List<Gift>> bags = Collector.collectGiftsV3(loader.getDsMap());

        System.out.println("");
    }

    public static List<List<Gift>> collectGifts(DsMap resp) {

        final List<Gift> gifts = resp.gifts();

        gifts.sort(Comparator.comparing(Gift::volume)
                .thenComparing(Gift::weight));

//        final int we = gifts.stream()
//                .map(Gift::weight)
//                .mapToInt(e -> e)
//                .sum();
//        final int vo = gifts.stream()
//                .map(Gift::volume)
//                .mapToInt(e -> e)
//                .sum();
//        System.out.println(we); //200 -> 40 по весу
//        System.out.println(vo); //100 -> 46 по объему
        //System.out.println(new ObjectMapper().writeValueAsString(gifts));

        List<List<Gift>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        int currentVolume = 0;
        int currentWeight = 0;
        for (int i = 0; i < gifts.size(); i++) {
            final Gift gift = gifts.get(i);

            if (currentVolume + gift.volume() <= 100 && currentWeight + gift.weight() <= 200) {
                result.get(result.size() - 1).add(gift);
                currentVolume += gift.volume();
                currentWeight += gift.weight();
            } else {
                result.add(new ArrayList<>());
                result.get(result.size() - 1).add(gift);
                currentVolume = gift.volume();
                currentWeight = gift.weight();
            }
        }

        result.sort(Comparator.comparing(List::size, Comparator.reverseOrder()));

        //System.out.println(new ObjectMapper().writeValueAsString(result));

        System.out.println("bags " + result.size() + ": " + result.stream().map(List::size).toList());

        return result;
    }

    @SneakyThrows
    public static List<List<Gift>> collectGiftsV2(DsMap resp) {

        final List<Gift> gifts = resp.gifts();

        final ArrayList<Gift> forCheck = new ArrayList<>(gifts);
        gifts.sort(Comparator.comparing(Gift::volume).reversed());
//                .thenComparing(Gift::weight));


        List<List<Gift>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        int currentVolume = 0;
        int currentWeight = 0;
//        for (int i = 0; i < gifts.size(); i++) {
        while (!gifts.isEmpty()) {
            final Gift gift = gifts.get(0);

            if (currentVolume + gift.volume() <= 100 && currentWeight + gift.weight() <= 200) {
                result.get(result.size() - 1).add(gift);
                currentVolume += gift.volume();
                currentWeight += gift.weight();
                gifts.remove(gift);
            } else {
                final int finalCurrentWeight = currentWeight;
                final int finalCurrentVolume = currentVolume;

                final Optional<Gift> optimum = gifts.stream()
                        .filter(e -> (e.weight() == 200 - finalCurrentWeight && e.volume() == 100 - finalCurrentVolume))
                        .findFirst();

                final Optional<Gift> optimumV = gifts.stream()
                        .filter(e -> (e.weight() <= 200 - finalCurrentWeight && e.volume() == 100 - finalCurrentVolume))
                        .max(Comparator.comparingInt(Gift::weight));

                final Optional<Gift> first = gifts.stream()
                        .filter(e -> (e.weight() <= 200 - finalCurrentWeight && e.volume() <= 100 - finalCurrentVolume))
//                        .findFirst();
                        .max(Comparator.comparingInt(Gift::weight));

                if (optimum.isPresent()) {
                    result.get(result.size() - 1).add(optimum.get());
                    currentVolume += optimum.get().volume();
                    currentWeight += optimum.get().weight();
                    gifts.remove(optimum.get());
                } else if (optimumV.isPresent()) {
                    result.get(result.size() - 1).add(optimumV.get());
                    currentVolume += optimumV.get().volume();
                    currentWeight += optimumV.get().weight();
                    gifts.remove(optimumV.get());
                } else if (first.isPresent()) {
                    result.get(result.size() - 1).add(first.get());
                    currentVolume += first.get().volume();
                    currentWeight += first.get().weight();
                    gifts.remove(first.get());
                } else {
                    result.add(new ArrayList<>());
                    result.get(result.size() - 1).add(gift);
                    currentVolume = gift.volume();
                    currentWeight = gift.weight();
                    gifts.remove(gift);
                }
            }
        }

        result.sort(Comparator.comparing(List::size, Comparator.reverseOrder()));

//        System.out.println(new ObjectMapper().writeValueAsString(result));
        final List<Integer> v = result.stream().map(e -> e.stream().map(Gift::volume).mapToInt(a -> a).sum()).toList();
        final List<Integer> w = result.stream().map(e -> e.stream().map(Gift::weight).mapToInt(a -> a).sum()).toList();
//        System.out.println(new ObjectMapper().writeValueAsString(v));
//        System.out.println(new ObjectMapper().writeValueAsString(result.stream().map(e -> e.stream().map(Gift::weight).mapToInt(a -> a).sum()).toList()));
        for (int i = 0; i < v.size(); i++) {
            System.out.println(v.get(i) + "_" + w.get(i));
        }

        System.out.println("bags " + result.size() + ": " + result.stream().map(List::size).toList());

        final List<Gift> gifts1 = new ArrayList<>(result.stream().flatMap(e -> e.stream()).toList());
        gifts1.sort(Comparator.comparingInt(Gift::id));
        forCheck.sort(Comparator.comparingInt(Gift::id));
        System.out.println(forCheck.equals(gifts1));
        return result;
    }

    @SneakyThrows
    public static List<List<Gift>> collectGiftsV3(DsMap resp) {

        List<List<Gift>> result = new ArrayList<>();

        final List<Gift> gifts = new ArrayList<>(resp.gifts());
        gifts.sort(Comparator.comparing(Gift::volume).reversed().thenComparing(Comparator.comparing(Gift::weight).reversed()));
        List<Gift> actualNow = new ArrayList<>(gifts);

        List<Gift> currentGifts = new ArrayList<>();


        int i = 0;
        while (!gifts.isEmpty()) {
            final Gift gift = actualNow.get(i);

//            int curItemValue = gift.volume() * 10000 + gift.weight();

            if(gift.id()==17)
            {
                System.out.println();
            }

            for (int j = 0; j < actualNow.size(); j++) {
                if(actualNow.get(j).id()==17)
                {
                    System.out.println();
                }

            }

            int realV = 0; //100 7-2
            int realW = 0; //200 12-4
            for (Gift currentGift : currentGifts) {
                realV += currentGift.volume();
                realW += currentGift.weight();
            }

            if (100 - realV >= gift.volume() && 200 - realW >= gift.weight()) {
//                curBag.addAndGet(curItemValue);
                currentGifts.add(gift);
                gifts.remove(i);
                actualNow.remove(i);
                i = 0;

                realV = 0; //100
                realW = 0; //200
                for (Gift currentGift : currentGifts) {
                    realV += currentGift.volume();
                    realW += currentGift.weight();
                }

                if ((realV >= 100 && realW >= 125) || (realW >= 200 && realV >= 60)) {
                    result.add(currentGifts);
                    System.out.println("Bag is completed " + result.size());
                    actualNow = new ArrayList<>(gifts);

                    final List<Gift> giftStream = actualNow.stream().filter(gift1 -> gift1.id() == 17).toList();

                    currentGifts = new ArrayList<>();
                    continue;
                }
            }

            if (i == actualNow.size() - 1 || realV >= 99 || realW >= 198) {
                int onBaseV = 0; //100
                int onBaseW = 0; //200
                for (Gift currentGift : gifts) {
                    onBaseV += currentGift.volume();
                    onBaseW += currentGift.weight();
                }

                if (100 - realV < 7 && 200 - realW < 12) {
                    actualNow = removeLast(gifts, currentGifts);
                } else

                if (100 - realV < 7) {
                    actualNow = removeLastV(gifts, currentGifts);
                }else

                if (200 - realW < 12) {
                    actualNow = removeLastW(gifts, currentGifts);
                }

                realV = 0; //100
                realW = 0; //200
                for (Gift currentGift : currentGifts) {
                    realV += currentGift.volume();
                    realW += currentGift.weight();
                }

                i = 0;
                continue;
            }
            i++;
        }
        result.add(currentGifts);
        System.out.println("");
//        gi
        final List<Integer> v = result.stream().map(e -> e.stream().map(Gift::volume).mapToInt(a -> a).sum()).toList();
        final List<Integer> w = result.stream().map(e -> e.stream().map(Gift::weight).mapToInt(a -> a).sum()).toList();
//        System.out.println(new ObjectMapper().writeValueAsString(v));
//        System.out.println(new ObjectMapper().writeValueAsString(result.stream().map(e -> e.stream().map(Gift::weight).mapToInt(a -> a).sum()).toList()));
        for (int j= 0; j < v.size(); j++) {
            System.out.println(v.get(j) + "_" + w.get(j));
        }

        System.out.println("bags " + result.size() + ": " + result.stream().map(List::size).toList());

        final List<Gift> gifts1 = new ArrayList<>(result.stream().flatMap(e -> e.stream()).toList());
        gifts1.sort(Comparator.comparingInt(Gift::id));
        resp.gifts().sort(Comparator.comparingInt(Gift::id));
        System.out.println(resp.gifts().equals(gifts1));

        return result;
    }

    private static List<Gift> removeLast(List<Gift> gifts, List<Gift> currentGifts) {
        List<Gift> actualNow;
        Gift giftForSkip = currentGifts.remove(currentGifts.size() - 1);
        gifts.add(giftForSkip);
//        curBag.addAndGet(-1 * (giftForSkip.volume() * 10000 + giftForSkip.weight()));
        actualNow = gifts.stream().filter(g -> g.volume() < giftForSkip.volume() && g.weight() < giftForSkip.weight())
            .sorted(Comparator.comparing(Gift::volume).reversed().thenComparing(Comparator.comparing(Gift::weight).reversed()))
            .collect(Collectors.toList());
        if (actualNow.isEmpty()) {
            actualNow = removeLast(gifts, currentGifts);
        }
        return actualNow;
    }

    private static List<Gift> removeLastV(List<Gift> gifts, List<Gift> currentGifts) {
        List<Gift> actualNow;
        Gift giftForSkip = currentGifts.remove(currentGifts.size() - 1);
        gifts.add(giftForSkip);
//        curBag.addAndGet(-1 * (giftForSkip.volume() * 10000 + giftForSkip.weight()));
        actualNow = gifts.stream().filter(g -> g.volume() < giftForSkip.volume())
            .sorted(Comparator.comparing(Gift::volume).reversed().thenComparing(Comparator.comparing(Gift::weight).reversed()))
            .collect(Collectors.toList());
        if (actualNow.isEmpty()) {
            actualNow = removeLastV(gifts, currentGifts);
        }
        return actualNow;
    }

    private static List<Gift> removeLastW(List<Gift> gifts, List<Gift> currentGifts) {
        List<Gift> actualNow;
        Gift giftForSkip = currentGifts.remove(currentGifts.size() - 1);
        gifts.add(giftForSkip);
//        curBag.addAndGet(-1 * (giftForSkip.volume() * 10000 + giftForSkip.weight()));
        actualNow = gifts.stream().filter(g -> g.weight() < giftForSkip.weight())
            .sorted(Comparator.comparing(Gift::volume).reversed().thenComparing(Comparator.comparing(Gift::weight).reversed()))
            .collect(Collectors.toList());
        if (actualNow.isEmpty()) {
            actualNow = removeLastW(gifts, currentGifts);
        }
        return actualNow;
    }
}
