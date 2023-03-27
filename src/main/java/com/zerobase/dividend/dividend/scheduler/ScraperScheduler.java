package com.zerobase.dividend.dividend.scheduler;

import com.zerobase.dividend.dividend.model.Company;
import com.zerobase.dividend.dividend.model.ScrapedResult;
import com.zerobase.dividend.dividend.model.constants.CacheKey;
import com.zerobase.dividend.dividend.persistence.entity.CompanyEntity;
import com.zerobase.dividend.dividend.persistence.entity.DividendEntity;
import com.zerobase.dividend.dividend.persistence.repository.CompanyRepository;
import com.zerobase.dividend.dividend.persistence.repository.DividendRepository;
import com.zerobase.dividend.dividend.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScraperScheduler {

    private final DividendRepository dividendRepository;
    private final CompanyRepository companyRepository;
    private final Scraper yahooFinanceScraper;

//    @Scheduled(fixedDelay = 1000)
//    public void test1() throws InterruptedException {
//
//        Thread.sleep(5000);
//        System.out.println(Thread.currentThread().getName() + ": test 1 -> " + LocalDateTime.now());
//
//    }
//
//    @Scheduled(fixedDelay = 1000)
//    public void test2() throws InterruptedException {
//
//        System.out.println(Thread.currentThread().getName() + ": test 2 -> " + LocalDateTime.now());
//
//    }

    // 일정 주기마다 수행
    @CacheEvict(value = CacheKey.KEY_FINANCE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling() {
        log.info("Scraping scheduler is started");
        // 저장된 회사 목록을 조회
        List<CompanyEntity> companies = companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for(var company: companies) {
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(new Company(company.getName(), company.getTicker()));

            // saveAll 사용하면 어떻게 될까?
            // 현재 DividendEntity 에 중복으로 데이터가 들어갈 수 없게 막아놓은 유니크 컬럼이 존재하기 때문에
            // 에러가 발생
            // 따라서 스트림을 돌면서 하나씩 확인

            // 스크래핑한 배당금 정보 중 데이터베이스에 없는 값은 저장
            scrapedResult.getDividends().stream()
                    // Dividend 모델을 DividendEntity 로 mapping
                    .map(e -> new DividendEntity(company.getId(), e))
                    // 엘리먼트를 하나씩 DividendRepository 에 삽입
                    .forEach(e -> {
                        boolean isExist = dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if(!isExist) {
                            dividendRepository.save(e);
                            log.info("insert new dividend -> " + e.toString());
                        }
                    });

            // 연속적으로 스크래핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // InterruptedException 의 경우 적절한 처리를 해주는 것이 좋다.
                Thread.currentThread().interrupt();
            }
        }
    }

}
