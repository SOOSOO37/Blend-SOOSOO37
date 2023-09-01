package com.blend.server.Product;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ProductRepository {

    private static Map<Long, Product> db = new HashMap<>();
    private static long sequence = 0L;

    //상품등록
    public Product save(Product product){
        product.setId(++sequence);
        db.put(product.getId(),product);
        return product;
    }
    //id 조회
    public Product findById(long id){
        return db.get(id);
    }

    //전체 조회
    public List<Product> findAll(){
        return new ArrayList<>(db.values());
    }

}
