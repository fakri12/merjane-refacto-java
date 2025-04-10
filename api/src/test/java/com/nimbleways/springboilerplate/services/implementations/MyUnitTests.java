package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.repositories.ProductRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static org.mockito.Mockito.*;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@UnitTest
public class MyUnitTests {

    @Mock
    private NotificationService notificationService;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks 
    private ProductService productService;
    @InjectMocks
    private NormalProductProcessor normalProductProcessor;
    @InjectMocks
    private ExpirableProductProcessor expirableProcessor;

    @Test
    public void test() {
        // GIVEN
        Product product =new Product(null, 15, 0, "NORMAL", "RJ45 Cable", null, null, null);

        Mockito.when(productRepository.save(product)).thenReturn(product);

        // WHEN
        productService.notifyDelay(product.getLeadTime(), product);

        // THEN
        assertEquals(0, product.getAvailable());
        assertEquals(15, product.getLeadTime());
        Mockito.verify(productRepository, Mockito.times(1)).save(product);
        Mockito.verify(notificationService, Mockito.times(1)).sendDelayNotification(product.getLeadTime(), product.getName());
    }

    @Test
    public void shouldDecrementNormalProductIfAvailable() {
        // GIVEN
        Product product = new Product(null, 5, 10, "NORMAL", "Test Product", null, null, null);

        // WHEN
        normalProductProcessor.process(product);

        // THEN
        assertEquals(9, product.getAvailable());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    public void shouldNotifyIfProductExpired() {
        Product expiredProduct = new Product(null, 5, 1, "EXPIRABLE", "Yogurt", LocalDate.now().minusDays(1), null, null);

        expirableProcessor.process(expiredProduct);

        assertEquals(0, expiredProduct.getAvailable());
        verify(notificationService).sendExpirationNotification("Yogurt", expiredProduct.getExpiryDate());
        verify(productRepository).save(expiredProduct);
    }
}