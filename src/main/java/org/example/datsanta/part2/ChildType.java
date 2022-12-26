package org.example.datsanta.part2;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChildType implements Comparable<ChildType> {
    Gender gender;
    int age;

    @Override
    public int compareTo(ChildType o) {
        return ("" + gender + "" + age).compareTo("" + o.gender + "" + o.age);
    }

    String str() {
        return "" + gender + age;
    }
}
