var productsModule = (function(){
    //cache elements
    var registrationSectionNavigator        = $("#registerSection");
    var viewRegisteredNavigator             = $(".viewSection");
    var editProductsNavigator               = $("#editProducts");
    var listAllProductsView                 = $("#listProducts");
    var registerProductsView                = $("#registerProducts");
    var productsList                        = $("#productLists");
    var newProductForm                      = $("#newProductForm");
    var editProductForm                     = $("#editProductForm");
    var productNameInput                    = $("#productName");
    var productDescriptionInput             = $("#productDescription");
    var searchProductInput                  = $("#searchProduct");
    var originalProductListToSearch         = [];
    var productListToSearch                 = [];

    // custom add
    var specialCheckbox                     = $("#special");

    // Update Special Components
    var productIdElementToUpdate            = $("#id");
    var productNameElementToUpdate          = $("#updateProductName");
    var productDescriptionElementToUpdate   = $("#updateProductDescription");
    var productPriceToUpdate                = $("#updatePrice");
    var productSpecialToUpdate              = $("#updateSpecial");
    var productCategoryToUpdate             = $("#updateCategory");
    var productSpecialPriceToUpdate         = $("#updateSpecialPrice");
    var productIsSpecial                    = $(".updateSpecialChecked");
    var newProductIsSpecial                 = $(".specialChecked");
    var productSpecialEndDate               = $("#updateSpecialEndDate");

    var onStringToTrue = function(val) {
        if (val === "on") return true; // parse on strings as true boolean
        return val;
    };

    //event binders
    registrationSectionNavigator.on('click',  __toggleRegisterProductsComponents);
    viewRegisteredNavigator.on('click',       __toggleViewProductsComponents);
    editProductForm.on('submit',              __updateProduct);
    newProductForm.on('submit',               __registerProduct);
    searchProductInput.on('input',            __searchProducts);
    productNameInput.on('blur',               __modifyInput);
    productDescriptionInput.on('blur',        __modifyInput);
    specialCheckbox.on('change',              __displaySpecialEndsOnDate);
    productSpecialToUpdate.on('change',       __displayUpdateSpecialEndsOnDate);

    //event handlers
    function __toggleRegisterProductsComponents() {
        listAllProductsView.hide();
        registerProductsView.show();
        editProductsNavigator.hide();
    }

    function __toggleViewProductsComponents() {
        newProductForm[0].reset();
        listAllProductsView.show();
        registerProductsView.hide();
        editProductsNavigator.hide();
    }

    function  __modifyInput() {
        var textValue = $(this).val();
        $(this).val(textValue.substr(0,1).toUpperCase()+textValue.substr(1).toLowerCase());
    }

    function __searchProducts() {
        var filterBy = $(this).val().toLowerCase();

        if(filterBy.length === 0) {
            productListToSearch = originalProductListToSearch;
        } else {
            productListToSearch = _.filter(originalProductListToSearch, function(product){
                return product.productName.toLowerCase().indexOf(filterBy)  >= 0;
            });
        }

        __filterProductList();
    }

    function __displaySpecialEndsOnDate() {
        if(this.checked) {
            newProductIsSpecial.show();
        } else {
            newProductIsSpecial.hide();
        }
    }

    function __displayUpdateSpecialEndsOnDate() {
        if(this.checked) {
            productIsSpecial.show();
        } else {
            productIsSpecial.hide();
            // reset
            productSpecialEndDate.val('');
            productSpecialPriceToUpdate.val(0);
        }
    }
    //private functions
    function __loadAllProducts() {
        $.ajax({
            url : "/api/auth/products",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (data) {
            if(data.length === 0) {
                toastr["success"]("No products were found.", "Success");
            } else {
                originalProductListToSearch = data;
                productListToSearch = data;

                __filterProductList();
                searchProductInput.val("");
            }
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __filterProductList() {
        var html = "";

        _.each(productListToSearch, function (product) {
            if (product.active)
                html += '<a class="list-group-item list-group-item-action">';
            else
                html += '<a class="list-group-item list-group-item-action list-group-item-danger">';

            html += '<div class="row">';
            html += '<div class="col-12 col-sm-8">';
            html += '<span style="font-weight: bold">' + product.productName + '</span><br><br><span style="font-weight: bold">Price:</span> (R' + product.price + ')';
            html += '</div>';

            html += '<div class="col-12 col-sm-4">';
            if (product.active) {
                html += '<button class="btn btn-primary btn-block" data-productid=' + product.id + ' onclick="productsModule.editProduct(this)">Edit</button>';
                html += '<button class="btn btn-danger btn-block" data-productid=' + product.id + ' onclick="productsModule.disableProduct(this)">Disable</button>';
            } else {
                html += '<button class="btn btn-success btn-block" data-productid=' + product.id + ' onclick="productsModule.enableProduct(this)">Enable</button>';
            }
            html += '</div>';
            html += '</div>';

            html += '</a>';
        });

        productsList.empty().append(html);
    }

    function __disableProduct(btn){
        $.ajax({
            url : "/api/auth/products/disable/"+btn.dataset.productid,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'))
            }
        })
        .done(function(response) {
            if(response.success){
                toastr["success"]("The product was successfully disabled.", "Operation Successful");
                productsList.empty();
                __loadAllProducts();
            }else{
                toastr["error"]("The product was not disabled. Please refresh the page and try again.", "Error");
                __loadAllProducts();
            }
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __enableProduct(btn){
        $.ajax({
            url : "/api/auth/products/enable/"+btn.dataset.productid,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'))
            }
        })
        .done(function(response) {
            if(response.success){
                toastr["success"]("The product was successfully enabled.", "Operation Successful");
                productsList.empty();
                __loadAllProducts();
            }else{
                toastr["error"]("The product was not enabled. Please refresh the page and try again.", "Error");
                __loadAllProducts();
            }
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __editProduct(btn){
        editProductsNavigator.show();
        listAllProductsView.hide();

        $.ajax({
            url : "/api/auth/products/"+btn.dataset.productid,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (data) {
            productIdElementToUpdate.val(btn.dataset.productid);
            productNameElementToUpdate.val(data.productName);
            productDescriptionElementToUpdate.val(data.productDescription);
            productPriceToUpdate.val(data.price);
            productCategoryToUpdate.val(data.category);
            if(data.special === true){
                productSpecialToUpdate.prop('checked', true);
                productSpecialPriceToUpdate.val(data.specialPrice);
                productIsSpecial.show();
                productSpecialEndDate.val(moment(data.specialEndDate).format('YYYY-MM-DD'));
            } else {
                productSpecialToUpdate.prop('checked', false)
                productIsSpecial.hide();
            }w
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __registerProduct(e){
        e.preventDefault();

        var form = document.getElementById('newProductForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            newProductForm.addClass('was-validated');
        } else {
            var newProduct = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });
            $.ajax({
                url         : "/api/auth/products",
                type        : "POST",
                dataType    : "json",
                data        : JSON.stringify(newProduct),
                contentType : "application/json;charset=UTF-8",
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function(){
                toastr["success"]("Your item was successfully registered.", "Success");
                __loadAllProducts();
                __toggleViewProductsComponents();

            }).fail(function(response){
                __handleAjaxError(response)
            });
        }
    }

    function __updateProduct(e) {
        e.preventDefault();

        var form = document.getElementById('editProductForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            editProductForm.addClass('was-validated');
        } else {
            var updatedProduct = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });
            updatedProduct.specialEndDate = moment(updatedProduct.specialEndDate).toDate().getTime();
            $.ajax({
                url : "/api/auth/products/" + productIdElementToUpdate.val(),
                type: 'PUT',
                data: JSON.stringify(updatedProduct),
                contentType: "application/json;charset=utf-8",
                dataType:"json",
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function () {
                toastr["success"]("Your product was successfully updated.", "Success");
                editProductsNavigator.hide();
                listAllProductsView.show();
                __loadAllProducts();
            }).fail(function(response){
                __handleAjaxError(response)
            });
        }
    }

    function __handleAjaxError(response) {
        var parsedResponse = JSON.parse(response.responseText);

        toastr["error"](parsedResponse.detail, parsedResponse.title);

        if(parsedResponse.status === 403) {
            __forceLogout();
        }
    }

    function __forceLogout() {
        setTimeout(function(){
            localStorage.clear();
            window.location.href = "/views/login/";
        }, 1500);
    }

    function __initialiseModule(){
        toastr.options = {
            "closeButton": true,
            "debug": false,
            "newestOnTop": true,
            "progressBar": false,
            "positionClass": "toast-top-right",
            "preventDuplicates": false,
            "onclick": null,
            "showDuration": "300",
            "hideDuration": "1000",
            "timeOut": "5000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };
        __loadAllProducts();
    }

    //public functions
    return{
        initialiseModule      : __initialiseModule,
        editProduct         : __editProduct,
        disableProduct      : __disableProduct,
        enableProduct       : __enableProduct
    }
})();


