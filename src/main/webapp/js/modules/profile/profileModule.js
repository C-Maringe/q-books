var profileModule = (function(){
    var miniLoader                      = $(".mini-loader");
    var baseUrl                         = "/api/mobile/bookings";
    var listToSearch                    = [];
    var fromDate                        = $("#fromDate");
    var toDate                          = $("#toDate");
    var filter                          = $("#filter");
    var resultsSection                  = $("#resultsFound");
    var dataTable                       = $('#bookingResults table');

    var editForm                        = $("#updateForm");
    var updateInputName                 = $("#editFirstName");
    var updateInputSurname              = $("#editLastName");
    var userId                          = $("#editClientId");
    var updateInputEmail                = $("#editEmailAddress");
    var updateMobileNumber              = $("#editMobileNumber");
    var updateDateOfBirth               = $("#editDateOfBirth");
    var updateForm                      = $("#updatePermissionForm");
    var receiveMarketingEmailsValue     = $("#receiveEmails");

    // Loyalty Points
    var pointsAvailable                 = $(".pointsAvailable");
    var voucherResultsFound             = $("#voucherResultsFound");

    var onStringToTrue = function(val) {
        if (val === "on") return true; // parse on strings as true boolean
        return val;
    };
    //event binders
    editForm.on('submit',                       __updateClient);
    updateForm.on('submit',                     __receiveMarketingEmails);

    function __loadProfileBookings(reload) {
        var from = moment().subtract(3, 'months').toDate().getTime();
        var to = moment().add(3, 'months').toDate().getTime();
        $.ajax({
            url: "/api/mobile/bookings/filter?startDate="+from+"&endDate="+to+"&status=All",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            listToSearch = response;

            __filterList(reload);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __filterList(reload) {
        var html = '';
        _.each(listToSearch, function(booking) {
            html += '<tr>';
            html += '<td>' + booking.startTime + '</td>';
            html += '<td>' + booking.endTime + '</td>';
            html += '<td>' + booking.employeeFullName + '</td>';
            html += '<td>';
            _.each(booking.treatmentNames, function(treatmentName) {
                html += treatmentName + "</br>";
            });
            html += '</td>';
            html += '<td>' + booking.depositPaid + '</td>'

            if(booking.depositPaid === 'Yes') {
                html += '<td>R ' + booking.depositAmount + '</td>'
            } else {
                html += '<td></td>'
            }

            html += '<td>R ' + booking.totalPrice + '</td>'
            html += '<td><button class="btn btn-danger" onclick=profileModule.cancelBooking("' + booking.bookingId + '")>Cancel</button></td>'
            html += '</tr>';
        });

        resultsSection.empty().append(html);

        if(!reload) {
            dataTable.DataTable({
                ordering:  false
            });
        }
    }

    function __loadProfile() {
        miniLoader.show();
        $.ajax({
            url: "/api/mobile/profile",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            userId.val(response.id);
            updateInputName.val(response.firstName);
            updateInputSurname.val(response.lastName);
            updateInputEmail.val(response.emailAddress);
            updateMobileNumber.val(response.mobileNumber);
            updateDateOfBirth.val(moment(response.dateOfBirth, "DD/MM/YYYY").format("YYYY/MM/DD"));

            __loadProfileLoyaltyPoints(response);
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        });
    }

    function __loadProfileLoyaltyPoints(response) {
        let html = '';
        _.each(response.vouchers,function (voucher) {
            html += '<tr>';
            html += '<td>' + voucher.voucherNumber + '</td>';
            html += '<td>' + voucher.valid + '</td>';
            html += '<td>' + moment(voucher.createdDate).format('YYYY/MM/DD HH:mm') + '</td>';
            html += '<td>' + moment(voucher.expiryDate).format('YYYY/MM/DD') + '</td>';
            html += '<td>' + moment(voucher.redeemedDate).format('YYYY/MM/DD') + '</td>';
            html += '<td>' + voucher.redeemed + '</td>';
            html += '</tr>';
        })

        voucherResultsFound.empty().append(html);
        pointsAvailable.empty().append('Total points available ' + response.loyaltyPoints);
    }

    function __updateClient(e) {

        e.preventDefault();

        var updatedClient = $(this).serializeJSON();

        $.ajax({
            url : "/api/mobile/profile",
            type: 'PUT',
            data: JSON.stringify(updatedClient),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");
        }).fail(function(response){
            __handleAjaxError(response)
        });

    }

    function __searchBookings() {
        var from = fromDate.datepicker('getDate').getTime();
        var to = toDate.datepicker('getDate').getTime();

        if ((from !== "" && from != null) && (to !== "" && to != null)) {
            $.ajax({
                url: "/api/mobile/bookings/filter?startDate="+from+"&endDate="+to+"&status="+filter.val(),
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                listToSearch = response;

                __filterList();
            }).fail(function(response){
                __handleAjaxError(response)
            });
        } else {
            toastr["info"]("Please select a valid date range.", "Info");
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

    function __receiveMarketingEmails(e) {

        e.preventDefault();

        var receiveMarketingEmail = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });

            $.ajax({
                url: "/api/mobile/profile/marketing-email",
                type: 'PUT',
                data: JSON.stringify(receiveMarketingEmail),
                contentType: "application/json;charset=utf-8",
                dataType: "json",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                receiveMarketingEmailsValue.val(response.receiveEmails);

                toastr["success"](response.message, "Success");
            }).fail(function(response){
                __handleAjaxError(response)
            });
    }

    function __loadReceiveMarketingEmailsValue() {
        $.ajax({
            url: "/api/mobile/profile/marketing-email",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            receiveMarketingEmailsValue.each(function(){
                $(this).prop( "checked", response.receiveEmails);
            });

        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __cancelBooking(bookingId) {
        $.ajax({
            url: baseUrl + "/" + bookingId + "/cancel",
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");
            __loadProfileBookings(true);
        }).fail(function(response){
            __handleAjaxError(response)
        });
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

        fromDate.datepicker();
        toDate.datepicker();
        __loadProfile();
        __loadProfileBookings(false);
        __loadReceiveMarketingEmailsValue();
    }

    //public functions
    return{
        initialiseModule                : __initialiseModule,
        searchBookings                  : __searchBookings,
        receiveMarketingEmails          : __receiveMarketingEmails,
        cancelBooking                   : __cancelBooking
    }
})();
