package com.javaredis.redis.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javaredis.redis.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Repository;
import org.springframework.data.redis.core.RedisCallback;

import java.io.IOException;


import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {
    private ObjectMapper mapper = new ObjectMapper();

    public static final String HASH_KEY="Product";

    private final RedisTemplate<String,String> redisTemplate;

    public ProductRepository(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<Object> findAll(){
        return redisTemplate.opsForHash().values(HASH_KEY).stream().map(a -> {
            try {
                return mapper.readValue(a.toString(), Product.class);
            } catch (JsonProcessingException e) {
                System.out.println("error while parsing ");
                e.printStackTrace();
            }
            return null;
        })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Object findProductById(String id) throws JsonProcessingException {
        System.out.println("called findProductById() from cache ");
        return mapper.readValue(redisTemplate.opsForHash().get(HASH_KEY, id).toString(), Product.class);
    }

    public List<Object> findByCriteria(String criteria) {
        List<Object> productsByCriteria = new ArrayList<>();
        Map<Object, Object> allProducts = redisTemplate.opsForHash().entries(HASH_KEY);

        //to avoid the * exception, we use replace all to catch it,
        // java.util.regex.PatternSyntaxException: Dangling meta character ‘*’ near index 0 s
        String finalCriteria = criteria.replaceAll("\\*", "\\\\w*");
        return allProducts.keySet().stream().map(Object::toString).filter(a -> a.matches(finalCriteria))
                .map(allProducts::get).map(a -> {
                    try {
                        return mapper.readValue(a.toString(), Product.class);
                    } catch (JsonProcessingException e) {
                        System.out.println("error while parsing ");
                        e.printStackTrace();
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    public Product save(Product product) throws JsonProcessingException {
        redisTemplate.opsForHash().put(HASH_KEY,product.getId(),mapper.writeValueAsString(product));
        return product;
    }

    public String deleteProduct(String id){
        redisTemplate.opsForHash().delete(HASH_KEY,id);
        return "Status 204";
    }
}
