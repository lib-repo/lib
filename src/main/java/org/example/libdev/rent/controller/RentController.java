package org.example.libdev.rent.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.rent.dto.ResponseRentDto;
import org.example.libdev.rent.service.RentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/rent")
public class RentController {

    private final RentService rentService;

    @GetMapping("/{userId}")
    public String selectRentByUser(@PathVariable Long userId, Model model,
                                   @PageableDefault(page = 0, size = 10, sort = "rentDate", direction = Sort.Direction.DESC)
                                   Pageable pageable) {

        Page<ResponseRentDto> rents = rentService.selectRentByUserId(userId, pageable);

        if (rents.isEmpty()) {
            model.addAttribute("message", "대여 내역이 없습니다.");
        }

        int nowPage = rents.getPageable().getPageNumber();
        model.addAttribute("rents", rents);
        model.addAttribute("userId", userId);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", Math.max(nowPage - 2, 0));
        model.addAttribute("endPage", Math.min(nowPage + 2, rents.getTotalPages() - 1));

        return "rent/selectRentByUser";
    }

}
