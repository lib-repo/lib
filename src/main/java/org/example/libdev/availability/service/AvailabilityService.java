package org.example.libdev.availability.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.availability.entity.Availability;
import org.example.libdev.availability.repository.AvailabilityRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final AvailabilityRepository availabilityRepository;

    public Availability getAvailability(Long availabilityId) {
        try {
            Availability availability = availabilityRepository.findById(availabilityId)
                    .orElseThrow(() -> new NoSuchElementException("해당 ID의 대여 여부를 찾을 수 없습니다." + availabilityId));
            return availability;
        } catch (NoSuchElementException e) {
            log.error("대여 여부 조회 실패: {}", e.getMessage());
            throw new RuntimeException("해당 ID의 대여 여부를 찾을 수 없습니다." + availabilityId);
        } catch (Exception e) {
            log.error("대여 여부 조회 중 오류가 발생: {}", e.getMessage());
            throw new RuntimeException("대여 여부 조회 중 오류가 발생했습니다.");
        }
    }
}
