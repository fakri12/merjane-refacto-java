package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ExpirableProductProcessor implements ProductProcessor {

    @Autowired
    private ProductRepository pr;

    @Autowired
    private NotificationService ns;

    @Override
    public boolean supports(Product product) {
        return "EXPIRABLE".equals(product.getType());
    }

    @Override
    public void process(Product product) {
        LocalDate now = LocalDate.now();

        if (product.getAvailable() > 0 && product.getExpiryDate().isAfter(now)) {
            product.setAvailable(product.getAvailable() - 1);
            pr.save(product);
        } else {
            ns.sendExpirationNotification(product.getName(), product.getExpiryDate());
            product.setAvailable(0);
            pr.save(product);
        }
    }
}
