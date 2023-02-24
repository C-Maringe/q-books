package com.qbook.app.application.rest.web;

import com.qbook.app.application.models.productModels.*;
import com.qbook.app.application.services.appservices.ProductServices;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Log
@RestController
@RequestMapping("/api/auth/products")
@AllArgsConstructor
public class ProductsResource {
    private final ProductServices productServices;

    @GetMapping
    public ResponseEntity<List<ViewProductModel>> getAllProducts(){
        return new ResponseEntity<>(productServices.viewAllProducts(), HttpStatus.OK);
    }
    
    @PutMapping("disable/{id}")
    public ResponseEntity<ProductDisabledModel> disableProduct(@PathVariable("id") String id){
        return new ResponseEntity<>(productServices.disableProduct(id), HttpStatus.OK);
    }

    @PutMapping("enable/{id}")
    public ResponseEntity<ProductEnabledModel> enableProduct(@PathVariable("id") String id){
        return new ResponseEntity<>(productServices.enableProduct(id), HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<ViewProductModel> viewProduct(@PathVariable("id") String id){
        return new ResponseEntity<>(productServices.viewProduct(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ProductCreatedModel> registerProductItem(@RequestBody NewProductModel newServiceItem){
        return new ResponseEntity<>(productServices.createNewProduct(newServiceItem), HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    public ResponseEntity<ProductUpdatedModel> updateProductItem(@PathVariable("id") String id, @RequestBody UpdateProductModel productToUpdate){
        return new ResponseEntity<>(productServices.updateProduct(id, productToUpdate), HttpStatus.OK);
    }
}
