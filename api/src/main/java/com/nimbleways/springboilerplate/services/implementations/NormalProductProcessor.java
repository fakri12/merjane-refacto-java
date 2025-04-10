package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NormalProductProcessor implements ProductProcessor {
    @Autowired
    ProductRepository repo;
    @Autowired ProductService service;

    @Override
    public boolean supports(Product product) {
        return "NORMAL".equals(product.getType());
    }

    @Override
    public void process(Product product) {
        if (product.getAvailable() > 0) {
            product.setAvailable(product.getAvailable() - 1);
            repo.save(product);
        } else if (product.getLeadTime() > 0) {
            service.notifyDelay(product.getLeadTime(), product);
        }
    }
}
