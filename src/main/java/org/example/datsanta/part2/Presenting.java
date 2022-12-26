package org.example.datsanta.part2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Presenting {
    int giftID;
    int childID;
    int price;

    public Presenting(
            int giftID,
            int childID
    ) {
        this.giftID = giftID ;
        this.childID = childID;
    }
}
