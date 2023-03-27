package com.zerobase.dividend.dividend.scraper;

import com.zerobase.dividend.dividend.model.Company;
import com.zerobase.dividend.dividend.model.ScrapedResult;

public interface Scraper {

    Company scrapCompanyByTicker(String ticker);
    ScrapedResult scrap(Company company);

}
