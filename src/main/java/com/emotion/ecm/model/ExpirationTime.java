package com.emotion.ecm.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_EXPIRATION_TIME")
@SQLDelete(sql = "UPDATE TB_EXPIRATION_TIME SET deleted=true WHERE id=?")
@Where(clause = "deleted=false")
public class ExpirationTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "DELETED")
    private boolean deleted;

    @Column(name = "NAME")
    private String name;

    @Column(name = "VALUE")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;
}
