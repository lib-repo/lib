package org.example.libdev.payment.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.availability.repository.AvailabilityRepository;
import org.example.libdev.global.exception.ErrorCode;
import org.example.libdev.global.exception.NotFoundException;
import org.example.libdev.library.repository.LibraryAgreementRepository;
import org.example.libdev.payment.dto.RequestPaymentDto;
import org.example.libdev.payment.entity.PaymentEntity;
import org.example.libdev.payment.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private IamportClient api;

    @Value("${imp.api.key}")
    private String apiKey;

    @Value("${imp.api.secretkey}")
    private String secretKey;

    @PostConstruct
    public void init() {
        this.api = new IamportClient(apiKey, secretKey);
    }



    @Transactional
    public PaymentEntity postPrepare(RequestPaymentDto request) throws IamportResponseException, IOException {
        PrepareData prepareData = new PrepareData(request.getMerchantUid(), BigDecimal.valueOf(request.getAmount()));
        api.postPrepare(prepareData);

        log.info("Preparing payment: merchantUid={}, amount={}", request.getMerchantUid(), request.getAmount());


        PaymentEntity payment = PaymentEntity.builder()
                .impUid(request.getMerchantUid())
                .amount(request.getAmount())
                .pay_create_at(String.valueOf(System.currentTimeMillis()))
                .pay_update_at(String.valueOf(System.currentTimeMillis()))
//                .bank_name()
//                .book_name()
//                .order_email()
                .build();

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment validatePayment(RequestPaymentDto request) throws IamportResponseException, IOException {

        log.info("민주:{}",request.getImpUid());
        PaymentEntity validatePayment = paymentRepository.findPaymentByImpUid(request.getMerchantUid()).orElseThrow(
                () -> new NotFoundException("asdf", ErrorCode.NOT_FOUND)
        );


        BigDecimal preAmount = BigDecimal.valueOf(validatePayment.getAmount());

        IamportResponse<Payment> iamportResponse = api.paymentByImpUid(request.getImpUid());
        BigDecimal paidAmount = iamportResponse.getResponse().getAmount();

        if (!preAmount.equals(paidAmount)) {
            CancelData cancelData = cancelPayment(iamportResponse);
            api.cancelPaymentByImpUid(cancelData);
        }

        return iamportResponse.getResponse();
    }

    public CancelData cancelPayment(IamportResponse<Payment> response) {
        return new CancelData(response.getResponse().getImpUid(), true);
    }
}
