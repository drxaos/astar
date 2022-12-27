package org.example.datsanta.part2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Presenting {
    int giftID;
    int childID;
    @JsonIgnore
    int price;
    @JsonIgnore
    GiftType giftType;
    @JsonIgnore
    ChildType childType;

    public Presenting(int giftID, int childID) {
        this.giftID = giftID;
        this.childID = childID;
    }
}
