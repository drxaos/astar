package org.example.datsanta.part2;

import lombok.Data;

@Data
public class Gift {
    int id;
    GiftType type;
    int price;
}
