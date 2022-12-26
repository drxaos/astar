package org.example.datsanta.part2;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoundPostResult {
    boolean success;
    String error;
    String roundId;

}
