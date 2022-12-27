package org.example.datsanta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.example.datsanta.part2.Presenting;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Collector {
    static String apiKey = "90a75999-2d77-41d9-aa22-26a85571da53";
    static String mapId = "faf7ef78-41b3-4a36-8423-688a61929c08";

//    public static void main(String[] args) throws Exception {
//        JsonLoader loader = new JsonLoader();
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("X-API-Key", apiKey);
//        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
//        HttpEntity<String> entity = new HttpEntity<>(null, headers);
//        final ResponseEntity<String> exchange = new RestTemplate()
//                .exchange(
//                        new URI("https://datsanta.dats.team/json/map/" + mapId + ".json"),
//                        HttpMethod.GET,
//                        entity,
//                        String.class
//                );
//        String mapJson = exchange.getBody();
//        Files.write(Paths.get("" + mapId + "_map.json"), mapJson.getBytes());
//
//
//        //loader.load("src/main/java/org/example/datsanta/faf7ef78-41b3-4a36-8423-688a61929c08.json");
//        loader.loadJson(mapJson);
//        final Map<Child, Set<Child>> nodes = loader.toNodes();
//
//        long startTime = System.currentTimeMillis();
//
//        final List<List<Gift>> lists1 = collectGiftsV3(loader.dsMap);
////        crnt best 97 197 88 158 bags 46
////        98, 199
//        //2  150 75
////        final List<List<Gift>> lists = Collector.collectGiftsV3(loader.getDsMap(), 98, 199);
//
//        final ExecutorService executorService = Executors.newFixedThreadPool(20, tf);
//        AtomicInteger best = new AtomicInteger(100);
//        AtomicInteger bv = new AtomicInteger(0);
//        AtomicInteger bw = new AtomicInteger(0);
//        AtomicInteger bv2 = new AtomicInteger(0);
//        AtomicInteger bw2 = new AtomicInteger(0);
//        for (int v = 97; v <= 100; v++) {
//            for (int w = 197; w <= 200; w++) {
//                for (int v2 = 50; v2 < 100; v2++) {
//                    for (int w2 = 100; w2 < 200; w2++) {
//                        final AtomicInteger vA = new AtomicInteger(v);
//                        final AtomicInteger wA = new AtomicInteger(w);
//                        final AtomicInteger v2A = new AtomicInteger(v2);
//                        final AtomicInteger w2A = new AtomicInteger(w2);
//                        executorService.submit(() -> {
//                            try {
//
//                                if (w2A.get() % 50 == 0) {
//                                    System.out.println("crnt best %s %s %s %s bags %s".formatted(bv.get(), bw.get(), bv2.get(), bw2.get(), best.get()));
//                                }
//
//                                final List<List<Gift>> lists = Collector.collectGiftsV3(loader.getDsMap(), vA.get(), wA.get(), v2A.get(), w2A.get());
//                                System.out.printf("result %s, %s %s %s %s bags%n", vA.get(), wA.get(), v2A.get(), w2A.get(), lists.size());
//                                final int tmpBest = best.get();
//                                int tmpV = bv.get();
//                                int tmpW = bw.get();
//                                int tmpV2 = bv2.get();
//                                int tmpW2 = bw2.get();
//
//                                if (lists.size() < tmpBest) {
//                                    best.compareAndSet(tmpBest, lists.size());
//                                    bv.compareAndSet(tmpV, vA.get());
//                                    bw.compareAndSet(tmpW, wA.get());
//                                    bv2.compareAndSet(tmpV2, v2A.get());
//                                    bw2.compareAndSet(tmpW2, w2A.get());
//                                }
//                            } catch (Exception e) {
//                                System.out.println("result %s, %s %s %s error".formatted(vA.get(), wA.get(), v2A.get(), w2A.get()));
//                            }
//                        });
//
//                    }
//                }
//
//            }
//        }
//        System.out.println("best: " + best + " bv %s bw %s bv2 %s bw2 %s".formatted(bv, bw, bv2, bw2));
////        final List<List<Gift>> bags = Collector.collectGiftsV3(loader.getDsMap());
//
//        System.out.println("");
//    }

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
    public static List<List<Presenting>> collectGiftsV2(List<Presenting> resp) {

        final List<Presenting> gifts =new ArrayList<>(resp);

        final ArrayList<Presenting> forCheck = new ArrayList<>(gifts);
        gifts.sort(Comparator.comparing(Presenting::getVolume).reversed());
//                .thenComparing(Gift::weight));


        List<List<Presenting>> result = new ArrayList<>();
        result.add(new ArrayList<>());

        int currentVolume = 0;
        int currentWeight = 0;
        boolean q = true;
//        for (int i = 0; i < gifts.size(); i++) {
        while (!gifts.isEmpty()) {
            final Presenting gift = getGift(gifts, q);
            q = !q;

            if (currentVolume + gift.getVolume() <= 100 && currentWeight + gift.getWeight() <= 200) {
                result.get(result.size() - 1).add(gift);
                currentVolume += gift.getVolume();
                currentWeight += gift.getWeight();
                gifts.remove(gift);
            } else {
                final int finalCurrentWeight = currentWeight;
                final int finalCurrentVolume = currentVolume;

                final Optional<Presenting> optimum = gifts.stream()
                        .filter(e -> (e.getWeight() == 200 - finalCurrentWeight && e.getVolume() == 100 - finalCurrentVolume))
                        .findFirst();

                final Optional<Presenting> optimumV = gifts.stream()
                        .filter(e -> (e.getWeight() <= 200 - finalCurrentWeight && e.getVolume() == 100 - finalCurrentVolume))
                        .max(Comparator.comparingInt(Presenting::getWeight).thenComparing(Comparator.comparingInt(Presenting::getVolume)));

                final Optional<Presenting> first = gifts.stream()
                        .filter(e -> (e.getWeight() <= 200 - finalCurrentWeight && e.getVolume() <= 100 - finalCurrentVolume))
                        .max(Comparator.comparingInt(Presenting::getWeight).thenComparing(Comparator.comparingInt(Presenting::getVolume)));

                if (optimum.isPresent()) {
                    result.get(result.size() - 1).add(optimum.get());
                    currentVolume += optimum.get().getVolume();
                    currentWeight += optimum.get().getWeight();
                    gifts.remove(optimum.get());
                } else if (optimumV.isPresent()) {
                    result.get(result.size() - 1).add(optimumV.get());
                    currentVolume += optimumV.get().getVolume();
                    currentWeight += optimumV.get().getWeight();
                    gifts.remove(optimumV.get());
                } else if (first.isPresent()) {
                    result.get(result.size() - 1).add(first.get());
                    currentVolume += first.get().getVolume();
                    currentWeight += first.get().getWeight();
                    gifts.remove(first.get());
                } else {
                    result.add(new ArrayList<>());
                    result.get(result.size() - 1).add(gift);
                    currentVolume = gift.getVolume();
                    currentWeight = gift.getWeight();
                    gifts.remove(gift);
                }
            }
        }

        final List<Pair> pairs = result.stream()
                .map(e -> {
                    return new Pair(e.stream().mapToInt(r -> r.getWeight()).sum(), e.stream().mapToInt(r -> r.getVolume()).sum());
                })
                .sorted((e1, e2) -> Integer.compare(e2.w, e1.w))
                .toList();

//        System.out.println(new ObjectMapper().writeValueAsString(result));
        final List<Integer> v = result.stream().map(e -> e.stream().map(Presenting::getVolume).mapToInt(a -> a).sum()).toList();
        final List<Integer> w = result.stream().map(e -> e.stream().map(Presenting::getWeight).mapToInt(a -> a).sum()).toList();
//        System.out.println(new ObjectMapper().writeValueAsString(v));
//        System.out.println(new ObjectMapper().writeValueAsString(result.stream().map(e -> e.stream().map(Gift::weight).mapToInt(a -> a).sum()).toList()));
        for (int i = 0; i < v.size(); i++) {
            System.out.println(w.get(i) + "_" + v.get(i));
        }

        System.out.println("bags " + result.size() + ": " + result.stream().map(List::size).toList());

//        final List<Presenting> gifts1 = new ArrayList<>(result.stream().flatMap(e -> e.stream()).toList());
//        gifts1.sort(Comparator.comparingInt(Presenting::id));
//        forCheck.sort(Comparator.comparingInt(Presenting::id));
//        System.out.println(forCheck.equals(gifts1));
        return result;
    }

    private static Presenting getGift(final List<Presenting> gifts, boolean q) {
        if (!q) {
            return gifts.stream().max(Comparator.comparingInt(Presenting::getWeight).thenComparing(Comparator.comparingInt(Presenting::getVolume))).get();

        }
        return gifts.stream().max(Comparator.comparingInt(Presenting::getWeight).reversed().thenComparing(Comparator.comparingInt(Presenting::getVolume))).get();
    }

    //98 199 150 75 bags 47
    //97 197 89 164 bags 46
    public static List<List<Presenting>> collectGiftsV3(List<Presenting> resp) {
        return collectGiftsV3(resp, 97, 197, 72, 165);
    }

    @SneakyThrows
    public static List<List<Presenting>> collectGiftsV3(List<Presenting> resp, int v, int w, int v2, int w2) {

        List<List<Presenting>> result = new ArrayList<>();

        final List<Presenting> gifts = new ArrayList<>(resp);
        gifts.sort(Comparator.comparing(Presenting::getVolume).reversed().thenComparing(Comparator.comparing(Presenting::getWeight).reversed()));
        List<Presenting> actualNow = new ArrayList<>(gifts);

        List<Presenting> currentGifts = new ArrayList<>();


        int i = 0;
        int iter_count = 0;

        while (!gifts.isEmpty()) {
            iter_count++;

            if (actualNow.size() == 0 || actualNow.size() == 1) {
//                System.out.println();
            }
            final Presenting gift = actualNow.get(i);

//            int curItemValue = gift.volume() * 10000 + gift.weight();

            int onBaseV = 0; //100
            int onBaseW = 0; //200
            for (Presenting currentGift : gifts) {
                onBaseV += currentGift.getVolume();
                onBaseW += currentGift.getWeight();
            }

            int realV = 0; //100 7-2
            int realW = 0; //200 12-4
            for (Presenting currentGift : currentGifts) {
                realV += currentGift.getVolume();
                realW += currentGift.getWeight();
            }

            if (100 - realV >= gift.getVolume() && 200 - realW >= gift.getWeight()) {
//                curBag.addAndGet(curItemValue);
                currentGifts.add(gift);

                gifts.remove(gift);
                actualNow.remove(i);
                i = 0;

                realV = 0; //100
                realW = 0; //200
                for (Presenting currentGift : currentGifts) {
                    realV += currentGift.getVolume();
                    realW += currentGift.getWeight();
                }
//150 75
                if ((realV >= v && realW >= w2) || (realW >= w && realV >= v2)) {
                    result.add(currentGifts);

//                    System.out.println("Bag is completed " + result.size());
                    actualNow = new ArrayList<>(gifts);

                    //final List<Gift> giftStream = actualNow.stream().filter(gift1 -> gift1.id() == 17).toList();

                    currentGifts = new ArrayList<>();
                    continue;
                }
            }
            if (iter_count == 2000) {
                System.out.println("iter");
            }

            if (i == actualNow.size() - 1 || realV >= 99 || realW >= 197 || iter_count == 5000) {
                iter_count = 0;
                onBaseV = 0; //100
                onBaseW = 0; //200
                for (Presenting currentGift : gifts) {
                    onBaseV += currentGift.getVolume();
                    onBaseW += currentGift.getWeight();
                }

                if (100 - realV < 7 && 200 - realW < 12) {
                    actualNow = removeLast(gifts, currentGifts);
                } else if (100 - realV < 7) {
                    actualNow = removeLastV(gifts, currentGifts);
                } else if (200 - realW < 12) {
                    actualNow = removeLastW(gifts, currentGifts);
                }

                realV = 0; //100
                realW = 0; //200
                for (Presenting currentGift : currentGifts) {
                    realV += currentGift.getVolume();
                    realW += currentGift.getWeight();
                }

                i = 0;
                continue;
            }
            i++;
        }
        result.add(currentGifts);
//        System.out.println("");
//        gi
//        final List<Integer> v = result.stream().map(e -> e.stream().map(Gift::volume).mapToInt(a -> a).sum()).toList();
//        final List<Integer> w = result.stream().map(e -> e.stream().map(Gift::weight).mapToInt(a -> a).sum()).toList();
//        System.out.println(new ObjectMapper().writeValueAsString(v));
//        System.out.println(new ObjectMapper().writeValueAsString(result.stream().map(e -> e.stream().map(Gift::weight).mapToInt(a -> a).sum()).toList()));
//        for (int j= 0; j < v.size(); j++) {
//            System.out.println(v.get(j) + "_" + w.get(j));
//        }

//        System.out.println("bags " + result.size() + ": " + result.stream().map(List::size).toList());
//

//        final List<Presenting> gifts1 = new ArrayList<>(result.stream().flatMap(e -> e.stream()).toList());
//        gifts1.sort(Comparator.comparingInt(Presenting::id));
//        resp.gifts().sort(Comparator.comparingInt(Gift::id));
//        System.out.println(resp.gifts().equals(gifts1));

//        resp.gifts().removeAll(gifts1);
//        System.out.println(resp.gifts());

        // todo validate

        return result;
    }

    private static List<Presenting> removeLast(List<Presenting> gifts, List<Presenting> currentGifts) {
        List<Presenting> actualNow;
        Presenting giftForSkip = currentGifts.remove(currentGifts.size() - 1);
        gifts.add(giftForSkip);
//        curBag.addAndGet(-1 * (giftForSkip.volume() * 10000 + giftForSkip.weight()));
        actualNow = gifts.stream().filter(g -> g.getVolume() < giftForSkip.getVolume() && g.getWeight() < giftForSkip.getWeight())
                .sorted(Comparator.comparing(Presenting::getVolume).reversed().thenComparing(Comparator.comparing(Presenting::getWeight).reversed()))
                .collect(Collectors.toList());
        if (actualNow.isEmpty()) {
            actualNow = removeLast(gifts, currentGifts);
        }
        return actualNow;
    }

    private static List<Presenting> removeLastV(List<Presenting> gifts, List<Presenting> currentGifts) {
        List<Presenting> actualNow;
        Presenting giftForSkip = currentGifts.remove(currentGifts.size() - 1);
        gifts.add(giftForSkip);
//        curBag.addAndGet(-1 * (giftForSkip.volume() * 10000 + giftForSkip.weight()));
        actualNow = gifts.stream().filter(g -> g.getVolume() < giftForSkip.getVolume())
                .sorted(Comparator.comparing(Presenting::getVolume).reversed().thenComparing(Comparator.comparing(Presenting::getWeight).reversed()))
                .collect(Collectors.toList());
        if (actualNow.isEmpty()) {
            actualNow = removeLastV(gifts, currentGifts);
        }
        return actualNow;
    }

    private static List<Presenting> removeLastW(List<Presenting> gifts, List<Presenting> currentGifts) {
        List<Presenting> actualNow;
        Presenting giftForSkip = currentGifts.remove(currentGifts.size() - 1);
        gifts.add(giftForSkip);
//        curBag.addAndGet(-1 * (giftForSkip.volume() * 10000 + giftForSkip.weight()));
        actualNow = gifts.stream().filter(g -> g.getWeight() < giftForSkip.getWeight())
                .sorted(Comparator.comparing(Presenting::getVolume).reversed().thenComparing(Comparator.comparing(Presenting::getWeight).reversed()))
                .collect(Collectors.toList());
        if (actualNow.isEmpty()) {
            actualNow = removeLastW(gifts, currentGifts);
        }
        return actualNow;
    }

    @Data
    @AllArgsConstructor
    static class Pair {
        int w;
        int v;
    }
}
