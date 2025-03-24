package org.example.libdev.payment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RequestPaymentDto {
//    @JsonProperty("merchant_uid")
    private String merchantUid;
    private int amount;
    private String bookName;
    private String orderEmail;
    private String orderName;
    @JsonProperty("imp_uid")
    private String impUid;
}
