//package org.example.libdev.rent.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.example.libdev.rent.dto.ResponseRentDto;
//import org.example.libdev.rent.service.RentService;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//import java.util.List;
//
//@Controller
//@RequiredArgsConstructor
//@RequestMapping("/api/rent")
//public class RentController {
//
//    private final RentService rentService;
//
//    @GetMapping("/{userId}")
//    public String selectRentByUser(@PathVariable Long userId, Model model) {
//        List<ResponseRentDto> rents = rentService.selectRentByUserId(userId);
//
//        if (rents.isEmpty()) {
//            model.addAttribute("message", "대여 내역이 없습니다.");
//        }
//
//        model.addAttribute("rents", rents);
//        model.addAttribute("userId", userId);
//
//        return "rent";
//    }
//}
