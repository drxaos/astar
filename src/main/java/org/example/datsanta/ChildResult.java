package org.example.datsanta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.example.datsanta.part2.Gender;

import java.util.Map;

public record ChildResult(
        int x,
        int y
) {
}