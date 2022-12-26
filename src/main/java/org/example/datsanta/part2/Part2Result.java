package org.example.datsanta.part2;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Part2Result {
    String mapID;
    List<Presenting> presentingGifts;
}
