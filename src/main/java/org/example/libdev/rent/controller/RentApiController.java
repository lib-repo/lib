package org.example.libdev.rent.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.rent.service.RentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rent")
public class RentApiController {
    private final RentService rentService;

    /**
     * rent 생성
     */
    @PostMapping("/{userId}/{bookId}/{libraryId}")
    public ResponseEntity<String> createRent(@PathVariable Long userId, @PathVariable Long bookId, @PathVariable Long libraryId) {
        try{
            rentService.saveRent(userId, bookId,libraryId);
            return ResponseEntity.ok().build();
        }catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * rent 연장
     */
    @PostMapping("/renew/{rentId}")
    public ResponseEntity<String> renew(@PathVariable Long rentId) {
        try{
            rentService.renewRent(rentId);
            return ResponseEntity.ok().build();
        }catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * admin rent 반납
     */
    @PostMapping("/admin/return/{rentId}")
    public ResponseEntity<String> returnBook(@PathVariable Long rentId) {
        try{
            rentService.returnRent(rentId);
            return ResponseEntity.ok().build();
        }catch (IllegalStateException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     *  도착 통보 시 이메일 전송
     */
    @PostMapping("/{rentId}/arrival")
    public ResponseEntity<String> arrivalEmail(@PathVariable Long rentId){
        try{
            rentService.sendArrivalNotification(rentId);
            return ResponseEntity.ok().build();
        }catch (IllegalArgumentException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
