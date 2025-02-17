package org.example.libdev.rent.service;

import lombok.RequiredArgsConstructor;
import org.example.libdev.rent.dto.ResponseAdminRentDto;
import org.example.libdev.rent.entity.Rent;
import org.example.libdev.rent.repository.RentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminRentService {
    private final RentRepository rentRepository;

    @Transactional(readOnly = true)
    public Page<ResponseAdminRentDto> selectAdminRentByUserId(String bookTitle, Pageable pageable){
        Page<Rent> rents = rentRepository.findByBookTitleContaining(bookTitle,pageable);

        return rents.map(ResponseAdminRentDto::toDto);
    }
}
