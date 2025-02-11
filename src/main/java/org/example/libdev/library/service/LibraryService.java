package org.example.libdev.library.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.library.dto.LibraryRequestDTO;
import org.example.libdev.library.dto.LibraryResponseDTO;
import org.example.libdev.library.entity.Library;
import org.example.libdev.library.entity.RegionCode;
import org.example.libdev.library.repository.LibraryRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryService {

    @Value("${openapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    private final LibraryRepository libraryRepository;

    // 전체 도서관 등록
    public List<LibraryResponseDTO> getAllLibraries() {
        return libraryRepository.findAll().stream().map(Library::toResponseDTO).toList();
    }

    // 상세 도서관 조회
    public LibraryResponseDTO getLibraryById(Long libraryId) {
        Library library = libraryRepository.findById(libraryId).orElseThrow();

        return library.toResponseDTO();
    }

    // open api로 도서관 정보 조회
    public List<LibraryResponseDTO> getLibrariesByRegion(String region) {
        String regionCode = RegionCode.valueOf(region).getRegionCode();

        String url = String.format("http://data4library.kr/api/libSrch?authKey=%s&region=%s&format=json", apiKey, regionCode);
        log.info("url : {}", url);

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        String body = response.getBody();
        log.info("Library info: {}", body);

        JSONObject jsonResponse = new JSONObject(body);

        JSONObject responseObject = jsonResponse.optJSONObject("response");
        if (responseObject == null) {
            log.error("Response object is null");
            throw new IllegalArgumentException();
        }

        JSONArray librariesArray = responseObject.optJSONArray("libs");
        if (librariesArray == null) {
            log.error("Libraries array is null");
            throw new IllegalArgumentException();
        }

        List<LibraryResponseDTO> libraryList = new ArrayList<>();

        for (int i = 0; i < librariesArray.length(); i++) {
            JSONObject libraryDetails = librariesArray.getJSONObject(i).getJSONObject("lib");

            LibraryResponseDTO libraryResponseDTO = LibraryResponseDTO.builder()
                    .libraryCode(libraryDetails.getString("libCode"))
                    .libraryName(libraryDetails.getString("libName"))
                    .libraryLocation(libraryDetails.getString("address"))
                    .libraryPhone(libraryDetails.getString("tel"))
                    .build();

            libraryList.add(libraryResponseDTO);
        }

        return libraryList;
    }

    // 도서관 등록
    public LibraryResponseDTO createLibrary(LibraryRequestDTO libraryRequestDTO) {
        Library newLibrary = libraryRequestDTO.toEntity();

        return libraryRepository.save(newLibrary).toResponseDTO();
    }

    // 도서관 삭제
    public void deleteLibraryById(Long libraryId) {
        Library existingLibrary = libraryRepository.findById(libraryId).orElseThrow();

        libraryRepository.deleteById(libraryId);
    }
}
