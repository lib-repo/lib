package org.example.libdev.order.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.order.dto.RequestOrderDto;
import org.example.libdev.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class OrderController {
    private final OrderService orderService;

    @ResponseBody
    @PostMapping("/payment/prepare")
    public ResponseEntity<Map<String, String>> preparePayment(@RequestBody RequestOrderDto request)
            throws IamportResponseException, IOException {
        log.info("preparePayment:{}", request.getMerchantUid());

        orderService.saveOrder(request);

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }
}
