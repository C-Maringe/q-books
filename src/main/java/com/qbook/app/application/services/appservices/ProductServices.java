package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.productModels.*;

import java.util.List;

public interface ProductServices {

    ProductCreatedModel createNewProduct(NewProductModel Product);

    ProductUpdatedModel updateProduct(String id, UpdateProductModel updateProductModel);

    ProductDisabledModel disableProduct(String id);

    ProductEnabledModel enableProduct(String id);

    ViewProductModel viewProduct(String id);

    List<ViewProductModel> viewAllProducts();

    List<ViewProductModel> viewAllActiveProducts();

    List<ViewProductModel> viewAllProductsByCategory(String category);
}
