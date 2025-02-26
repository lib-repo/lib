package org.example.libdev.availability.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.availability.entity.Availability;
import org.example.libdev.availability.service.AvailabilityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/available")
@RequiredArgsConstructor
public class AvailabilityRestController {

    private final AvailabilityService availabilityService;

    @GetMapping("/{availabilityId}")
    public ResponseEntity<Availability> getAvailability(@PathVariable("availabilityId") Long availabilityId) {
        try {
            return ResponseEntity.ok(availabilityService.getAvailability(availabilityId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
