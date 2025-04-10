package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.services.ProductProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductProcessingDelegator {

    private final List<ProductProcessor> processors;

    @Autowired
    public ProductProcessingDelegator(List<ProductProcessor> processors) {
        this.processors = processors;
    }

    public void process(Product product) {
        processors.stream()
                .filter(p -> p.supports(product))
                .findFirst()
                .ifPresent(p -> p.process(product));
    }
}
