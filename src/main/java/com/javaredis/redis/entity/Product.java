package com.javaredis.redis.entity;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("Product")

public class Product implements Serializable {
    private static final long serialVersionUID = -2792713759608165824L;
    @Id
    private String id;
    private String name;
    private int qty;
    private long price;
}
