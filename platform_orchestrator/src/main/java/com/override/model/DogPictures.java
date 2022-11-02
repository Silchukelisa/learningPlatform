package com.override.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class DogPictures {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private String type;

        @Column
        private LocalDateTime date = LocalDateTime.now();

        @JsonIgnore
        @Lob
        @Type(type = "org.hibernate.type.ImageType")
        private byte[] content;

        @JoinColumn(name = "user_id")
        @ManyToOne(fetch = FetchType.EAGER)
        private PlatformUser user;

       public void Display ()
        {
                System.out.println ("Display Function invoked ...");
        }
}
