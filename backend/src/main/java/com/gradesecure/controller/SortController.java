package com.gradesecure.controller;

import com.gradesecure.dto.SortResponse;
import com.gradesecure.service.RadixSortService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/classes")
public class SortController {

    private final RadixSortService radixSortService;

    public SortController(RadixSortService radixSortService) {
        this.radixSortService = radixSortService;
    }

    /**
     * POST /api/classes/{id}/sort
     * Flowchart 4: Radix Sort + apply bonus
     * ★ DSA: Triggers the LSD Radix Sort algorithm on student grades
     */
    @PostMapping("/{id}/sort")
    public ResponseEntity<SortResponse> sortAndApplyBonus(@PathVariable Long id) {
        SortResponse response = radixSortService.sortAndApplyBonus(id);
        return ResponseEntity.ok(response);
    }
}
