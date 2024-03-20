package com.example.web_bookstore_be.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "feedback")
public class Feedbacks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_feedback")
    private int idFeedback;
    @Column(name = "title" , columnDefinition = "NVARCHAR(MAX)")
    private String title;
    @Column(name = "comment" , columnDefinition = "NVARCHAR(MAX)")
    private String comment;
    @Column(name = "dateCreated")
    private Date dateCreated;
    @Column(name = "isReaded")
    private boolean isReaded;

    @ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    @JoinColumn(name = "id_user", nullable = false)
    private User users;

    @Override
    public String toString() {
        return "Feedbacks{" +
                "idFeedback=" + idFeedback +
                ", title='" + title + '\'' +
                ", comment='" + comment + '\'' +
                ", dateCreated=" + dateCreated +
                ", isReaded=" + isReaded +
                '}';
    }
}
