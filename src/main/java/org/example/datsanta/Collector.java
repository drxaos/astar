package org.example.datsanta;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Collector {

    public static List<List<Gift>> collectGifts(DsMap resp)  {
        //        HttpHeaders headers = new HttpHeaders();
        //        headers.add("X-API-Key", "90a75999-2d77-41d9-aa22-26a85571da53");
        //        headers.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        //        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        //        final ResponseEntity<String> exchange = new RestTemplate()
        //                .exchange(
        //                        new URI("https://datsanta.dats.team/json/map/faf7ef78-41b3-4a36-8423-688a61929c08.json"),
        //                        HttpMethod.GET,
        //                        entity,
        //                        String.class
        //                );

        //        final DsMap resp = new ObjectMapper().readValue(exchange.getBidy(), DsMap.class);
        //        doWork(new int[]{3, 4, 5, 8, 9}, new int[]{1, 6, 4, 7, 6});

        final List<Gift> gifts = resp.gifts();

        gifts.sort(Comparator.comparing(Gift::volume, Comparator.reverseOrder())
                .thenComparing(Comparator.comparing(Gift::weight, Comparator.reverseOrder())));

        final int we = gifts.stream()
                .map(Gift::weight)
                .mapToInt(e -> e)
                .sum();
        final int vo = gifts.stream()
                .map(Gift::volume)
                .mapToInt(e -> e)
                .sum();
        System.out.println(we); //200 -> 40 по весу
        System.out.println(vo); //100 -> 46 по объему
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

        System.out.println(result.size());
        int count = 0;
        System.out.println(count);
        return result;
    }
}
