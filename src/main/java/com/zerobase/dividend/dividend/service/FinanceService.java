package com.zerobase.dividend.dividend.service;

import com.zerobase.dividend.dividend.exception.impl.NoCompanyException;
import com.zerobase.dividend.dividend.model.Company;
import com.zerobase.dividend.dividend.model.Dividend;
import com.zerobase.dividend.dividend.model.ScrapedResult;
import com.zerobase.dividend.dividend.model.constants.CacheKey;
import com.zerobase.dividend.dividend.persistence.entity.CompanyEntity;
import com.zerobase.dividend.dividend.persistence.entity.DividendEntity;
import com.zerobase.dividend.dividend.persistence.repository.CompanyRepository;
import com.zerobase.dividend.dividend.persistence.repository.DividendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    // 요청이 자주 들어오는가?
    // 자주 변경되는 데이터인가?
    // key : 메서드 파라미터
    // value :
    @Cacheable(key = "#companyName", value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {

        // 1. 회사명을 기준으로 회사 정보를 조회
        CompanyEntity company = companyRepository.findByName(companyName)
                .orElseThrow(NoCompanyException::new);

        // 2. 조호된 회사 ID 로 배당금 정보 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        // 3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream()
                .map(entity -> new Dividend(entity.getDate(), entity.getDividend()))
                .collect(Collectors.toList());

        return new ScrapedResult(new Company(company.getTicker(), company.getName()), dividends);
    }

}
