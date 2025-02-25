//package org.example.libdev.rent;
//
//import org.example.libdev.rent.controller.RentController;
//import org.example.libdev.rent.dto.ResponseRentDto;
//import org.example.libdev.rent.entity.RentStatus;
//import org.example.libdev.rent.service.RentService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(controllers = RentController.class)
//public class RentControllerTest {
//
//    @Autowired
//    private MockMvc mvc;
//
//    @MockitoBean
//    private RentService rentService;
//
//    @Test
//    @DisplayName("대여 내역이 있을 때 rent 페이지로 이동하고 데이터를 포함해야 한다.")
//    void selectRentByUser() throws Exception {
//
//        //given
//        List<ResponseRentDto> mockRents = List.of(ResponseRentDto.builder()
//                        .rentId(1L)
//                        .rentDate(LocalDateTime.now())
//                        .book(null)
//                        .renew(0)
//                        .status(RentStatus.RENTED)
//                        .returnDate(LocalDateTime.now().plusWeeks(2))
//                        .build()
//        );
//        Mockito.when(rentService.selectRentByUserId(anyLong())).thenReturn(mockRents);
//
//        //when
//        mvc.perform(get("/api/rent/1"))
//
//                //then
//                .andExpect(model().attributeExists("rents"))
//                .andExpect(model().attribute("rents",mockRents))
//                .andExpect(view().name("rent"));
//    }
//
//    @Test
//    @DisplayName("대여 내역이 없을 때 rent 페이지로 이동하고 message를 포함해야 한다")
//    void selectRentByUser_WithoutData() throws Exception {
//        // given
//        Mockito.when(rentService.selectRentByUserId(anyLong())).thenReturn(Collections.emptyList());
//
//        // when
//        mvc.perform(get("/api/rent/1"))
//
//                // then
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("message"))
//                .andExpect(model().attribute("message", "대여 내역이 없습니다."))
//                .andExpect(view().name("rent"));
//    }
//
//}
