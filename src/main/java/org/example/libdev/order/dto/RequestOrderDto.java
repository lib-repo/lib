package org.example.libdev.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RequestOrderDto {
    @JsonProperty("merchant_uid")
    private String merchantUid;
    private int amount;
    private String bookName;
    private String orderEmail;
    private String orderName;
    private Long bookId;
    private String userId;
    private Long toLibraryId;
    private Long selectedLibraryId;
}
