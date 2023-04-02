var salesModule = (function () {
    var cashupId            = "";
    var baseSalesUrl        = "/api/auth/sales";
    var bookingsList        = [];
    var bookingId           = "";
    var miniLoader          = $(".mini-loader");
    var backNavbar          = $("#backNavbar");
    var backToCashupView    = $("#backToCashupView");

    var dateToView          = $("#dateToView");
    var getBookingsBtn      = $("#getBookings");
    var bookingResultsTable = $("#bookingResults");
    var cashupCardHeader    = $(".card-header");
    var completedDailyCashup= $("#completedDailyCashup");
    var continueCashup      = $("#continueCashup");
    var confirmCashupCompleted = $("#confirmCashupCompleted");

    var cashupSection       = $("#cashupSection");
    var salesTreatmentsSection = $("#salesTreatmentsSection");
    var saleDate            = $("#saleDate");
    var timeList            = $("#saleTime");
    var treatmentList       = $("#treatmentLists");
    var employeeList        = $("#employeeList");
    var clientList          = $("#clientList");
    var bookingTime         = $("#bookingTime");
    var startNewSaleBookingProcessButton = $("#startNewSaleBookingProcessButton");
    var newSaleTreatmentsSection         = $("#newSaleTreatmentsSection");
    var newSaleTreatmentLists            = $("#newSaleTreatmentLists");
    var completeBooking                  = $("#completeBooking");
    var captureSaleItemModal             = $("#captureSaleItemModal");
    var btnCompleteNewSaleBooking        = $("#btnCompleteNewSaleBooking");
    var saleItemCaptureForm              = $("#saleItemCaptureForm");
    var bookingIdToCapture;
    var voucherNumber                    = $("#voucherNumber");
    var discounted                       = $("#discounted");
    var discountPercentage               = $("#discountPercentage");
    var totalVoucherPaid                 = $("#totalVoucherPaid");
    // product adding
    var newSaleProductsSection      = $("#newSaleProductsSection");
    var newSaleProductLists         = $("#newSaleProductLists");
    var productList                 = [];
    var btnCompleteNewSaleProduct   = $("#btnCompleteNewSaleProduct");

    getBookingsBtn.on('click',                      __handleGetBookingsForDate);
    continueCashup.on('click',                      __handleCompleteCashup);
    backToCashupView.on('click',                    __toggleViewCashupForBookings);
    completeBooking.on('click',                     __completeBookingChanges);
    startNewSaleBookingProcessButton.on('click',    __startNewSaleBookingProcess);
    btnCompleteNewSaleBooking.on('click',           __completeNewBookingSaleProcess);
    saleItemCaptureForm.on('submit',                __captureSaleItem);
    btnCompleteNewSaleProduct.on('click',           __captureProductSale)
    var onStringToTrue = function(val) {
        if (val === "on") return true; // parse on strings as true boolean
        return val;
    };

    function __toggleViewCashupForBookings() {
        salesTreatmentsSection.hide();
        newSaleTreatmentsSection.hide();
        startNewSaleBookingProcessButton.show();
        cashupSection.show();
        backNavbar.hide();
        newSaleProductsSection.hide();
    }

    function __handleCompleteCashup() {
        confirmCashupCompleted.modal('show');
    }

    function __confirmDayCanBeCashedUp() {

        if(cashupId && cashupId !== "") {
            var salesCashupCompleteModel = {};
            salesCashupCompleteModel.cashUpId = cashupId;

            $.ajax({
                url: baseSalesUrl + "/cash-up/complete",
                type: 'PUT',
                data: JSON.stringify(salesCashupCompleteModel),
                contentType: "application/json;charset=utf-8",
                dataType:"json",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                console.log(JSON.stringify(salesCashupCompleteModel))
                toastr["success"](response.message, "Success");

                confirmCashupCompleted.modal('hide');
                cashupCardHeader.empty().append("<div class='row'><div class='col-3'>CASH UP</div></div>");
                bookingResultsTable.empty();
                completedDailyCashup.hide();
                startNewSaleBookingProcessButton.hide();
                cashupId = "";

            }).fail(function (response) {
                __handleAjaxError(response)
            });
        } else {

        }
    }

    function __captureSaleItem(e) {

        e.preventDefault();

        var form = document.getElementById('saleItemCaptureForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            saleItemCaptureForm.addClass('was-validated');
        } else {
            var saleItemCapture = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });
            saleItemCapture.cashUpId = cashupId;
            saleItemCapture.bookingId = bookingIdToCapture;

            miniLoader.show();

            $.ajax({
                url : baseSalesUrl + "/booking/capture",
                type: 'POST',
                data: JSON.stringify(saleItemCapture),
                contentType: "application/json;charset=utf-8",
                dataType:"json",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                console.log(JSON.stringify(saleItemCapture))
                toastr["success"](response.message, "Success");

                bookingsList = [];

                __handleGetBookingsForDate();

                __toggleViewCashupForBookings();

                bookingResultsTable.empty();

                saleItemCapture.cashUpId = '';
                saleItemCapture.bookingId = '';

                saleItemCaptureForm[0].reset();
            }).fail(function(response){
                __handleAjaxError(response)
            }).always(function(){
                captureSaleItemModal.modal('hide');
                miniLoader.hide();
            });

        }
    }

    function __handleGetBookingsForDate() {

        var dateSelected = new moment(dateToView.val()).format('YY-MM-DD');
        if(dateSelected === "Invalid date") {
            toastr["info"]("Please select a valid date to start the cash up.", "Hint");
            return;
        }

        miniLoader.show();
        $.ajax({
            url: baseSalesUrl + "/cashup-start/" + dateSelected,
            method: "PUT",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(bookings){
            console.log(baseSalesUrl + "/cashup-start/" + dateSelected)
            if(bookings.salesBookingModelList.length > 0) {
                var html = "";
                _.each(bookings.salesBookingModelList, function (booking) {
                    html += '<div class="list-group-item list-group-item-action">';
                        html += '<div class="row">';
                            html += '<div class="col-12 col-sm-4">';
                                html += booking.clientFullName;
                                html += "<br><span style='font-weight: bold; font-size: 12px'>(" + booking.bookingSlot + ")</span>";
                            html += '</div>';

                            html += '<div class="col-12 col-sm-5">';
                                html += '<div class="col-12">';
                                    html += booking.treatments;
                                html += '</div>';
                                html += '<div class="col-12">';
                                    html += "<span style='font-weight: bold; '>Total</span> R " + booking.bookingTotal + " (VAT Incl.)";
                                html += '</div>';
                            html += '</div>';

                        if(!booking.captured) {
                            html += '<div class="col-12 col-sm-3">';
                                if(!booking.productModels || booking.productModels.length === 0) {
                                    html += '<button class="btn btn-info btn-block" data-bookingId=' + booking.bookingId + ' onclick="salesModule.addProduct(this)">ADD PRODUCT</button>';
                                }
                                html += '<button class="btn btn-success btn-block" data-bookingId=' + booking.bookingId + ' onclick="salesModule.captureBooking(this)">CAPTURE</button>';
                                html += '<button class="btn btn-primary btn-block" data-bookingId=' + booking.bookingId + ' onclick="salesModule.editBooking(this)">EDIT</button>';
                                html += '<button class="btn btn-danger btn-block" data-bookingId=' + booking.bookingId + ' onclick="salesModule.cancelBooking(this)">CANCEL</button>';
                            html += '</div>';
                        }
                        html += '</div>'; // End of row

                        if(booking.productModels && booking.productModels.length > 0) {
                            html += '<div class="row" style="margin-top: 50px;">';
                                html += '<div class="col-12"><b>Products Purchased</b></div>';
                                html += '<div class="col-12">';
                                    booking.productModels.forEach(product => {
                                        html += product.quantity + " x " + product.name + " = R " + (product.price * product.quantity).toFixed(2) + "<br>";
                                    });
                                html += '</div>';
                            html += '</div>';
                        }
                    html += '</div>'; // End of list-group-item
                });

                cashupCardHeader.empty().append("<div class='row'><div class='col-3'>CASH UP</div><div class='offset-6 col-3 text-right'>R " + bookings.overviewTotal + "</div></div>");
                bookingResultsTable.empty().append(html);

                if(!bookings.cashupCanBeCompleted) {
                    continueCashup.hide();
                } else {
                    continueCashup.show();
                }

                cashupId = bookings.cashupId;

                miniLoader.hide();
                startNewSaleBookingProcessButton.show();
                completedDailyCashup.show();
            } else {
                toastr["info"]("There are no bookings to cash up for this day. Start adding some to complete the cashup", "Hint");
                miniLoader.hide();
                completedDailyCashup.hide();
                bookingResultsTable.empty();
                startNewSaleBookingProcessButton.show();
            }
        }).fail(function(response){
            miniLoader.hide();
            startNewSaleBookingProcessButton.hide();
            completedDailyCashup.hide();
            bookingResultsTable.empty();

            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        });
    }

    function __captureBooking(btn) {
        bookingIdToCapture = btn.dataset.bookingid;
        // check if booking id has a deposit then load it
        $.ajax({
            url: baseSalesUrl + "/view/" + bookingIdToCapture,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(booking){
            console.log(baseSalesUrl + "/view/" + bookingIdToCapture)
            if(booking.depositPaid) {
                $(".deposit-paid").show();
                $("#depositPaid").val(booking.depositAmount);
            }

            if(booking.voucher) {
                voucherNumber.val(booking.voucher.voucherNumber);
                discounted.prop("checked", true)
                discountPercentage.val(5);
                totalVoucherPaid.hide();
            }

            $(".totalToPay").empty().append("Sale Item - Capture (Total for booking: R " + booking.totalToPay + ")");
            captureSaleItemModal.modal('show');
        }).fail(function(response){
            __handleAjaxError(response)
        });

    }

    function __closeModal() {
        saleItemCaptureForm[0].reset();
        bookingIdToCapture = '';
        captureSaleItemModal.modal('hide');
    }

    function __addProductsToSale(btn) {
        bookingIdToCapture = btn.dataset.bookingid;
        salesTreatmentsSection.hide();
        newSaleTreatmentsSection.hide();
        startNewSaleBookingProcessButton.hide();
        cashupSection.hide();
        backNavbar.show();
        newSaleProductsSection.show();
        __loadAllProducts();
    }

    function __loadAllProducts() {
        $.ajax({
            url : "/api/auth/sales/products",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (data) {
            var html = '';

            _.each(data, function (product) {
                if(!product.quantity) {
                    product.quantity = 0;
                }
                html += '<tr>';
                html += '<td class="secondary-text-color" style="padding: 0; vertical-align: middle">';
                html += '<span style="font-weight: bold; font-size: 16px;">'+product.productName+'</span><br>';
                html += '<span style="font-size: 12px;">R '+product.price+'</span><br>';
                html += '</td>';
                html += '<td style="padding: 0; vertical-align: middle">';
                html += '<div class="row">';
                html += '<div class="col-4 text-center">';
                html += '<i data-productid="'+product.id+'" class="fas fa-arrow-alt-circle-left fa-2x" style="color: dimgrey; cursor: pointer" onclick="salesModule.decreaseProductTotal(this)"></i>';
                html += '</div>';
                html += '<div class="col-4 text-center">';
                html += '<input data-productid="'+product.id+'" type="number" readonly value="'+product.quantity +'" class="productQuantity" style="border: 1px solid dimgrey; width: 100%; border-radius: 5px; text-align: center; width: 100%"/>';
                html += '</div>';
                html += '<div class="col-4 text-center">';
                html += '<i data-productid="'+product.id+'" class="fas fa-arrow-alt-circle-right fa-2x increaseQuantityTotal" style="color: dimgrey; cursor: pointer" onclick="salesModule.increaseProductTotal(this)"></i>';
                html += '</div>';
                html += '</div>';
                html += '</td>';
                html += '</tr>';

                if(product.quantity && product.quantity > 0) {
                    productList.push({
                        id: product.id,
                        quantity: product.quantity
                    });
                }
            });
            newSaleProductLists.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __decreaseProductTotal(target) {
        var productQuantity = $('.productQuantity[data-productid="'+$(target).data("productid")+'"]');
        if(parseInt(productQuantity.val()) > 0) {
            if(parseInt(productQuantity.val()) > 1) {
                productQuantity.val(parseInt(productQuantity.val()) - 1);

                for(var i = 0; i < productList.length; i++) {
                    if(productList[i].id === $(target).data("productid")) {
                        productList[i].quantity = parseInt(productList[i].quantity) - 1;
                    }
                }

            } else {
                // remove from booking list
                productQuantity.val(parseInt(0));

                for(var i = 0; i < productList.length; i++) {
                    if(productList[i].id === $(target).data("productid")) {
                        productList.splice(i, 1);
                    }
                }
            }
        }
    }

    function __increaseProductTotal(target) {
        var productQuantity = $('.productQuantity[data-productid="'+$(target).data("productid")+'"]');

        productQuantity.val(parseInt(productQuantity.val()) + 1);

        var alreadyInList = false;
        for(var i = 0; i < productList.length; i++) {
            if(productList[i].id === $(target).data("productid")) {
                alreadyInList = true;
                productList[i].quantity = parseInt(productList[i].quantity) + 1;
            }
        }

        if(!alreadyInList) {
            productList.push({
                id: $(target).data("productid"),
                quantity: parseInt(productQuantity.val())
            });
        }
    }

    function __captureProductSale() {
        if(!productList || productList.length === 0) {
            toastr["info"]("Please select at least one product to capture for.","Info");
            return;
        }

        $.ajax({
            url : baseSalesUrl + "/product/capture",
            type: 'POST',
            data: JSON.stringify({
                bookingId: bookingIdToCapture,
                products: productList
            }),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function () {
            console.log({
                url : baseSalesUrl + "/product/capture",
                type: 'POST',
                data: JSON.stringify({
                    bookingId: bookingIdToCapture,
                    products: productList
                })})
            bookingIdToCapture = "";

            productList = [];

            salesTreatmentsSection.hide();
            newSaleTreatmentsSection.hide();
            startNewSaleBookingProcessButton.show();
            cashupSection.show();
            backNavbar.hide();
            newSaleProductsSection.hide();

            newSaleProductLists.empty();

            __handleGetBookingsForDate();
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __editBooking(btn) {
        startNewSaleBookingProcessButton.hide();
        miniLoader.show();
        $.ajax({
            url: baseSalesUrl+"/view/"+$(btn).data('bookingid'),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            console.log({
                url: baseSalesUrl+"/view/"+$(btn).data('bookingid'),
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            });
            bookingId = $(btn).data('bookingid');
            saleDate.val(new moment(response.bookingDate).format('YYYY-MM-DD'));

            var timeHtml = "";

            _.each(response.saleTimeSlotModels, function (time) {

                if(time.bookingStartTime) {
                    timeHtml += "<option value="+time.time+" selected>"+time.time+"</option>";
                } else {
                    timeHtml += "<option value="+time.time+">"+time.time+"</option>";
                }
            });

            timeList.empty().append(timeHtml);

            var html = '';

            _.each(response.salesBookingItemModels, function (treatment) {
                html += '<tr>';
                html += '<td class="secondary-text-color" style="padding: 0; vertical-align: middle">';
                html += '<span style="font-weight: bold; font-size: 16px;">'+treatment.treatmentName+'</span><br>';
                html += '<span style="font-size: 12px;">R '+treatment.treatmentPrice+'</span><br>';
                html += '<span style="font-size: 12px;">'+treatment.treatmentDuration+' minutes </span>';
                html += '</td>';
                html += '<td style="padding: 0; vertical-align: middle">';
                html += '<div class="row">';
                html += '<div class="col-4 text-center">';
                html += '<i data-treatmentid="'+treatment.treatmentId+'" class="fas fa-arrow-alt-circle-left fa-2x" style="color: dimgrey; cursor: pointer" onclick="salesModule.decreaseBookingTreatmentTotal(this)"></i>';
                html += '</div>';
                html += '<div class="col-4 text-center">';
                html += '<input data-treatmentid="'+treatment.treatmentId+'" type="number" readonly value="'+treatment.quantity +'" class="treatmentQuantity" style="border: 1px solid dimgrey; width: 100%; border-radius: 5px; text-align: center; width: 100%"/>';
                html += '</div>';
                html += '<div class="col-4 text-center">';
                html += '<i data-treatmentid="'+treatment.treatmentId+'" class="fas fa-arrow-alt-circle-right fa-2x increaseQuantityTotal" style="color: dimgrey; cursor: pointer" onclick="salesModule.increaseBookingTreatmentTotal(this)"></i>';
                html += '</div>';
                html += '</div>';
                html += '</td>';
                html += '</tr>';

                if(treatment.quantity > 0) {
                    bookingsList.push({
                        id: treatment.treatmentId,
                        quantity: treatment.quantity,
                        specialOffer: false
                    });
                }
            });
            miniLoader.hide();
            treatmentList.empty().append(html);
            salesTreatmentsSection.show();
            cashupSection.hide();
            backNavbar.show();
            startNewSaleBookingProcessButton.hide();
            // __handleGetBookingsForDate()
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __decreaseBookingTreatmentTotal(target) {
        var treatmentQuantity = $('.treatmentQuantity[data-treatmentid="'+$(target).data("treatmentid")+'"]');
        if(parseInt(treatmentQuantity.val()) > 0) {
            if(parseInt(treatmentQuantity.val()) > 1) {
                treatmentQuantity.val(parseInt(treatmentQuantity.val()) - 1);

                for(var i = 0; i < bookingsList.length; i++) {
                    if(bookingsList[i].id === $(target).data("treatmentid")) {
                        bookingsList[i].quantity = parseInt(bookingsList[i].quantity) - 1;
                    }
                }

            } else {
                // remove from booking list
                treatmentQuantity.val(parseInt(0));

                for(var i = 0; i < bookingsList.length; i++) {
                    if(bookingsList[i].id === $(target).data("treatmentid")) {
                        bookingsList.splice(i, 1);
                    }
                }
            }
        }
    }

    function __increaseBookingTreatmentTotal(target) {
        var treatmentQuantity = $('.treatmentQuantity[data-treatmentid="'+$(target).data("treatmentid")+'"]');

        treatmentQuantity.val(parseInt(treatmentQuantity.val()) + 1);

        var alreadyInList = false;
        for(var i = 0; i < bookingsList.length; i++) {
            if(bookingsList[i].id === $(target).data("treatmentid")) {
                alreadyInList = true;
                bookingsList[i].quantity = parseInt(bookingsList[i].quantity) + 1;
            }
        }

        if(!alreadyInList) {
            bookingsList.push({
                id: $(target).data("treatmentid"),
                quantity: parseInt(treatmentQuantity.val()),
                specialOffer: false
            });
        }
    }

    function __completeBookingChanges() {
        var editBooking = {};

        if(!bookingsList || bookingsList.length == 0) {
            toastr["info"]("Please select atleast one treatment to book for.","Info");
            return;
        }

        if(saleDate.val()
            && timeList.val()) {

            editBooking.startDateTime = saleDate.val() + ' ' + timeList.val();
            editBooking.saleEditBookingItemModels = bookingsList;
            editBooking.bookingId = bookingId;
        } else {
            toastr["info"]("Please ensure you have selected a booking date and time.","Info");
            return;
        }
        miniLoader.show();
        $.ajax({
            url : baseSalesUrl + "/booking/complete",
            type: 'PUT',
            data: JSON.stringify(editBooking),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");
            bookingsList = [];
            bookingId = "";

            __handleGetBookingsForDate();

            __toggleViewCashupForBookings();

            bookingResultsTable.empty();

            miniLoader.hide();
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __cancelBooking(btn) {
        miniLoader.show();
        $.ajax({
            url: baseSalesUrl+"/"+btn.dataset.bookingid+"/cancel",
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");

            __handleGetBookingsForDate()
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        });
    }

    function __startNewSaleBookingProcess() {
        startNewSaleBookingProcessButton.hide();

        // get times
        __viewTimeList();

        // we need to load client list
        __loadClientList();

        // we need to load employee list
        __getEmployeesToBookWith();

        // we need to load treatments for employee list
        setTimeout(function () {
            __loadTreatmentList();

            newSaleTreatmentsSection.show();
            cashupSection.hide();
            backNavbar.show();
        }, 1000);
    }

    function __getEmployeesToBookWith() {
        $.ajax({
            url: baseSalesUrl + "/employees",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = "";

            _.each(response, function (employee) {
                html += '<option data-employeeId="'+employee.employeeId+'" value="'+employee.employeeTitle+'">' + employee.employeeTitle + '(' + employee.employeeFullName + ')</button>';
            });

            employeeList.empty().append(html);

        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __viewTimeList() {
        $.ajax({
            url: baseSalesUrl + "/times",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html ="";

            _.each(response, function(time){
                html+= "<option value="+time.time+">"+time.time+"</option>";
            });

            bookingTime.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __loadTreatmentList() {
        miniLoader.show();
        $.ajax({
            url: baseSalesUrl + "/treatments",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            console.log({
                url: baseSalesUrl + "/treatments",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            })
            bookingsList = [];
            var html = '';

            _.each(response, function (treatment) {
                html += '<tr>';
                html += '<td class="secondary-text-color" style="padding: 0; vertical-align: middle">';
                html += '<span style="font-weight: bold; font-size: 16px;">'+treatment.treatmentName+'</span><br>';
                html += '<span style="font-size: 12px;">R '+treatment.treatmentPrice+'</span><br>';
                html += '<span style="font-size: 12px;">'+treatment.treatmentDuration+' minutes </span>';
                html += '</td>';
                html += '<td style="padding: 0; vertical-align: middle">';
                html += '<div class="row">';
                html += '<div class="col-4 text-center">';
                html += '<i data-treatmentid="'+treatment.treatmentId+'" class="fas fa-arrow-alt-circle-left fa-2x" style="color: dimgrey; cursor: pointer" onclick="salesModule.decreaseBookingTreatmentTotal(this)"></i>';
                html += '</div>';
                html += '<div class="col-4 text-center">';
                html += '<input data-treatmentid="'+treatment.treatmentId+'" type="number" readonly value="0" class="treatmentQuantity" style="border: 1px solid dimgrey; width: 100%; border-radius: 5px; text-align: center;"/>';
                html += '</div>';
                html += '<div class="col-4 text-center">';
                html += '<i data-treatmentid="'+treatment.treatmentId+'" class="fas fa-arrow-alt-circle-right fa-2x increaseQuantityTotal" style="color: dimgrey; cursor: pointer" onclick="salesModule.increaseBookingTreatmentTotal(this)"></i>';
                html += '</div>';
                html += '</div>';
                html += '</td>';
                html += '</tr>';
            });

            newSaleTreatmentLists.empty().append(html);
        }).fail(function (response) {
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __loadClientList() {

        $.ajax({
            url: baseSalesUrl + "/clients",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = "";

            _.each(response, function (client) {
                html += '<option value="' + client.clientId + '">' + client.clientFullName + '</option>';
            });

            clientList.empty().append(html);
        }).fail(function (response) {
            __handleAjaxError(response)
        });
    }

    function __completeNewBookingSaleProcess() {
        var newBooking = {};

        // if(!bookingsList || bookingsList.length == 0) {
        //     toastr["info"]("Please select atleast one treatment to book for.","Info");
        //     return;
        // }

        var dateSelected = new moment(dateToView.val()).format('YYYY-MM-DD');

        if(dateSelected !== 'Invalid Date'
            && bookingTime.val()
            && employeeList.val()
            && clientList.val()) {

            newBooking.startDateTime = dateSelected + ' ' + bookingTime.val();
            newBooking.employeeId = employeeList.find(':selected').data('employeeid');
            newBooking.clientId = clientList.val();
            newBooking.saleNewBookingItemModels = bookingsList;

        } else {
            toastr["info"]("Please ensure you have selected the correct booking date, time and employee.","Info");
            return;
        }


        $.ajax({
            url : baseSalesUrl + "/book",
            type: 'POST',
            data: JSON.stringify(newBooking),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            console.log({
                url : baseSalesUrl + "/book",
                type: 'POST',
                data: JSON.stringify(newBooking),
                contentType: "application/json;charset=utf-8",
                dataType:"json",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            })
            toastr["success"](response.message, "Success");

            bookingsList = [];

            __handleGetBookingsForDate();

            __toggleViewCashupForBookings();

            bookingResultsTable.empty();
        }).fail(function(response){
            __handleAjaxError(response)
        });
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

    function __initialiseModule () {
        cashupId = '';
        bookingIdToCapture = '';
    }

    return {
        initialiseModule                : __initialiseModule,
        cancelBooking                   : __cancelBooking,
        editBooking                     : __editBooking,
        captureBooking                  : __captureBooking,
        closeModal                      : __closeModal,
        decreaseBookingTreatmentTotal   : __decreaseBookingTreatmentTotal,
        increaseBookingTreatmentTotal   : __increaseBookingTreatmentTotal,
        confirmDayCanBeCashedUp         : __confirmDayCanBeCashedUp,
        addProduct                      : __addProductsToSale,
        decreaseProductTotal            : __decreaseProductTotal,
        increaseProductTotal            : __increaseProductTotal
    }
})();
