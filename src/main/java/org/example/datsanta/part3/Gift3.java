package org.example.datsanta.part3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Gift3 {
    int id;
    int weight;
    int volume;
    GiftType3 type;
    int price;
}