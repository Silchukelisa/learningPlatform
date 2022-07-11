package com.override.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreProjectLesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Pattern(regexp = "^((http(s)?://)?(github.com)+([\\w\\-\\.,@?^=%&amp;:/~\\+#]*[\\w\\-\\@?^=%&amp;/~\\+#])?)$")
    private String link;

    @Column(name = "task_identifier")
    private String taskIdentifier;

    @Column
    private boolean approve;

    @Column
    private boolean viewed;

    @Column
    private ArrayList<String> messages;

    @JoinColumn(name = "user_id")
    @ManyToOne
    private PlatformUser user;
}

