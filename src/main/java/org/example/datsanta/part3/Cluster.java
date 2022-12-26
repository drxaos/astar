package org.example.datsanta.part3;

import lombok.Data;

import java.util.List;

@Data
public class Cluster {

    int cost;
    int volume;
    int weight;
    List<GiftToChild> giftToChild;

}
