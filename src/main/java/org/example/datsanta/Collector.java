package org.example.datsanta;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Collector {

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
}
