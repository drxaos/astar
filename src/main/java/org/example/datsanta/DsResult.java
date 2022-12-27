package org.example.datsanta;

import java.util.List;

public record DsResult(
        String mapID,
        List<ChildResult> moves,
        List<List<Integer>> stackOfBags
) {
}
