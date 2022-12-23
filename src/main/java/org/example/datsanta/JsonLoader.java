package org.example.datsanta;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class JsonLoader {
    @Getter
    DsMap dsMap;

    public void load(String path) {
        try {
            dsMap = new ObjectMapper().readValue(new File(path), DsMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Child, Set<Child>> toNodes() {
        final Map<Child, Set<Child>> result = new HashMap<>();

        TreeSet<Child> children = new TreeSet<>(dsMap.children());
        children.add(new Child(0, 0));

        dsMap.snowAreas().forEach(snowArea -> {
            for (int i = 0; i < 100; i++) {
                final int x = snowArea.x() + (int) ((snowArea.r() + 2) * Math.cos(2 * Math.PI * i / 100));
                final int y = snowArea.y() + (int) ((snowArea.r() + 2) * Math.sin(2 * Math.PI * i / 100));
                if (x >= 0 && x < 10000 && y >= 0 && y < 10000) {
                    children.add(new Child(x, y));
                }
            }
        });

        for (Child from : children) {
            result.put(from, children);
        }

        return result;
    }
}
