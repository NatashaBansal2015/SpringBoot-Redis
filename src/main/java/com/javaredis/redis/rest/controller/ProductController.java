package com.javaredis.redis.rest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javaredis.redis.entity.Product;
import com.javaredis.redis.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @author DilankaM
 * @created 15/05/2021 - 22:23
 */

@RestController
@RequestMapping("/product")
@EnableCaching

public class ProductController {
    @Autowired
    private ProductRepository dao;

    @PostMapping
    public Product save(@RequestBody Product product) throws JsonProcessingException {
        return dao.save(product);
    }

    @GetMapping
    public List<Object> getAllProducts() {
        return dao.findAll();
    }

    @GetMapping("/{id}")
    @Cacheable(key="#id", value = "Product", unless = "#result.price > 800")
    public Object findProduct(@PathVariable String id) throws JsonProcessingException {
        return dao.findProductById(id);
    }

    @GetMapping("/fetch/{criteria}")
    public List<Object> KeySearch(@PathVariable String criteria) {
        return dao.findByCriteria(criteria);
    }


    @DeleteMapping("/{id}")
    @CacheEvict(key="#id", value = "Product")
    public String remove(@PathVariable String id) {
        return dao.deleteProduct(id);
    }


}
