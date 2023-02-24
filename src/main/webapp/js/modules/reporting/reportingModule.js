var reportingModule = (function(){
    var miniLoader                      = $(".mini-loader");
    // variables
    var reportingModuleBaseUrl          = "/api/auth/reporting";
    var clientId                        = "";
    //cache elements
    var tabsToggle                      = $('a[data-toggle="tab"]');

    var startDateTime                   = $("#startDateTime");
    var endDateTime                     = $("#endDateTime");
    var searchClientButton              = $("#searchClientButton");
    var clientsSearchedResults          = $("#clientResultLists");

    var startDateTimeTreatment          = $("#startDateTimeTreatment");
    var endDateTimeTreatment            = $("#endDateTimeTreatment");
    var searchTopTreatmentButton        = $("#searchTopTreatmentButton");
    var treatmentResultLists            = $("#treatmentResultLists");

    var searchBookingsFromDate          = $("#fromDate");
    var searchBookingsToDate            = $("#toDate");
    var searchBookingsClientOptions     = $("#clientOption");
    var searchBookingsEmployeeOptions   = $("#employeeOption");
    var searchBookingsStatusOptions     = $("#bookingStatusOption");
    var searchBookingsButton            = $("#searchBookings");
    var searchBookingsResultList        = $("#bookingsResultList");
    var searchBookingsGrandTotalsView   = $(".grandTotals");
    var searchBookingsTotalRevenueMade  = $("#totalRevenueMade");
    var searchBookingsTotalTimeSpent    = $("#totalTimeSpent");

    var searchClientsFromDate           = $("#fromClientDate");
    var searchClientsToDate             = $("#toClientDate");
    var searchClientSignupsButton       = $("#searchClients");
    var signupsGrandTotals              = $(".signupsGrandTotals");
    var totalClientsSignedUp            = $("#totalClientsSignedUp");
    var totalActiveProfiles             = $("#totalActiveProfiles");
    var totalClientsWithBookings        = $("#totalClientsWithBookings");
    var clientResultList                = $("#clientResultList");

    var userInsightFilter               = $("#userInsightFilter");
    var searchUserInsitesButton         = $("#searchUserInsites");
    var clientInsiteResultList          = $("#clientInsiteResultList");

    // modal
    var quickEmailModal                 = $("#quickEmailModal");
    var btnSendEmail                    = $("#btnSendEmail");

    // invoice
    var sendBookingsInvoiceBtn          = $("#sendBookingsInvoice");
    var sendBookingsInvoiceSection      = $(".sendBookingsInvoiceSection");

    // event handlers
    searchClientButton.on('click',  __searchTopClients);
    searchTopTreatmentButton.on('click',  __searchTopTreatments);
    searchBookingsButton.on('click',  __searchBookings);
    searchClientSignupsButton.on('click', __searchClientSignUps);
    searchUserInsitesButton.on('click', __searchUserInsites);
    tabsToggle.on('shown.bs.tab', __handleTabsNavigation);
    btnSendEmail.on('click', __sendQuickEmailContent);
    sendBookingsInvoiceBtn.on('click', __sendInvoiceEmail);

    //private functions

    function __handleTabsNavigation(e) {
        if(e.relatedTarget.id === "search-tab") {
            // searchClientInput.val("");
            clientsSearchedResults.empty();

        } else if(e.relatedTarget.id === "bookings-tab") {
            searchBookingsTotalRevenueMade.empty();
            searchBookingsTotalTimeSpent.empty();
            searchBookingsResultList.empty();
            searchBookingsGrandTotalsView.hide();

        } else if(e.relatedTarget.id === "clients-tab") {

        } else if(e.relatedTarget.id === "insites-tab") {

        }

        if(e.target.id === "insites-tab") {
            // load clients
            __loadClientsForInsites();
        }
    }

    function __searchTopClients() {

        miniLoader.show();
        var start = new moment(startDateTime.val());
        var end = new moment(endDateTime.val());

        $.ajax({
            url : "/api/auth/reporting/topBookedClient/"+start.valueOf()+"/"+end.valueOf(),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
            .done(function (response) {
                var html = "";

                _.each(response, function(reportItem) {
                    html += '<a href="#" class="list-group-item list-group-item-action">';

                        html += '<div class="row">';
                            html += '<div class="col-12">';
                                html +=  reportItem.clientName+ " <span style='font-weight: bold'>("+ reportItem.totalBookings+")</span>";
                            html += '</div>';
                        html += '</div>';

                    html += '</a>';
                });

                clientsSearchedResults.empty().append(html);
            })
            .fail(function(response){
                __handleAjaxError(response)
            }).always(function () {
            miniLoader.hide();
        })
    }

    function __searchTopTreatments() {

        miniLoader.show();
        var start = new moment(startDateTimeTreatment.val());
        var end = new moment(endDateTimeTreatment.val());

        $.ajax({
            url : "/api/auth/reporting/topBookedServiceItem/"+start.valueOf()+"/"+end.valueOf(),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
            .done(function (response) {
                var html = "";

                _.each(response, function(reportItem) {
                    html += '<a href="#" class="list-group-item list-group-item-action">';

                    html += '<div class="row">';
                        html += '<div class="col-12">';
                            html +=  reportItem.treatmentName+ " <span style='font-weight: bold'>("+ reportItem.totalBookings+")</span>";
                        html += '</div>';
                    html += '</div>';

                    html += '</a>';
                });

                treatmentResultLists.empty().append(html);
            })
            .fail(function(response){
                __handleAjaxError(response)
            }).always(function () {
            miniLoader.hide();
        })
    }

    function __searchBookings() {

        if(searchBookingsFromDate.val() === "" && searchBookingsToDate.val() === "") {
            toastr["info"]("Please ensure you provide a valid dates to search with", "Hint");
        } else {
            miniLoader.show();
            $.ajax({
                url : reportingModuleBaseUrl + "/bookings/" + searchBookingsFromDate.val() + "/"+ searchBookingsToDate.val() +"/"+searchBookingsStatusOptions.val()+"/"+searchBookingsClientOptions.val()+"/"+searchBookingsEmployeeOptions.val(),
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            })
            .done(function (response) {

                if(response.reportBookingModels) {
                    searchBookingsTotalRevenueMade.empty().append("<div style='font-size: 12px'>R " + response.totalRevenue + " Excl. VAT <br>" + "R " + response.totalRevenueInclVAT + " Incl. VAT <br></div>");
                    searchBookingsTotalTimeSpent.empty().append((response.totalTimeWorked / 60).toFixed(2) + " (hours)");
                    searchBookingsGrandTotalsView.show();

                    var html = "";

                    _.each(response.reportBookingModels, function(booking) {
                        html += '<a class="list-group-item list-group-item-action">';
                        html += '<div class="row">';
                        html += '<div class="col-4">';
                        html += booking.clientFullName;
                        html += "<br><span style='font-weight: bold;font-size: 12px'>("+ booking.bookingSlot+")</span>";
                        html += '<br><span style="font-style: italic;font-size: 12px"> Client of - ' + booking.employeeFullName + '</span>';
                        html += '</div>';
                        html += '<div class="col-4">';
                        html += '<br>' + booking.treatments;
                        html += '</div>';
                        html += '<div class="col-4">';
                        html += '<br>Duration - ' + booking.totalTimeSpentOnBooking + ' (minutes)';
                        html += '<br>Cost - R ' + booking.totalRevenueForBooking;
                        html += '<br>Total Cost - R ' + booking.totalRevenueForBookingIncludingVAT + '<b> (Incl. VAT)</b>';
                        html += '</div>';
                        html += '</div>';
                        html += '</a>';
                    });

                    searchBookingsResultList.empty().append(html);
                } else {
                    searchBookingsTotalRevenueMade.empty().append("<div style='font-size: 18px'>R " + response.totalRevenue + " Excl. VAT <br>" + "R " + response.totalRevenueInclVAT + " Incl. VAT <br></div>");
                    searchBookingsTotalTimeSpent.empty().append((response.totalTimeWorked / 60).toFixed(2) + " (hours)");
                    searchBookingsGrandTotalsView.show();

                    var html = "";

                    _.each(response.reportExtendedBookingModels, function(booking) {
                        html += '<a class="list-group-item list-group-item-action">';
                        html += '<div class="row">';
                        html += '<div class="col-4">';
                        html += booking.clientFullName;
                        html += "<br><span style='font-weight: bold;font-size: 12px'>("+ booking.bookingSlot+")</span>";
                        html += '<br><span style="font-style: italic;font-size: 12px"> Client of - ' + booking.employeeFullName + '</span>';
                        html += '</div>';
                        html += '<div class="col-4">';
                        html += '<br>' + booking.treatments;
                        html += '</div>';
                        html += '<div class="col-4">';
                        html += '<br>Duration - ' + booking.totalTimeSpentOnBooking + ' (minutes)';
                        html += '<br>Total Excl VAT. - R ' + booking.totalRevenueForBooking;
                        html += '<br>Total Incl VAT. - R ' + booking.totalRevenueForBookingIncludingVAT;
                        if(booking.depositPaid) {
                            html += '<br>Deposit Paid - R ' + booking.depositAmount;
                        }
                        html += '<br>Total Cash - R ' + booking.cashTotal.toFixed(2);
                        html += '<br>Total Card - R ' + booking.cardPaymentTotal.toFixed(2);
                        html += '<br>Total EFT - R ' + booking.eftTotal.toFixed(2);
                        html += '<br>Total VOUCHER - R ' + booking.otherTotal.toFixed(2);
                        html += '</div>';
                        html += '</div>';
                        html += '</a>';
                    });

                    searchBookingsResultList.empty().append(html);
                }

                if(searchBookingsClientOptions.val() !== 'All') {
                    sendBookingsInvoiceSection.show();
                } else {
                    sendBookingsInvoiceSection.hide();
                }
            })
            .fail(function(response){
                __handleAjaxError(response)
            }).always(function () {
                miniLoader.hide();
            })
        }
    }

    function __sendQuickEmail(btn) {
        clientId = btn.dataset.clientid;
        quickEmailModal.modal('show');
    }

    function __sendQuickEmailContent() {

        if($("#quickEmailModal #body").val() === '' || $("#quickEmailModal #subject").val() === '') {
            toastr["info"]("Please ensure you add a subject and body.", "Info.");
        } else {
            miniLoader.show();
            var quickEmail = {};

            quickEmail.clientId = clientId;
            quickEmail.title = $("#quickEmailModal #subject").val();
            quickEmail.message = $("#quickEmailModal #body").val();

            $.ajax({
                url: reportingModuleBaseUrl + "/client/email",
                type: 'PUT',
                data: JSON.stringify(quickEmail),
                contentType: "application/json;charset=utf-8",
                dataType: "json",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                toastr["success"](response.message, "Success");
                quickEmailModal.modal('hide');
            })
            .fail(function (response) {
                __handleAjaxError(response)
            }).always(function () {
                miniLoader.hide();
            })
        }
    }

    function __searchClientSignUps() {
        if(searchClientsFromDate.val() === "" && searchClientsToDate.val() === "") {
            toastr["info"]("Please ensure you provide a valid dates to search with", "Hint");
        } else {
            miniLoader.show();
            $.ajax({
                url : reportingModuleBaseUrl + "/signups/" + searchClientsFromDate.val() + "/" + searchClientsToDate.val(),
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            })
            .done(function (response) {
                totalActiveProfiles.empty().append(response.totalActiveClients);
                totalClientsWithBookings.empty().append(response.totalClientsWithBookings);
                totalClientsSignedUp.empty().append(response.reportClientSignUpModels.length);
                signupsGrandTotals.show();

                var html = "";

                _.each(response.reportClientSignUpModels, function(client) {
                    if(client.active) {
                        html += '<a href="#" class="list-group-item list-group-item-action">';
                    } else {
                        html += '<a href="#" class="list-group-item list-group-item-action list-group-item-danger">';
                    }

                    if(client.totalBookingsMade > 0) {
                        html += '<div class="row text-success">';
                    } else {
                        html += '<div class="row">';
                    }
                            html += '<div class="col-5">';
                                html += client.firstName + ' ' + client.lastName + ' (' + client.contactDetails.emailAddress + ')';
                            html += '</div>';
                            html += '<div class="col-4">';
                                html += 'Registered - ' + moment(client.dateRegistered).format('YYYY-MM-DD');
                            html += '</div>';

                            html += '<div class="col-3">';
                                html += 'Total Bookings - <span style="font-weight: bold">(' + client.totalBookingsMade + ')</span>';
                            html += '</div>';

                        html += '</div>';
                    html += '</a>';
                });

                clientResultList.empty().append(html);
            })
            .fail(function(response){
                __handleAjaxError(response)
            }).always(function () {
                miniLoader.hide();
            })
        }
    }

    function __searchUserInsites() {
        miniLoader.show();

        $.ajax({
            url : reportingModuleBaseUrl + "/getTopServiceItemsPerClient/"+userInsightFilter.val(),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = "";

            _.each(response, function(reportClientInsightsModel) {
                html += '<a href="#" class="list-group-item list-group-item-action">';
                    html += '<div class="row">';
                        html += '<div class="col-6">';
                            html += reportClientInsightsModel.treatmentName;
                        html += '</div>';
                        html += '<div class="col-6">';
                            html += reportClientInsightsModel.totalBooked;
                        html += '</div>';
                    html += '</div>';
                html += '</a>';
            });

            clientInsiteResultList.empty().append(html);
        })
        .fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __sendInvoiceEmail() {
        if(searchBookingsFromDate.val() === ""
            && searchBookingsToDate.val() === ""
            && searchBookingsClientOptions.val() !== "All"
        ) {
            toastr["info"]("Please ensure you provide a valid dates to search with and select only one client.", "Hint");
        } else {
            miniLoader.show();
            var clientInvoice = {};
            clientInvoice.from = searchBookingsFromDate.val();
            clientInvoice.to = searchBookingsToDate.val();
            clientInvoice.clientEmail = searchBookingsClientOptions.val();
            clientInvoice.employeeEmail = searchBookingsEmployeeOptions.val();

            $.ajax({
                url: reportingModuleBaseUrl + "/client/invoice/email",
                type: 'PUT',
                data: JSON.stringify(clientInvoice),
                contentType: "application/json;charset=utf-8",
                dataType: "json",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                toastr["success"](response.message, "Success");
            })
            .fail(function(response){
                __handleAjaxError(response)
            }).always(function () {
                miniLoader.hide();
            })
        }
    }

    function __disableAccount(element){
        miniLoader.show();
        $.ajax({
            url: reportingModuleBaseUrl + "/client/disable/"+element.dataset.clientid,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
        .done(function (response) {
            toastr["success"](response.message, "Success");
            __searchClient();
        })
        .fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __enableAccount(element){
        miniLoader.show();
        $.ajax({
            url: reportingModuleBaseUrl + "/client/enable/"+element.dataset.clientid,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
        .done(function (response) {
            toastr["success"](response.message, "Success");
            __searchClient();
        })
        .fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __loadEmployeesForFilter() {
        $.ajax({
            url : reportingModuleBaseUrl + "/employeesFilter",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = '';
            html += '<option value="All">All Employees</option>';
            _.each(response, function(employee) {
                if(employee.active) {
                    html += '<option value=' + employee.emailAddress + '>' + employee.firstName + ' ' + employee.lastName + ' ('+employee.emailAddress+')'+'</option>';
                } else {
                    html += '<option value=' + employee.emailAddress + ' styling="color:red">' + employee.firstName + ' ' + employee.lastName + ' ('+employee.emailAddress+')'+'</option>';
                }
            });

            searchBookingsEmployeeOptions.empty().append(html);

        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __loadClientsForFilter() {
        $.ajax({
            url : reportingModuleBaseUrl + "/clientsFilter",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = '';
            html += '<option value="All">All Clients</option>';
            _.each(response, function(client) {
                html += '<option value=' + client.emailAddress + '>' + client.firstName + ' ' + client.lastName + ' ('+client.emailAddress+')'+'</option>';
            });

            searchBookingsClientOptions.empty().append(html);

        }).fail(function(response){
            __handleAjaxError(response)
        })
    }

    function __loadClientsForInsites() {
        miniLoader.show();
        $.ajax({
            url : reportingModuleBaseUrl + "/clientsFilter",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = '';
            _.each(response, function(client) {
                html += '<option value=' + client.emailAddress + '>' + client.firstName + ' ' + client.lastName + ' ('+client.emailAddress+')'+'</option>';
            });

            userInsightFilter.empty().append(html);

        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
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
        __loadEmployeesForFilter();
        __loadClientsForFilter();
    }

    //public functions
    return{
        initialiseModule: __initialiseModule,
        disableAccount  : __disableAccount,
        enableAccount   : __enableAccount,
        sendQuickEmail  : __sendQuickEmail
    }
})();
