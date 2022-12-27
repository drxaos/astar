package org.example.datsanta;

import org.example.datsanta.part2.ChildType;
import org.example.datsanta.part2.Presenting;
import org.example.datsanta.part3.GiftType3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.datsanta.part2.Gender.female;
import static org.example.datsanta.part2.Gender.male;
import static org.example.datsanta.part3.GiftType3.bath_toys;
import static org.example.datsanta.part3.GiftType3.bike;
import static org.example.datsanta.part3.GiftType3.casket;
import static org.example.datsanta.part3.GiftType3.educational_games;
import static org.example.datsanta.part3.GiftType3.music_games;
import static org.example.datsanta.part3.GiftType3.paints;
import static org.example.datsanta.part3.GiftType3.soccer_ball;
import static org.example.datsanta.part3.GiftType3.toy_kitchen;

public class Shafler {
    static Map<ChildType, List<GiftType3>> best = new HashMap<>() {{
        put(new ChildType(male, 0), List.of(bath_toys));
        put(new ChildType(male, 1), List.of(bath_toys));
        put(new ChildType(male, 2), List.of(bath_toys));
        put(new ChildType(male, 3), List.of(educational_games));
        put(new ChildType(male, 4), List.of(educational_games, paints));
        put(new ChildType(male, 5), List.of(educational_games, paints));
        put(new ChildType(male, 6), List.of(soccer_ball, music_games, educational_games, paints));
        put(new ChildType(male, 7), List.of(soccer_ball, music_games, paints));
        put(new ChildType(male, 8), List.of(soccer_ball, bike));
        put(new ChildType(male, 9), List.of(soccer_ball, bike));
        put(new ChildType(male, 10), List.of(soccer_ball, bike));
        put(new ChildType(female, 0), List.of(bath_toys));
        put(new ChildType(female, 1), List.of(bath_toys));
        put(new ChildType(female, 2), List.of(bath_toys));
        put(new ChildType(female, 3), List.of(toy_kitchen, educational_games));
        put(new ChildType(female, 4), List.of(toy_kitchen, educational_games, paints));
        put(new ChildType(female, 5), List.of(toy_kitchen, educational_games, paints));
        put(new ChildType(female, 6), List.of(toy_kitchen, educational_games, paints));
        put(new ChildType(female, 7), List.of(music_games));
        put(new ChildType(female, 8), List.of(music_games, bike));
        put(new ChildType(female, 9), List.of(bike, casket));
        put(new ChildType(female, 10), List.of(bike, casket));
    }};

    public static void main(String[] args) {
        final ArrayList<Presenting> presentings1 = new ArrayList<>(List.of(
                new Presenting(12, 12, 1, 1, toy_kitchen, new ChildType(male, 10), 20, 30),
                new Presenting(14, 12, 1, 1, educational_games, new ChildType(male, 10), 10, 50)
        ));
        final ArrayList<Presenting> presentings2 = new ArrayList<>(List.of(
                new Presenting(16, 12, 1, 1, bike, new ChildType(female, 4), 20, 30),
                new Presenting(18, 12, 1, 1, soccer_ball, new ChildType(female, 4), 10, 50)
        ));
        final List<List<Presenting>> objects = new ArrayList<>();
        objects.add(presentings1);
        objects.add(presentings2);

        final ArrayList<Presenting> presentings = new ArrayList<>(presentings1);
        presentings.addAll(presentings2);
        doWork(presentings);
        doWorkWithList(objects);

        //16 18 12 14
        System.out.println("");
    }

    public static void doWorkWithList(List<List<Presenting>> presentingsList) {
        for (int q = 0; q < presentingsList.size() - 1; q++) {
            final List<Presenting> presentings = presentingsList.get(q);
            for (int i = 0; i < presentings.size() - 1; i++) {
                final Presenting presenting1 = presentings.get(i);
                if (!best.get(presenting1.getChildType()).contains(presenting1.getGiftType())) {
                    for (int j = i + 1; j < presentings.size(); j++) {
                        final Presenting presenting2 = presentings.get(j);
                        if (best.get(presenting1.getChildType()).contains(presenting2.getGiftType())) {
                            final int giftID1 = presenting1.getGiftID();
                            final int giftID2 = presenting2.getGiftID();
                            presenting1.setGiftID(giftID2);
                            presenting2.setGiftID(giftID1);
                        } else {
                            for (int w = q + 1; w < presentingsList.size(); w++) {
                                final List<Presenting> presentingsNext = presentingsList.get(w);
                                for (int a = 0; a < presentingsNext.size(); a++) {
                                    final Presenting presenting3 = presentingsNext.get(a);
                                    if (best.get(presenting1.getChildType()).contains(presenting3.getGiftType())) {
                                        if (presenting3.getWeight() == presenting1.getWeight() && presenting3.getVolume() == presenting1.getVolume()) {
                                            final int giftID1 = presenting1.getGiftID();
                                            final int giftID2 = presenting3.getGiftID();
                                            presenting1.setGiftID(giftID2);
                                            presenting3.setGiftID(giftID1);
                                        } else if (presenting3.getWeight() == presenting2.getWeight() && presenting3.getVolume() == presenting2.getVolume()) {
                                            final int giftID1 = presenting2.getGiftID();
                                            final int giftID2 = presenting3.getGiftID();
                                            presenting2.setGiftID(giftID2);
                                            presenting3.setGiftID(giftID1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public static void doWork(List<Presenting> presentings) {
        for (int i = 0; i < presentings.size(); i++) {
            final Presenting presenting1 = presentings.get(i);

            if (!best.get(presenting1.getChildType()).contains(presenting1.getGiftType())) {
                for (int j = 0; j < presentings.size(); j++) {

                    final Presenting presenting2 = presentings.get(j);

                    if (best.get(presenting1.getChildType()).contains(presenting2.getGiftType())) {
                        final int giftID1 = presenting1.getGiftID();
                        final GiftType3 giftType1 = presenting1.getGiftType();
                        final int giftID2 = presenting2.getGiftID();
                        final GiftType3 giftType2 = presenting2.getGiftType();
                        presenting1.setGiftID(giftID2);
                        presenting1.setGiftType(giftType2);
                        presenting2.setGiftID(giftID1);
                        presenting2.setGiftType(giftType1);
                        break;
                    } else if (best.get(presenting2.getChildType()).contains(presenting1.getGiftType())) {
                        final int giftID1 = presenting1.getGiftID();
                        final GiftType3 giftType1 = presenting1.getGiftType();
                        final int giftID2 = presenting2.getGiftID();
                        final GiftType3 giftType2 = presenting2.getGiftType();
                        presenting1.setGiftID(giftID2);
                        presenting1.setGiftType(giftType2);
                        presenting2.setGiftID(giftID1);
                        presenting2.setGiftType(giftType1);
                        break;
                    }
                }
            }
        }
    }
}
