//package org.example.libdev.rent.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.libdev.rent.service.RentService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/rent")
//public class RentApiController {
//    private final RentService rentService;
//
//    /**
//     * rent 생성
//     */
////    @PostMapping("/{userId}/{bookId}")
////    public ResponseEntity<String> createRent(@PathVariable Long userId, @PathVariable Long bookId) {
////        try{
////            rentService.saveRent(userId, bookId);
////            return ResponseEntity.ok().build();
////        }catch (IllegalStateException e){
////            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
////        }
////    }
//
//    /**
//     * rent 연장
//     */
//    @PostMapping("/renew/{rentId}")
//    public ResponseEntity<String> renew(@PathVariable Long rentId) {
//        try{
//            rentService.renewRent(rentId);
//            return ResponseEntity.ok().build();
//        }catch (IllegalStateException e){
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
//}
