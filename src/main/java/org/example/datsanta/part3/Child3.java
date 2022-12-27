package org.example.datsanta.part3;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Child3 {
    int id;
    int x;
    int y;
    int age;
    Gender3 gender;
}
