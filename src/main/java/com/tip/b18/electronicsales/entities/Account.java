package com.tip.b18.electronicsales.entities;

import com.tip.b18.electronicsales.entities.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Account extends BaseEntity {
    @Column(name = "phone_number", unique = true, columnDefinition = "CHAR(10)")
    private String phoneNumber;

    @Column(name = "user_name", unique = true, columnDefinition = "CHAR(30)")
    private String userName;

    @Column(name = "password", columnDefinition = "VARCHAR(60)")
    private String password;

    @Column(name = "email", unique = true, columnDefinition = "VARCHAR(255)")
    private String email;

    @Column(name = "full_name", nullable = false, columnDefinition = "VARCHAR(50)")
    private String fullName;

    private boolean role;

    @Column(name = "address", columnDefinition = "VARCHAR(100)")
    private String address;

    private Boolean gender;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;
}
