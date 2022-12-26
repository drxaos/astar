package org.example.datsanta.part2;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RoungGetResult {

    @lombok.Data
    @NoArgsConstructor
    public static class ResData {
        String error_message;
        String status;
        Integer total_happy;
    }

    ResData data;
    String error;
    boolean success;
}

