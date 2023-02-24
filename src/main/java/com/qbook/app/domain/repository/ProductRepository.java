package com.qbook.app.domain.repository;

import com.qbook.app.domain.models.Product;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<Product, ObjectId> {
    List<Product> findAllByCategoryOrderByCategory(String category);
    List<Product> findAllByActiveOrderByCategory(boolean active);
}
