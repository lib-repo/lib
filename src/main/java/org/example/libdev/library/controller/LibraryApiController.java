package org.example.libdev.library.controller;

import lombok.RequiredArgsConstructor;
import org.example.libdev.library.dto.LibraryRequestDTO;
import org.example.libdev.library.dto.LibraryResponseDTO;
import org.example.libdev.library.service.LibraryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.libdev.library.entity.Library;
import java.util.List;

@RestController
@RequestMapping("/api/admin/libraries")
@RequiredArgsConstructor
public class LibraryApiController {

    private final LibraryService libraryService;

    @GetMapping("/{libraryId}/partners")
    public ResponseEntity<List<Library>> getPartnerLibraries(@PathVariable Long libraryId) {
        List<Library> partnerLibraries = libraryService.findPartnerLibraries(libraryId);
        return ResponseEntity.ok(partnerLibraries);
    }

    @GetMapping
    public ResponseEntity<List<LibraryResponseDTO>> getAllLibraries() {
        return ResponseEntity.ok(libraryService.getAllLibraries());
    }

    @GetMapping("/{libraryId}")
    public ResponseEntity<LibraryResponseDTO> getLibraryById(@PathVariable Long libraryId) {
        return ResponseEntity.ok(libraryService.getLibraryById(libraryId));
    }

    @GetMapping("/search/{region}")
    public ResponseEntity<List<LibraryResponseDTO>> getLibrariesByRegion(@PathVariable String region) {
        return ResponseEntity.ok(libraryService.getLibrariesByRegion(region));
    }

    @PostMapping
    public ResponseEntity<LibraryResponseDTO> createLibrary(@RequestBody LibraryRequestDTO libraryRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(libraryService.createLibrary(libraryRequestDTO));
    }

    @DeleteMapping("/{libraryId}")
    public ResponseEntity<Void> deleteLibraryById(@PathVariable Long libraryId) {
        libraryService.deleteLibraryById(libraryId);
        return ResponseEntity.noContent().build();
    }
}