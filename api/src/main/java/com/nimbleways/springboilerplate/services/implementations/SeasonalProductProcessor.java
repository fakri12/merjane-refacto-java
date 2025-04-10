package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SeasonalProductProcessor implements ProductProcessor {

    @Autowired
    private ProductRepository pr;

    @Autowired
    private NotificationService ns;

    @Autowired
    private ProductService ps;

    @Override
    public boolean supports(Product product) {
        return "SEASONAL".equals(product.getType());
    }

    @Override
    public void process(Product product) {
        LocalDate now = LocalDate.now();

        boolean inSeason = now.isAfter(product.getSeasonStartDate()) && now.isBefore(product.getSeasonEndDate());

        if (inSeason && product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            pr.save(product);
        } else {
            if (now.plusDays(product.getLeadTime()).isAfter(product.getSeasonEndDate())) {
                ns.sendOutOfStockNotification(product.getName());
                product.setAvailable(0);
                pr.save(product);
            } else if (product.getSeasonStartDate().isAfter(now)) {
                ns.sendOutOfStockNotification(product.getName());
                pr.save(product);
            } else {
                ps.notifyDelay(product.getLeadTime(), product);
            }
        }
    }
}
