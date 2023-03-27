package com.zerobase.dividend.dividend.persistence.entity;

import com.zerobase.dividend.dividend.model.Dividend;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@ToString
@Entity(name = "DIVIDEND")
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"companyId", "date"})
})
public class DividendEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long companyId;

    private LocalDateTime date;

    private String dividend;

    public DividendEntity(Long companyId, Dividend dividend) {
        this.companyId = companyId;
        this.date = dividend.getDate();
        this.dividend = dividend.getDividend();
    }
}
