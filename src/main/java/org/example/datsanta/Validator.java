package org.example.datsanta;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Validator {
    public static void main(String[] args) throws Exception {

        DsResult dsResult = new ObjectMapper().readValue(new File("faf7ef78-41b3-4a36-8423-688a61929c08_result_1671966612877_len_964269_cost_1215030_rid_01GN4G1QYK3Z76F10TRGEWDKMD.json"), DsResult.class);
        DsMap dsMap = new ObjectMapper().readValue(new File("faf7ef78-41b3-4a36-8423-688a61929c08_map.json"), DsMap.class);

        Child zero = new Child(0, 0);
        Collections.reverse(dsResult.stackOfBags());
        List<Integer> bag = dsResult.stackOfBags().remove(0);

        HashSet<Child> remain = new HashSet<>(dsMap.children());
        List<Child> moves = dsResult.moves();
        for (int i = 0; i < moves.size(); i++) {
            Child move = moves.get(i);
            if (bag.size() > 0 && remain.remove(move)) {
                bag.remove(0);
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
            }
        }
        System.out.println(remain);

    }
}
