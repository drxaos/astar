package org.example.datsanta;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Validator {
    static Child zero = new Child(0, 0);

    public static void main(String[] args) throws Exception {

        DsResult dsResult = new ObjectMapper().readValue(new File("faf7ef78-41b3-4a36-8423-688a61929c08_result_1671977394381_len_948553_cost_1190321.json"), DsResult.class);
        DsMap dsMap = new ObjectMapper().readValue(new File("faf7ef78-41b3-4a36-8423-688a61929c08_map.json"), DsMap.class);

//
//        HashSet<Child> childrenSet = new HashSet<>(dsMap.children());
//        List<List<Child>> clusters = new ArrayList<>();
//        List<Child> currentCluster = new ArrayList<>();
//        clusters.add(currentCluster);
//        for (Child move : dsResult.moves()) {
//            if (move.equals(zero)) {
//                if (clusters.size() == 9) {
//                    break;
//                }
//                currentCluster = new ArrayList<>();
//                clusters.add(currentCluster);
//                continue;
//            }
//            if (!childrenSet.contains(move)) {
//                continue;
//            }
//            currentCluster.add(move);
//        }
//        clusters.remove(currentCluster);
//        for (int i = 0; i < clusters.size(); i++) {
//            List<Child> cluster = clusters.get(i);
//            Set<Child> result = currentCluster.stream()
//                    .distinct()
//                    .filter(cluster::contains)
//                    .collect(Collectors.toSet());
//            System.out.println("intersection " + i + ": " + result);
//        }


        Collections.reverse(dsResult.stackOfBags());
        List<Integer> bag = dsResult.stackOfBags().remove(0);
        System.out.println("bag " + bag.size());

        HashSet<Child> remain = new HashSet<>(dsMap.children());
        List<Child> moves = dsResult.moves();
        for (int i = 0; i < moves.size(); i++) {
            Child move = moves.get(i);
            if (remain.remove(move)) {
                if (bag.size() > 0) {
                    bag.remove(0);
                    //System.out.println("bag remove " + bag.size());
                    for (int j = i + 1; j < moves.size(); j++) {
                        if (moves.get(j).equals(move)) {
                            //System.out.println("more");
                        }
                    }
                } else {
                    System.out.println("no gift");
                }
            }
            if (remain.isEmpty()) {
                System.out.println("done");
                break;
            }
            if (move.equals(zero)) {
                if (dsResult.stackOfBags().isEmpty()) {
                    System.out.println("no more bags");
                    break;
                }
                if (!bag.isEmpty()) {
                    System.out.println("!!!");
                }
                bag = dsResult.stackOfBags().remove(0);
                System.out.println("bag " + bag.size());
            }
        }
        System.out.println(remain);

    }
}
