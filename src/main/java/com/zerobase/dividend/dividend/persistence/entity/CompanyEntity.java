package com.zerobase.dividend.dividend.persistence.entity;

import com.zerobase.dividend.dividend.model.Company;
import lombok.*;

import javax.persistence.*;

@Getter
@NoArgsConstructor
@ToString
@Entity(name = "COMPANY")
public class CompanyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String ticker;

    private String name;

    public CompanyEntity(Company company) {
        this.ticker = company.getTicker();
        this.name = company.getName();
    }

}
