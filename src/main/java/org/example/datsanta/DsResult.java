package org.example.datsanta;

import java.util.List;

public record DsResult(
        String mapID,
        List<Child> moves,
        List<List<Integer>> stackOfBags
) {
}
