package org.example.datsanta;

import java.util.List;

public class Debug {
    public static void main(String[] args) {
        var cls = new CircleLineScorer(List.of(
                new SnowArea(350,1106,4150)
        ));
        var a = new Child(1372, 4377);
//        var a = new Child(1373, 4378);
        var b = new Child(934, 3957);
        List.of(
                cls.computeCost(a,b),
                cls.computeCost(b,a)
        );

    }
}
