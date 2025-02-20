package org.example.libdev.library.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.libdev.library.dto.LibraryRequestDTO;
import org.example.libdev.library.dto.LibraryResponseDTO;
import org.example.libdev.library.entity.Library;
import org.example.libdev.library.entity.RegionCode;
import org.example.libdev.library.repository.LibraryRepository;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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

    @PostConstruct
    public void init() {
        saveLibraries();
    }

    // open api로 도서관 데이터 삽입
    public void saveLibraries() {
        try {
            String url = String.format("http://data4library.kr/api/libSrch?authKey=%s&pageSize=20&format=json", apiKey);

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.error("API 호출 실패: {}", response.getStatusCode());
                throw new RuntimeException("API 호출 실패");
            }

            String body = response.getBody();
            log.debug("응답 본문: {}", body);

            JSONObject jsonResponse = new JSONObject(body);

            if (!jsonResponse.has("response") || !jsonResponse.getJSONObject("response").has("libs")) {
                log.error("응답에서 'libs'을 찾을 수 없습니다.");
                throw new RuntimeException("응답에서 'libs'을 찾을 수 없습니다.");
            }

            JSONArray libs = jsonResponse.getJSONObject("response").getJSONArray("libs");

            for (int i = 0; i < libs.length(); i++) {
                JSONObject lib = libs.getJSONObject(i).getJSONObject("lib");

                Library library = new Library();
                library.setLibraryCode(lib.getString("libCode"));
                library.setLibraryName(lib.getString("libName"));
                library.setLibraryLocation(lib.getString("address"));
                library.setLibraryPhone(lib.getString("tel"));

                libraryRepository.save(library);
            }
        } catch (JSONException e) {
            log.error("JSON 파싱 중 오류 발생: {}",e.getMessage());
            throw new RuntimeException("JSON 파싱 중 오류가 발생했습니다.");
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage());
            throw new RuntimeException("책 정보를 처리하는 중 오류가 발생했습니다.");
        }
    }

    // 전체 도서관 등록
    public List<LibraryResponseDTO> getAllLibraries() {
        return libraryRepository.findAll().stream().map(Library::toResponseDTO).toList();
    }

    // 상세 도서관 조회
    public LibraryResponseDTO getLibraryById(Long libraryId) {
        Library library = libraryRepository.findById(libraryId).orElseThrow();

        return library.toResponseDTO();
    }

    // open api로 도서관 데이터 조회
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
