package org.example.datsanta;

import java.util.List;

public record DsMap(
        List<Gift> gifts,
        List<Child> children,
        List<SnowArea> snowAreas
) {

}
