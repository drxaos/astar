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

    public void load(DsMap dsMap) {
        this.dsMap = dsMap;
    }

    public void loadJson(String json) {
        try {
            dsMap = new ObjectMapper().readValue(json, DsMap.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Child, Set<Child>> toNodes() {
        final Map<Child, Set<Child>> result = new HashMap<>();

        TreeSet<Child> children = new TreeSet<>(dsMap.children());
        children.add(new Child(0, 0));

        dsMap.snowAreas().forEach(snowArea -> {
            // 1000/(cos(pi/50))=1001.97717
            // 1000/(cos(pi/70))=1001.00795
            // 1000/(cos(pi/80))=1000.77156
            int N = Math.min(130, (int) (snowArea.r() * 3.14 * 2 / 50));
            for (int i = 0; i < N; i++) {
                final int x = snowArea.x() + (int) ((snowArea.r() + 3) * Math.cos(2 * Math.PI * i / N));
                final int y = snowArea.y() + (int) ((snowArea.r() + 3) * Math.sin(2 * Math.PI * i / N));
                if (x >= 0 && x < 10000 && y >= 0 && y < 10000) {
                    children.add(new Child(x, y));
                }
            }
        });

//        for (int i = 0; i < 10000; i += 200) {
//            for (int j = 0; j < 10000; j += 200) {
//                children.add(new Child(i, j));
//            }
//        }

        for (Child from : children) {
            result.put(from, children);
        }


        return result;
    }
}
