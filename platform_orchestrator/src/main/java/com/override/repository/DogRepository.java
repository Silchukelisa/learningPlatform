package com.override.repository;

import com.override.model.Document;
import com.override.model.DogPictures;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DogRepository extends JpaRepository<DogPictures, Long> {
    List<DogPictures> findAllByUserId(Long id);
}
