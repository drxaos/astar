package org.example.datsanta;

import org.example.datsanta.part3.GiftType3;

public record Gift(
        int id,
        int weight,
        int volume,
        GiftType3 type,
        int price
) {
}
