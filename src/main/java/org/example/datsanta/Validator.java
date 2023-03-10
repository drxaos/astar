package org.example.datsanta;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class Validator {
    static Child zero = new Child(0, 0);

    public static void main(String[] args) throws Exception {
        System.out.println(test(
                "faf7ef78-41b3-4a36-8423-688a61929c08_map.json",
                "faf7ef78-41b3-4a36-8423-688a61929c08_result_1671976532651_len_940045_cost_1186460.json"
        ));
    }

    public static boolean test(String mapFile, String resultFile) throws Exception {

        DsResult dsResult = new ObjectMapper().readValue(new File(resultFile), DsResult.class);
        DsMap dsMap = new ObjectMapper().readValue(new File(mapFile), DsMap.class);

        HashSet<Child> childrenSet = new HashSet<>(dsMap.children());
        List<ChildResult> moves1 = dsResult.moves();
        while(!childrenSet.contains(moves1.get(moves1.size()-1))){
            moves1.remove(moves1.size()-1);
        }
        new ObjectMapper().writeValue(new File("faf7ef78-41b3-4a36-8423-688a61929c08_result_1671976532651_len_940045_cost_1186460_fixed.json"), dsResult);


        Collections.reverse(dsResult.stackOfBags());
        List<Integer> bag = dsResult.stackOfBags().remove(0);
        //System.out.println("bag " + bag.size());

        HashSet<Child> remain = new HashSet<>(dsMap.children());
        List<ChildResult> moves = dsResult.moves();
        for (int i = 0; i < moves.size(); i++) {
            ChildResult move = moves.get(i);
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
                    //System.out.println("no gift");
                }
            }
            if (remain.isEmpty()) {
                System.out.println("done");
                return true;
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
                //System.out.println("bag " + bag.size());
            }
        }
        //System.out.println(remain);
        return false;
    }
}
