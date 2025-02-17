package org.example.libdev.rent.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.rent.dto.ResponseAdminRentDto;
import org.example.libdev.rent.service.AdminRentService;
import org.example.libdev.rent.service.RentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.data.domain.Sort;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/admin/rents")
public class AdminRentController {
    private final AdminRentService adminRentService;

    @GetMapping("/managements")
    public String rentManagement(Model model,
                                 @PageableDefault(page = 0, size = 10, sort = "rentDate",direction = Sort.Direction.DESC)
                                 Pageable pageable) {
        Page<ResponseAdminRentDto> rents= adminRentService.selectAdminRentByUserId(pageable);

        if(rents.isEmpty()){
            model.addAttribute("error","대여 내역이 없습니다.");
        }
        int nowPage = rents.getPageable().getPageNumber();
        model.addAttribute("pageSize", rents.getSize());
        model.addAttribute("rents", rents);
        model.addAttribute("nowPage", nowPage);
        model.addAttribute("startPage", Math.max(nowPage - 2, 0));
        model.addAttribute("endPage", Math.min(nowPage + 2, rents.getTotalPages() - 1));
        return "rent/adminRentManagement";
    }
}
