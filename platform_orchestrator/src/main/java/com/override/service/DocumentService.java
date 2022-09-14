package com.override.service;

import com.override.mapper.DocumentMapper;
import com.override.model.Document;
import com.override.repository.DocumentRepository;
import dto.DocumentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private PlatformUserService platformUserService;

    @Autowired
    private DocumentMapper documentMapper;

    public void uploadFile(MultipartFile file, String login) {

        try {
            Document document = new Document();
            document.setContent(file.getBytes());
            document.setType(file.getContentType());
            document.setName(file.getOriginalFilename());
            document.setUser(platformUserService.findPlatformUserByLogin(login));
            documentRepository.save(document);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<DocumentDTO> getAllByUserLogin(String login) {
        List<Document> documents = documentRepository.findAllByUserId(
                platformUserService.findPlatformUserByLogin(login).getId());
        List<DocumentDTO> documentDTOS = new ArrayList<>();
        for (Document document : documents) {
            documentDTOS.add(DocumentDTO.builder()
                    .id(document.getId())
                    .name(document.getName())
                    .type(document.getType())
                    .build());
        }
        return documentDTOS;
    }

    public Document downloadFile(Long fileId) {
        return documentRepository.getById(fileId);
    }

    public void delete(Long fileId) {
        documentRepository.deleteById(fileId);
    }

    public List<DocumentDTO> findAllByUserId(Long id) {
        return (documentRepository.findAllByUserId(id)).stream()
                .map(documentMapper::entityToDto).collect(Collectors.toList());
    }
}
