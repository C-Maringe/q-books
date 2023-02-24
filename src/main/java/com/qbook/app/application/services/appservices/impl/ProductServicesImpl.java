package com.qbook.app.application.services.appservices.impl;

import com.qbook.app.application.configuration.exception.ProductRegistrationException;
import com.qbook.app.application.configuration.exception.ProductUpdateException;
import com.qbook.app.application.configuration.exception.ResourceNotFoundException;
import com.qbook.app.application.configuration.properties.ApplicationProperties;
import com.qbook.app.application.models.productModels.*;
import com.qbook.app.application.services.appservices.ProductServices;
import com.qbook.app.domain.models.Product;
import com.qbook.app.domain.models.Treatment;
import com.qbook.app.domain.repository.ProductRepository;
import com.qbook.app.utilities.factory.Factory;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log
@Service
@AllArgsConstructor
public class ProductServicesImpl implements ProductServices {
    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final ApplicationProperties applicationProperties;

    @Override
    public ProductCreatedModel createNewProduct(NewProductModel newProduct) {
        Product product = Factory.buildProduct(newProduct);

        if(product.isSpecial()) {
            if(product.getSpecialEndDate() == 0L) {
                throw new ProductRegistrationException("The product is being registered as a special please ensure you add an end date for the special.");
            }
        }

        productRepository.save(product);

        return ProductCreatedModel.
                builder()
                .message("The product was successfully registered.")
                .id(product.getId().toString())
                .build();
    }

    @Override
    public ProductUpdatedModel updateProduct(String id, UpdateProductModel updateProductModel) {
        Optional<Product> productOptional = productRepository.findById(new ObjectId(id));

        if(!productOptional.isPresent()) {
            throw new ResourceNotFoundException("The product was not found.");
        }

        if(updateProductModel.isSpecial()) {
            if(updateProductModel.getSpecialEndDate() == 0L) {
                throw new ProductUpdateException("The product is being updated as a special please ensure you add an end date for the special.");
            }
        }

        Product product = productOptional.get();

        product.setProductName(updateProductModel.getProductName());
        product.setProductDescription(updateProductModel.getProductDescription());
        product.setSpecial(updateProductModel.isSpecial());
        product.setSpecialEndDate(updateProductModel.getSpecialEndDate());
        product.setSpecialPrice(updateProductModel.getSpecialPrice());
        product.setPrice(updateProductModel.getPrice());
        product.setCategory(updateProductModel.getCategory());

        productRepository.save(product);

        return ProductUpdatedModel.
                builder()
                .message("The product was successfully updated.")
                .id(product.getId().toString())
                .build();
    }

    @Override
    public ProductDisabledModel disableProduct(String id) {
        Optional<Product> productOptional = productRepository.findById(new ObjectId(id));

        if(!productOptional.isPresent()) {
            throw new ResourceNotFoundException("The product was not found.");
        }

        Product product = productOptional.get();

        product.setActive(false);

        productRepository.save(product);

        return ProductDisabledModel.
                builder()
                .message("The product was successfully disabled.")
                .success(true)
                .build();
    }

    @Override
    public ProductEnabledModel enableProduct(String id) {
        Optional<Product> productOptional = productRepository.findById(new ObjectId(id));

        if(!productOptional.isPresent()) {
            throw new ResourceNotFoundException("The product was not found.");
        }

        Product product = productOptional.get();

        product.setActive(true);

        productRepository.save(product);

        return ProductEnabledModel.
                builder()
                .message("The product was successfully enabled.")
                .success(true)
                .build();
    }

    @Override
    public ViewProductModel viewProduct(String id) {
        Optional<Product> productOptional = productRepository.findById(new ObjectId(id));

        if(!productOptional.isPresent()) {
            throw new ResourceNotFoundException("The product was not found.");
        }

        Product product = productOptional.get();

        return modelMapper.map(product, ViewProductModel.class);
    }

    @Override
    public List<ViewProductModel> viewAllProducts() {
        return productRepository
                .findAll()
                .stream()
                .map(product -> modelMapper.map(product, ViewProductModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ViewProductModel> viewAllActiveProducts() {
        return productRepository
                .findAllByActiveOrderByCategory(true)
                .stream()
                .map(product -> {
                    ViewProductModel viewProductModel = modelMapper.map(product, ViewProductModel.class);
                    if(!viewProductModel.isSpecial()) {
                        viewProductModel.setPrice(
                                BigDecimal.valueOf(viewProductModel.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
                        );
                    } else {
                        viewProductModel.setSpecialPrice(
                                BigDecimal.valueOf(viewProductModel.getPrice()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()
                        );
                    }

                    return viewProductModel;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ViewProductModel> viewAllProductsByCategory(String category) {
        return productRepository
                .findAllByCategoryOrderByCategory(category)
                .stream()
                .map(product -> modelMapper.map(product, ViewProductModel.class))
                .collect(Collectors.toList());
    }
}
