package org.example.datsanta.part2;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.datsanta.part3.GiftType3;

@Data
@AllArgsConstructor
public class Presenting {
    int giftID;
    int x;
    int y;
    @JsonIgnore
    int price;
    @JsonIgnore
    GiftType3 giftType;
    @JsonIgnore
    ChildType childType;
    @JsonIgnore
    int volume;
    @JsonIgnore
    int weight;

    public Presenting(int giftID, int childID) {

    }
}
