package com.ltfullstack.employeeservice.command.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employees")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    private String id;

    private String firstName;

    private String lastName;

    @Column(name = "kin")
private String Kin;

    private Boolean isDisciplined;

    @Column(name = "member_code")
    private String memberCode;
}
