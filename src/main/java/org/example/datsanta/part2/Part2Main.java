package org.example.datsanta.part2;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.File;

public class Part2Main {
    @SneakyThrows
    public static void main(String[] args) {
        final Step2Map step2Map = new ObjectMapper().readValue(new File("part2_gift.json"), Step2Map.class);
        System.out.println(step2Map);
    }
}
