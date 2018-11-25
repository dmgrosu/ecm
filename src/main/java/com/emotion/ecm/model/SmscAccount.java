package com.emotion.ecm.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;


@Data
@NoArgsConstructor
@Entity
@Table(name = "TB_SMSC_ACCOUNT")
@SQLDelete(sql = "UPDATE TB_SMSC_ACCOUNT SET deleted=true WHERE id=?")
@Where(clause = "deleted=false")
public class SmscAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "IP_ADDRESS")
    private String ipAddress;

    @Column(name = "PORT")
    private int port;

    @Column(name = "TPS")
    private short tps;

    @Column(name = "DELETED")
    private boolean deleted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID")
    private Account account;

}
