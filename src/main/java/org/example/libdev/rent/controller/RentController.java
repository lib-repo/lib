package org.example.libdev.rent.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.rent.dto.ResponseAdminRentDto;
import org.example.libdev.rent.dto.ResponseHistoryRentDto;
import org.example.libdev.rent.dto.ResponseRentDto;
import org.example.libdev.rent.entity.RentStatus;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/rent")
public class RentController {

    private final RentService rentService;

    @GetMapping("/{userId}")
    public String selectRentByUser(@PathVariable Long userId, @RequestParam(required = false) String status, Model model) {

        if (status == null || status.isEmpty()) {
            status = "ALL";
        }

        List<ResponseRentDto> rents = rentService.selectRentByUserId(userId, status);

        System.out.println(rents.toString());
        if (rents.isEmpty()) {
            model.addAttribute("message", "대여 내역이 없습니다.");
        }

        model.addAttribute("rents", rents);
        model.addAttribute("userId", userId);

        return "rent/selectRentByUser";
    }

    @GetMapping("/history/{userId}")
    public String selectRentHistory(@PathVariable Long userId, Model model) {

        List<ResponseHistoryRentDto> historyRentList = rentService.historyRentByUser(userId);

        if(historyRentList.isEmpty()) {
            model.addAttribute("error", "대여 내역이 없습니다.");
        }

        model.addAttribute("historyRentList", historyRentList);
        model.addAttribute("userId", userId);

        return "rent/selectRentHistory";
    }

    @GetMapping("/admin/managements")
    public String rentManagement(Model model,
                                 @RequestParam(value = "bookTitle", required = false, defaultValue = "") String bookTitle,
                                 @PageableDefault(page = 0, size = 10, sort = "rentDate",direction = Sort.Direction.DESC)
                                 Pageable pageable) {
        Page<ResponseAdminRentDto> rents= rentService.selectAdminRentByUserId(bookTitle,pageable);

        if(rents.isEmpty()){
            model.addAttribute("error","대여 내역이 없습니다.");
        }
        int nowPage = rents.getPageable().getPageNumber();
        model.addAttribute("pageSize", rents.getSize());
        model.addAttribute("rents", rents);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", Math.max(nowPage - 2, 0));
        model.addAttribute("endPage", Math.min(nowPage + 2, rents.getTotalPages() - 1));
        model.addAttribute("bookTitle", bookTitle);
        return "rent/adminRentManagement";
    }
}
