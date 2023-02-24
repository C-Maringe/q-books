var scheduleModule = (function(){
    var body                                = $("body");
    // variables
    var miniLoader                          = $(".mini-loader");
    var baseScheduleUrl                     = "/api/auth/schedule";
    var bookingsList                        = [];
    var bookingsTrackingList                = [];
    // Cached Items
    var termsAndAgreementModal              = $(".terms-and-agreement-modal");
    var acceptTermsAndAgreementButton       = $("#acceptTermsAndAgreement");
    var depositThreshHold                   = 999999;
    var depositPercentage                   = 0;

    // Cancellation Elements
    var initiateJoinCancellationQueueButton = $("#initiateJoinCancellationQueue");
    var joinCancellationQueueModal          = $('.join-cancellation-queue-modal');
    var joinCancellationQueueModalCloseButton = $("#joinCancellationQueueClose");
    var selectCancellationEmployeeOption    = $("#selectCancellationEmployee");
    var joinCancellationStartTimeOption     = $("#cancellationStartTime");
    var joinCancellationEndTimeOption       = $("#cancellationEndTime");
    var joinCancellationStartDate           = $(".cancellationDateStart");
    var joinCancellationEndDate             = $(".cancellationDateEnd");
    var joinCancellationQueueButton         = $("#joinCancellationQueue");
    var viewCancellationQueueModal          = $('.view-cancellation-queue-modal');
    var viewCancellationQueueModalCloseButton = $("#viewCancellationQueueClose");
    var viewCancellationQueueModalResults   = $("#viewCancellationQueueResults");

    // Booking Elements
    var employeesScheduleToViewResultList   = $("#employeesScheduleToViewResultList");
    var employeesScheduleToView             = $("#employeeSchedule");
    var employeesScheduleToViewModal        = $("#employeesScheduleToViewModal");
    var bookingToViewOrCancelInput          = $("#bookingToViewOrCancel");
    var scheduleDayClickContextOption       = $("#scheduleDayClickContextOption");
    var scheduleSpecificViewOptionModal     = $("#scheduleSpecificViewOption");
    var bookingSpecificView                 = $("#bookingSpecificView");
    var startBookingProcessButton           = $("#startBookingProcessButton");
    var startBookingDateTimeSelectionModal  = $("#startBookingDateTimeSelectionModal");
    var bookingDateInput                    = $("#bookingDate");
    var bookingTimeInput                    = $("#bookingTime");
    var clientListSelect                    = $("#clientList");
    var clientListView                      = $("#clientListView");
    var bookingTreatmentsSection            = $("#bookingTreatmentsSection");
    var treatmentLists                      = $("#treatmentLists");
    var emptyTimeListForEmployeeText        = $("#emptyTimeListForEmployeeText");
    var bookingsOverviewView                = $("#bookingsOverviewView");
    var treatmentsSelectedForBooking        = $("#treatmentsSelectedForBooking");
    var completeBookingButton               = $("#completeBooking");
    var completeBookingDepositButton        = $("#completeBookingDeposit");
    var refreshTreatmentListButton          = $("#refreshTreatmentListButton");
    var bookingDepositCaptureView           = $("#bookingDepositCaptureView");
    var bookingConfirmationModal            = $(".booking-confirmation-modal");
    var confirmationMessage                 = $(".confirmation-message");
    var bookingConfirmationModalClose       = $(".bookingConfirmationModalClose");

    // Block out time
    var initiateBlockoutDayButton           = $("#initiateBlockoutDay");
    var blockoutSpecificViewOption          = $("#blockoutSpecificViewOption");
    var blockoutTimeDate                    = $("#blockoutTimeDate");
    var blockoutTimeStart                   = $("#blockoutTimeStart");
    var blockoutTimeEnd                     = $("#blockoutTimeEnd");
    var blockoutTimeTitleInput              = $("#blockoutTimeTitle");
    var blockoutTimeEmployeesSelect         = $("#blockoutTimeEmployees");
    var confirmBlockoutProcessCompletedButton = $("#confirmBlockoutProcessCompletedButton");

    //event binders
    initiateJoinCancellationQueueButton.on('click', __viewModalToJoinCancellationQueue);
    joinCancellationQueueButton.on('click', __joinCancellationQueue);
    joinCancellationQueueModalCloseButton.on('click', __closeJoinCancellationModal);
    viewCancellationQueueModalCloseButton.on('click', __closeViewCancellationModal);
    bookingConfirmationModalClose.on('click', __closeBookingConfirmationModal);
    startBookingProcessButton.on('click', __startBookingProcess);
    bookingDateInput.on('blur', __loadTimeListForDate);
    completeBookingButton.on('click', __completeBookingProcess);
    completeBookingDepositButton.on('click', __completeBookingPendingProcess);
    initiateBlockoutDayButton.on('click', __startBlockoutProcess);
    confirmBlockoutProcessCompletedButton.on('click', __completeBlockoutTimeProcess);
    acceptTermsAndAgreementButton.on('click', __clientAcceptTerms);
    refreshTreatmentListButton.on('click', __loadTreatmentList);
    blockoutTimeDate.on('blur', __getTimesForBlockingOut);
    body.ready(__checkPaymentHandled);

    //event handlers
    function __checkPaymentHandled() {
        var sPageURL = window.location.search.substring(1),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;


        var transactionId = '';
        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === 'id') {
                transactionId =  sParameterName[1] === undefined ? '' : decodeURIComponent(sParameterName[1]);
            }
        }

        if(transactionId !== '') {
            $.ajax({
                url : "/api/schedule/payments/status/" + transactionId + "/" + localStorage.getItem('bookingId'),
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {

                if(response.transactionApproved) {
                    toastr["success"]("The booking was successfully processed.","Result");
                    bookingTreatmentsSection.hide();
                    bookingDepositCaptureView.hide();
                    bookingsList, bookingsTrackingList = [];
                    employeesScheduleToView.fullCalendar('refetchEvents');
                    bookingsOverviewView.hide();
                    bookingTreatmentsSection.hide();

                    employeesScheduleToView.show();
                    initiateJoinCancellationQueueButton.show();
                    startBookingProcessButton.show();
                } else {
                    toastr["error"]("The booking was not completed. " + response.result.description,"Result");
                    bookingTreatmentsSection.hide();
                    bookingDepositCaptureView.hide();
                    bookingsList, bookingsTrackingList = [];
                    employeesScheduleToView.fullCalendar('refetchEvents');
                    bookingsOverviewView.hide();
                    bookingTreatmentsSection.hide();

                    employeesScheduleToView.show();
                    initiateJoinCancellationQueueButton.show();
                    startBookingProcessButton.show();
                }

                setTimeout(function() {
                    window.location = '/views/layout/index.html';
                }, 3500);

            }).fail(function(response){
                __handleAjaxError(response)
            });
        }
    }

    function __viewModalToJoinCancellationQueue() {
        if(localStorage.getItem('role') !== 'client') {
            $.ajax({
                url: baseScheduleUrl + "/booking-cancellation",
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                var html = "";

                _.each(response, function(queueMember){
                    html+= "<tr>";
                    html+= "<td>"+queueMember.queuePosition+"</td>";
                    html+= "<td>"+queueMember.startDateTime+"</td>";
                    html+= "<td>"+queueMember.endDateTime+"</td>";
                    html+= "<td>"+queueMember.clientFullName+"</td>";
                    html+= "<td>"+queueMember.clientEmail+"</td>";
                    html+= "<tr>";
                });

                viewCancellationQueueModalResults.empty().append(html);

                viewCancellationQueueModal.modal('show');

            }).fail(function(response){
                __handleAjaxError(response)
            });
        } else {
            var calendar = employeesScheduleToView.fullCalendar('getCalendar');
            var view = calendar.view;

            __loadEmployeesForCancellationQueue();

            $.ajax({
                url: baseScheduleUrl + "/booking-cancellation/times/" + moment(view.start._i).format('YYYY-MM-DD'),
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                var html ="";

                _.each(response, function(time){
                    html+= "<option value="+time.time+">"+time.time+"</option>";
                });

                joinCancellationStartTimeOption.empty().append(html);
                joinCancellationEndTimeOption.empty().append(html);

                joinCancellationQueueModal.modal('show');

            }).fail(function(response){
                __handleAjaxError(response)
            });
        }
    }

    function __loadEmployeesForCancellationQueue() {
        $.ajax({
            url: baseScheduleUrl + "/employees",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = "";

            _.each(response, function (employee) {

                html+= "<option value="+employee.employeeId+">"+employee.employeeFullName+"</option>";
            });

            selectCancellationEmployeeOption.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __joinCancellationQueue() {
        var startDate = new Date(joinCancellationStartDate.val()).getTime();
        var endDate = new Date(joinCancellationEndDate.val()).getTime();

        if(isNaN(startDate) || isNaN(endDate) ) {
            toastr["info"]("Please select valid start and end dates.", "Hint!");
            return;
        }
        var cancellationJoining = {
            startDate   : startDate,
            endDate     : endDate,
            startTime   : joinCancellationStartTimeOption.val(),
            endTime     : joinCancellationEndTimeOption.val(),
            clientId    : "",
            employeeId  : selectCancellationEmployeeOption.val()
        };

        $.ajax({
            url         : baseScheduleUrl + '/booking-cancellation',
            type        : 'POST',
            dataType    : 'json',
            data        : JSON.stringify(cancellationJoining),
            contentType : 'application/json; charset=UTF-8',
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response){
            joinCancellationQueueModal.modal('hide');
            toastr["success"](response.message, "Success");
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __closeJoinCancellationModal() {
        joinCancellationQueueModal.modal('hide');
    }

    function __closeViewCancellationModal() {
        viewCancellationQueueModal.modal('hide');
    }

    function __closeBookingConfirmationModal() {
        bookingConfirmationModal.hide();
    }

    function __startBookingProcess() {
        if(!_.isUndefined(localStorage.getItem('employeeId'))
            && (!_.isUndefined(localStorage.getItem('employeeType')))
            && (!_.isNull(localStorage.getItem('employeeId')))
            && (!_.isNull(localStorage.getItem('employeeType')))
            && (!_.isEmpty(localStorage.getItem('employeeId')))
            && (!_.isEmpty(localStorage.getItem('employeeType')))
        ) {
            startBookingProcessButton.hide();
            initiateBlockoutDayButton.hide();

            // limit date selector to 2 months
            var twoMonths = new Date();
            var dd = twoMonths.getDate();
            var mm = twoMonths.getMonth()+1+2; //January is 0!
            var yyyy = twoMonths.getFullYear();
            if(dd<10){
                dd='0'+dd
            }
            if(mm<10){
                mm='0'+mm
            }

            twoMonths = yyyy+'-'+mm+'-'+dd;
            bookingDateInput.attr("max", twoMonths);

            __loadTreatmentList();

            if (localStorage.getItem('role') === 'client') {
                // client is booking
                clientListView.hide();
                // check if dates preselected and load
                if(localStorage.getItem('startDate') && localStorage.getItem('startDate') !== ''
                    && localStorage.getItem('startTime') && localStorage.getItem('startTime') !== ''
                ) {
                    bookingDateInput.val(moment(localStorage.getItem('startDate'), 'YY-MM-DD').format('YYYY-MM-DD'));
                    let html = '<option value="' + localStorage.getItem('startTime') + '" selected>' + localStorage.getItem('startTime') + '</option>';
                    bookingTimeInput.empty().append(html);
                    localStorage.removeItem('startDate');
                    localStorage.removeItem('startTime');
                }
            } else {
                // employee is booking
                __loadClientList();
            }

            // show treatments view
            __displayTreatmentsForBookingProcess();
        } else {
            toastr["info"]("Please ensure you have selected an employee to book with on the previous screen.","Info");
        }
    }

    function __loadTimeListForDate() {
        if(localStorage.getItem('employeeId') && localStorage.getItem('employeeId') !== "") {

            if(bookingDateInput.val() !== "") {
                // reload treatments incase specials are not valid anymore
                __loadTreatmentListForDateAndEmployeeType();

                var formattedDate = moment(bookingDateInput.val()).format('YY-MM-DD');

                bookingTimeInput.hide();
                emptyTimeListForEmployeeText.empty().append("<br>Loading times...").show();

                $.ajax({
                    url: baseScheduleUrl + "/times/" + localStorage.getItem('employeeId') + "/" + formattedDate,
                    beforeSend: function (xhr) {
                        xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                    }
                }).done(function (response) {
                    var html = "";

                    if (response.length >= 1) {
                        emptyTimeListForEmployeeText.hide();

                        _.each(response, function (time) {
                            if (time.available) {
                                html += '<option value="' + time.time + '">' + time.time + '</option>';
                            }
                        });

                        bookingTimeInput.empty().append(html);
                        bookingTimeInput.show();
                    } else {
                        html += "The selected employee has no time available for the date selected. Please select a new date.";

                        emptyTimeListForEmployeeText.empty().append(html);
                        emptyTimeListForEmployeeText.show();
                    }
                }).fail(function (response) {
                    __handleAjaxError(response)
                });
            } else {
                toastr["info"]("To continue booking please click on the date field and select a date.","Info");
            }
        } else {
            toastr["info"]("To continue booking please click on the select schedule button in the top right corner and select the employee you will be visiting.","Info");
        }
    }

    function __loadClientList() {

        $.ajax({
            url: baseScheduleUrl + "/clients",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = "";

            _.each(response, function (client) {
                html += '<option value="' + client.clientId + '">' + client.clientFullName + '</option>';
            });


            clientListSelect.empty().append(html);

            clientListView.show();
            startBookingDateTimeSelectionModal.modal('show');


        }).fail(function (response) {
            __handleAjaxError(response)
        });
    }

    function __loadTreatmentList() {
        miniLoader.show();
        if(typeof(localStorage.getItem('employeeType')) == "undefined") {
            toastr["info"]("Please ensure you have selected an employee to book with on the previous screen.","Info");
            miniLoader.hide();
            return;
        }

        $.ajax({
            url: baseScheduleUrl + "/treatments/"+localStorage.getItem('employeeType'),
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = '';

            _.each(response, function (treatment) {
                if (treatment.special === true) {
                    html += '<tr class="bg-success" style="color: white">';
                } else {
                    html += '<tr>';
                }
                html += '<td class="secondary-text-color" style="padding: 0; vertical-align: middle">';
                if (treatment.special === true) {
                    html += '<span style="font-weight: bold; font-size: 16px; color: white">' + treatment.treatmentName + '</span><br>';
                    html += '<span style="font-size: 12px; color: white">R ' + treatment.specialPrice + '</span><br>';
                    html += '<span style="font-size: 12px; color: white">' + treatment.treatmentDuration + ' minutes </span>';
                } else {
                    html += '<span style="font-weight: bold; font-size: 16px;">' + treatment.treatmentName + '</span><br>';
                    html += '<span style="font-size: 12px;">R ' + treatment.treatmentPrice + '</span><br>';
                    html += '<span style="font-size: 12px;">' + treatment.treatmentDuration + ' minutes </span>';
                }

                html += '</td>';
                html += '<td style="padding: 0; vertical-align: middle">';
                html += '<div class="row">';
                if (treatment.special === true) {
                    html += '<div class="col-4 text-center">';
                    html += '<i data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" class="fas fa-arrow-alt-circle-left fa-2x" style="color: white; cursor: pointer" onclick="scheduleModule.decreaseBookingTreatmentTotal(this)"></i>';
                    html += '</div>';
                    html += '<div class="col-4 text-center">';
                    html += '<input data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" type="number" readonly value="0" class="treatmentQuantity" style="border: 1px solid dimgrey; width: 100%; border-radius: 5px; text-align: center; width: 100%"/>';
                    html += '</div>';
                    html += '<div class="col-4 text-center">';
                    html += '<i data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" class="fas fa-arrow-alt-circle-right fa-2x increaseQuantityTotal" style="color: white; cursor: pointer" onclick="scheduleModule.increaseBookingTreatmentTotal(this)"></i>';
                    html += '</div>';
                } else {
                    html += '<div class="col-4 text-center">';
                    html += '<i data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" class="fas fa-arrow-alt-circle-left fa-2x" style="color: dimgrey; cursor: pointer" onclick="scheduleModule.decreaseBookingTreatmentTotal(this)"></i>';
                    html += '</div>';
                    html += '<div class="col-4 text-center">';
                    html += '<input data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" type="number" readonly value="0" class="treatmentQuantity" style="border: 1px solid dimgrey; width: 100%; border-radius: 5px; text-align: center; width: 100%"/>';
                    html += '</div>';
                    html += '<div class="col-4 text-center">';
                    html += '<i data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" class="fas fa-arrow-alt-circle-right fa-2x increaseQuantityTotal" style="color: dimgrey; cursor: pointer" onclick="scheduleModule.increaseBookingTreatmentTotal(this)"></i>';
                    html += '</div>';
                }
                html += '</div>';
                html += '</td>';
                html += '</tr>';
            });

            treatmentLists.empty().append(html);

            // show treatments view
            __displayTreatmentsForBookingProcess();
        }).fail(function (response) {
            __handleAjaxError(response)
        })
        .always(function(){
            miniLoader.hide();
        })
    }

    function __loadTreatmentListForDateAndEmployeeType() {

        var startDate = moment(bookingDateInput.val()).format('YY-MM-DD');
        if(typeof(localStorage.getItem('employeeType')) == "undefined") {
            toastr["info"]("Please ensure you have selected an employee to book with on the previous screen.","Info");
            miniLoader.hide();
            return;
        }
        $.ajax({
            url: baseScheduleUrl + "/treatments/"+localStorage.getItem('employeeType')+"/"+startDate,
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = '';

            _.each(response, function (treatment) {
                if (treatment.special === true) {
                    html += '<tr class="bg-success" style="color: white">';
                } else {
                    html += '<tr>';
                }
                html += '<td class="secondary-text-color" style="padding: 0; vertical-align: middle">';
                if (treatment.special === true) {
                    html += '<span style="font-weight: bold; font-size: 16px; color: white">' + treatment.treatmentName + '</span><br>';
                    html += '<span style="font-size: 12px; color: white">R ' + treatment.specialPrice + '</span><br>';
                    html += '<span style="font-size: 12px; color: white">' + treatment.treatmentDuration + ' minutes </span>';
                } else {
                    html += '<span style="font-weight: bold; font-size: 16px;">' + treatment.treatmentName + '</span><br>';
                    html += '<span style="font-size: 12px;">R ' + treatment.treatmentPrice + '</span><br>';
                    html += '<span style="font-size: 12px;">' + treatment.treatmentDuration + ' minutes </span>';
                }

                html += '</td>';
                html += '<td style="padding: 0; vertical-align: middle">';
                html += '<div class="row">';
                if (treatment.special === true) {
                    html += '<div class="col-4 text-center">';
                    html += '<i data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" class="fas fa-arrow-alt-circle-left fa-2x" style="color: white; cursor: pointer" onclick="scheduleModule.decreaseBookingTreatmentTotal(this)"></i>';
                    html += '</div>';
                    html += '<div class="col-4 text-center">';
                    html += '<input data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" type="number" readonly value="0" class="treatmentQuantity" style="border: 1px solid dimgrey; width: 100%; border-radius: 5px; text-align: center; width: 100%"/>';
                    html += '</div>';
                    html += '<div class="col-4 text-center">';
                    html += '<i data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" class="fas fa-arrow-alt-circle-right fa-2x increaseQuantityTotal" style="color: white; cursor: pointer" onclick="scheduleModule.increaseBookingTreatmentTotal(this)"></i>';
                    html += '</div>';
                } else {
                    html += '<div class="col-4 text-center">';
                    html += '<i data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" class="fas fa-arrow-alt-circle-left fa-2x" style="color: dimgrey; cursor: pointer" onclick="scheduleModule.decreaseBookingTreatmentTotal(this)"></i>';
                    html += '</div>';
                    html += '<div class="col-4 text-center">';
                    html += '<input data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" type="number" readonly value="0" class="treatmentQuantity" style="border: 1px solid dimgrey; width: 100%; border-radius: 5px; text-align: center; width: 100%"/>';
                    html += '</div>';
                    html += '<div class="col-4 text-center">';
                    html += '<i data-treatmentid="'+treatment.treatmentId+'" data-price="'+treatment.treatmentPrice+'" class="fas fa-arrow-alt-circle-right fa-2x increaseQuantityTotal" style="color: dimgrey; cursor: pointer" onclick="scheduleModule.increaseBookingTreatmentTotal(this)"></i>';
                    html += '</div>';
                }
                html += '</div>';
                html += '</td>';
                html += '</tr>';
            });

            treatmentLists.empty().append(html);
        }).fail(function (response) {
            __handleAjaxError(response)
        });
    }

    function __displayTreatmentsForBookingProcess() {
        bookingTreatmentsSection.show();
        employeesScheduleToView.hide();
        initiateJoinCancellationQueueButton.hide();
    }

    function __decreaseBookingTreatmentTotal(target) {
        var treatmentQuantity = $('.treatmentQuantity[data-treatmentid="'+$(target).data("treatmentid")+'"]');
        if(parseInt(treatmentQuantity.val()) > 0) {
            if(parseInt(treatmentQuantity.val()) > 1) {
                treatmentQuantity.val(parseInt(treatmentQuantity.val()) - 1);

                for(var i = 0; i < bookingsList.length; i++) {
                    if(bookingsList[i].id === $(target).data("treatmentid")) {
                        bookingsList[i].quantity = parseInt(treatmentQuantity.val());
                        bookingsTrackingList[i].quantity = parseInt(treatmentQuantity.val());
                    }
                }

            } else {
                treatmentQuantity.val(0);

                for(var i = 0; i < bookingsList.length; i++) {
                    if(bookingsList[i].id === $(target).data("treatmentid")) {
                        bookingsList.splice(i, 1);
                        bookingsTrackingList.splice(i, 1);
                    }
                }
            }
        }

        if(bookingsList.length === 0) {
            bookingsOverviewView.hide();
        } else {
            var bookingTotalValue = 0;
            for(var i = 0; i < bookingsTrackingList.length; i++) {
                bookingTotalValue += bookingsTrackingList[i].quantity * bookingsTrackingList[i].price;
            }

            var html = "";
            if(bookingTotalValue < depositThreshHold) {
                completeBookingButton.show();
                completeBookingDepositButton.hide();
                bookingsOverviewView.show();
                html += "You have selected " + bookingsList.length + " Treatment/s";
            } else {
                // update the treatment selected list
                completeBookingButton.hide();
                completeBookingDepositButton.show();
                bookingsOverviewView.show();
                html += "Your treatment total of R" + bookingTotalValue.toFixed(2) + " requires a "+depositPercentage+"% deposit to be paid. Click proceed to make payment ";
            }
            treatmentsSelectedForBooking.empty().append(html);
        }
    }

    function __increaseBookingTreatmentTotal(target) {
        var treatmentQuantity = $('.treatmentQuantity[data-treatmentid="'+$(target).data("treatmentid")+'"]');

        treatmentQuantity.val(parseInt(treatmentQuantity.val()) + 1);

        var alreadyInList = false;
        for(var i = 0; i < bookingsList.length; i++) {
            if(bookingsList[i].id === $(target).data("treatmentid")) {
                alreadyInList = true;
                bookingsList[i].quantity = parseInt(treatmentQuantity.val());
                bookingsTrackingList[i].quantity = parseInt(treatmentQuantity.val());
            }
        }

        if(!alreadyInList) {
            bookingsList.push({
                id: $(target).data("treatmentid"),
                quantity: parseInt(treatmentQuantity.val()),
                specialOffer: false
            });

            bookingsTrackingList.push({
                id: $(target).data("treatmentid"),
                quantity: parseInt(treatmentQuantity.val()),
                price: $(target).data("price")
            });
        }

        var bookingTotalValue = 0;
        for(var i = 0; i < bookingsTrackingList.length; i++) {
            bookingTotalValue += bookingsTrackingList[i].quantity * bookingsTrackingList[i].price;
        }

        var html = "";
        if(bookingTotalValue < depositThreshHold) {
            completeBookingButton.show();
            completeBookingDepositButton.hide();
            bookingsOverviewView.show();
            html += "You have selected " + bookingsList.length + " Treatment/s";
        } else {
            // update the treatment selected list
            completeBookingButton.hide();
            completeBookingDepositButton.show();
            bookingsOverviewView.show();
            html += "Your treatment total of R" + bookingTotalValue.toFixed(2) + " requires a "+depositPercentage+"% deposit to be paid. Click proceed to make payment ";
        }
        treatmentsSelectedForBooking.empty().append(html);
    }

    function __completeBookingProcess (){
        var newBooking = {};
        var bookingUrl = baseScheduleUrl;

        if(!bookingsList || bookingsList.length === 0) {
            toastr["info"]("Please select atleast one treatment to book for.","Info");
            return;
        }

        if(localStorage.getItem('role') !== 'client') {
            if(bookingDateInput.val()
                && bookingTimeInput.val()
                && localStorage.getItem('employeeId')
                && (clientListSelect.val() && clientListSelect.val() !== "")) {

                bookingUrl += "/employee-book";
                newBooking.startDateTime = bookingDateInput.val() + ' ' + bookingTimeInput.val();
                newBooking.employeeId = localStorage.getItem('employeeId');
                newBooking.clientId = clientListSelect.val();
                newBooking.scheduleNewBookingItemModels = bookingsList;

            } else {
                toastr["info"]("Please ensure you have selected the correct booking date, time and employee.","Info");
                return;
            }
        } else {
            if(bookingDateInput.val()
                && bookingTimeInput.val()
                && localStorage.getItem('employeeId')) {

                bookingUrl += "/client-book";
                newBooking.startDateTime = bookingDateInput.val() + ' ' + bookingTimeInput.val();
                newBooking.employeeId = localStorage.getItem('employeeId');
                newBooking.newBookingItemModel = bookingsList;

            } else {
                toastr["info"]("Please ensure you have selected the correct booking date and time.","Info");
                return;
            }
        }


        $.ajax({
            url : bookingUrl,
            type: 'POST',
            data: JSON.stringify(newBooking),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            // toastr["success"](response.message, "Success");

            // hide booking list, clear local storage, redirect
            localStorage.removeItem('bookingDate');
            localStorage.removeItem('bookingTime');
            bookingsList = [];
            employeesScheduleToView.fullCalendar('refetchEvents');
            bookingsOverviewView.hide();
            bookingTreatmentsSection.hide();

            employeesScheduleToView.show();
            initiateJoinCancellationQueueButton.show();
            startBookingProcessButton.show();
            if(localStorage.getItem('role') !== 'client') {
                initiateBlockoutDayButton.show();
            }

            var html = '<p>'

            if(response.pointsNeededForDiscount > 0) {
                html += 'Congratulations you have earned ' + response.pointsEarned + ' points, you are ';
                html += response.pointsNeededForDiscount + ' away from receiving a discount. </p>'
                confirmationMessage.empty().append(html);
            } else {
                html += 'Congratulations you have earned ' + response.pointsEarned + ' points, you will ';
                html += ' receive a 10% discount for your next booking. </p>'
                confirmationMessage.empty().append(html);
            }

            bookingConfirmationModal.modal('show');
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __completeBookingPendingProcess (){
        var newBooking = {};
        var bookingUrl = baseScheduleUrl;

        if(!bookingsList || bookingsList.length === 0) {
            toastr["info"]("Please select minimum one treatment to book for.","Info");
            return;
        }

        if(localStorage.getItem('role') !== 'client') {
            if(bookingDateInput.val()
                && bookingTimeInput.val()
                && localStorage.getItem('employeeId')
                && (clientListSelect.val() && clientListSelect.val() !== "")) {

                bookingUrl += "/employee-book";
                newBooking.startDateTime = bookingDateInput.val() + ' ' + bookingTimeInput.val();
                newBooking.employeeId = localStorage.getItem('employeeId');
                newBooking.clientId = clientListSelect.val();
                newBooking.scheduleNewBookingItemModels = bookingsList;
                newBooking.depositRequired = true;

            } else {
                toastr["info"]("Please ensure you have selected the correct booking date, time and employee.","Info");
                return;
            }
        } else {
            if(bookingDateInput.val()
                && bookingTimeInput.val()
                && localStorage.getItem('employeeId')) {

                bookingUrl += "/client-book";
                newBooking.startDateTime = bookingDateInput.val() + ' ' + bookingTimeInput.val();
                newBooking.employeeId = localStorage.getItem('employeeId');
                newBooking.newBookingItemModel = bookingsList;
                newBooking.depositRequired = true;

            } else {
                toastr["info"]("Please ensure you have selected the correct booking date and time.","Info");
                return;
            }
        }

        $.ajax({
            url : bookingUrl,
            type: 'POST',
            data: JSON.stringify(newBooking),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            // hide booking list, clear local storage, redirect
            localStorage.removeItem('bookingDate');
            localStorage.removeItem('bookingTime');

            localStorage.setItem('bookingId', response.bookingId);
            bookingsList = [];

            // once we have captured the pending booking start payment process
            __completeBookingDepositProcess();
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __completeBookingDepositProcess() {
        // calculate deposit of total bookings
        var bookingTotalValue = 0;
        for(var i = 0; i < bookingsTrackingList.length; i++) {
            bookingTotalValue += bookingsTrackingList[i].quantity * bookingsTrackingList[i].price;
        }

        var bookingValueToCapture = bookingTotalValue.toFixed(2) * (depositPercentage/100);

        var paymentModel = {};
        paymentModel.price = bookingValueToCapture.toFixed(2);

        // start payment process
        $.ajax({
            url : "/api/schedule/payments",
            type: 'POST',
            data: JSON.stringify(paymentModel),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            // if successfully received checkout id, display checkout form
            // $("#scheduleView").append($("<script>", {  src : "https://test.oppwa.com/v1/paymentWidgets.js?checkoutId="+response.id,  type : "text/javascript" }))
            $("#scheduleView").append($("<script>", {  src : "https://oppwa.com/v1/paymentWidgets.js?checkoutId="+response.id,  type : "text/javascript" }))
            // show capture card details form
            bookingsOverviewView.hide();
            bookingTreatmentsSection.hide();
            bookingDepositCaptureView.show();
        }).fail(function(response){
            __handleAjaxError(response)
        });

    }

    function __startBlockoutProcess() {
        // load start and end times
        // __getTimesForBlockingOut();

        // load drop down for all or multi select or single select
        __getAllEmployeesToBlockoutTimeFor();

        blockoutSpecificViewOption.modal('show');
    }

    function __getAllEmployeesToBlockoutTimeFor() {
        $.ajax({
            url: baseScheduleUrl + "/employees",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = "";

            _.each(response, function (employee) {
                html += '<option value="'+employee.employeeId+'">' + employee.employeeTitle + '</option>';
            });

            blockoutTimeEmployeesSelect.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __getTimesForBlockingOut() {
        $.ajax({
            url: baseScheduleUrl + "/blockout-time/times/" + moment(blockoutTimeDate.val()).format("dddd"),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html ="";

            _.each(response, function(time){
                html+= "<option value="+time.time+">"+time.time+"</option>";
            });

            blockoutTimeStart.empty().append(html);
            blockoutTimeEnd.empty().append(html);

        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __completeBlockoutTimeProcess() {
        var repeatEventDetails = {};

        if(localStorage.getItem('role') !== 'client') {
            if(blockoutTimeTitleInput.val()
                && blockoutTimeStart.val()
                && blockoutTimeEnd.val()
                && blockoutTimeDate.val()
                && (blockoutTimeEmployeesSelect.val() && blockoutTimeEmployeesSelect.val() !== "")) {

                repeatEventDetails.blockoutTimeTitle    = blockoutTimeTitleInput.val();
                repeatEventDetails.startDateTime        = moment(blockoutTimeDate.val()).format("YYYY-MM-DD") + ' ' + blockoutTimeStart.val();
                repeatEventDetails.endDateTime          = moment(blockoutTimeDate.val()).format("YYYY-MM-DD") + ' ' + blockoutTimeEnd.val();
                repeatEventDetails.employees            = blockoutTimeEmployeesSelect.val();

            } else {
                toastr["info"]("Please ensure you have selected the correct booking date, time and employees.","Info");
                return;
            }
        } else {
            toastr["warning"]("You are not authorised to perform this action.","Warning!");
            return;
        }

        $.ajax({
            url : baseScheduleUrl + "/blockout-time",
            type: 'POST',
            data: JSON.stringify(repeatEventDetails),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");

            // hide booking list, clear local storage, redirect
            employeesScheduleToView.fullCalendar('refetchEvents');
            blockoutSpecificViewOption.modal('hide');

            employeesScheduleToView.show();
            initiateJoinCancellationQueueButton.show();
            startBookingProcessButton.show();
            initiateBlockoutDayButton.show();
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __setDateTimeForBlockedOutDay(dateTime, calendarView){

        var formattedDate = "";
        if(calendarView === "agendaWeek"){
            var stringDate = dateTime.substring(0, dateTime.indexOf('T'));
            var stringTime = dateTime.substring(dateTime.indexOf('T')+1,dateTime.length - 3);

            formattedDate = moment(stringDate);

            fromDateInput.val(formattedDate.format('MM/DD/YYYY'));
            toDateInput.val(formattedDate.format('MM/DD/YYYY'));

            fromTimeInput.val(stringTime);
            toTimeInput.val(toHiddenTimeInput.val());

            allDayBlockCheckBox.prop('checked', false);

            //add details to view
            blockTimeDetails.empty().append("<br><b>Start Date Time:</b> " + fromDateInput.val() + " " + stringTime + "<br><b>End Date Time:</b> " + toDateInput.val() + " " + toHiddenTimeInput.val() + "<br><br>");
        } else {//month view
            //set date field as time will already be set
            formattedDate = moment(dateTime);

            fromDateInput.val(formattedDate.format('MM/DD/YYYY'));
            toDateInput.val(formattedDate.format('MM/DD/YYYY'));

            fromTimeInput.val(fromHiddenTimeInput.val());
            toTimeInput.val(toHiddenTimeInput.val());

            allDayBlockCheckBox.prop('checked', true);

            blockTimeDetails.empty().append("<b>All Day Booking</b>" + "<br><b>Start Date Time:</b> " + fromDateInput.val() + " " + fromHiddenTimeInput.val() + "<br><b>End Date Time:</b> " + toDateInput.val() + " " + toHiddenTimeInput.val() + "<br><br>");
        }

        repeatEventDetails =  {};

        repeatEventDetails.repeatEventTitle = blockDayTitleInput.val();
        repeatEventDetails.startsOnDate     = moment(fromDateInput.val()).format("YYYY-MM-DD");
        repeatEventDetails.startsOnTime     = fromTimeInput.val() + ":00";
        repeatEventDetails.endsOnDate       = moment(toDateInput.val()).format("YYYY-MM-DD");
        repeatEventDetails.endsOnTime       = toTimeInput.val() + ":00";
        repeatEventDetails.employeeId       = loggedInEmployeeId.val();
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

    function __initialiseDefaultSchedule() {

        var headerOptions;

        var width = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);
        var height = "";
        var defaultView = "";
        var customButtonText = "";

        if(width < 480) {
            height = 700;
            headerOptions = {
                left: 'prev,next',
                center: 'title',
                right: 'selectScheduleButton'
            };

            if(localStorage.getItem('role') === 'client') {
                defaultView = "listDay";
            } else {
                defaultView = "listWeek";
            }
            customButtonText = "SCHEDULE";

        } else {
            height = "auto";
            headerOptions = {
                left: 'prev,next today',
                center: 'title',
                right: 'selectScheduleButton'
            };

            if(localStorage.getItem('role') === 'client') {
                defaultView = "listDay";
            } else {
                defaultView = "listWeek";
            }
            customButtonText = "SELECT SCHEDULE";
        }

        employeesScheduleToView.fullCalendar({
            height: height,
            eventColor: '#C5F861',
            customButtons: {
                selectScheduleButton: {
                    text: customButtonText,
                    click: function() {
                        __displayEmployeesToScheduleWith();
                    }
                }
            },
            header: headerOptions,
            defaultView: defaultView,
            validRange: function(nowDate) {
                if(localStorage.getItem('role') === 'client') {
                    return {
                        start: nowDate.clone().subtract(1, 'weeks'),
                        end: nowDate.clone().add(6, 'weeks')
                    };
                } else {
                    return {
                        start: nowDate.clone().subtract(12, 'months'),
                        end: nowDate.clone().add(12, 'months')
                    };
                }
            },
            displayEventEnd: {
                "default": true
            },
            selectable: true,
            eventLimit: true,
            noEventsMessage: "Seems like no one has booked yet. Hurry now to make your booking!",
            events: function (start, end, timezone, callback) {
                if(localStorage.getItem('role') === 'client') {
                    if(localStorage.getItem('employeeId') && localStorage.getItem('employeeId') !== "") {
                        return __loadTimeSlotsForBooking(start, callback)
                    } else {
                        toastr["info"]("To view one of the employees schedules please click on the select schedule button in the top right.","Info");
                    }
                } else {
                    if(localStorage.getItem('employeeId') && localStorage.getItem('employeeId') !== "") {
                        return __getAllBookingsForSchedule(start, end, timezone, callback)
                    } else {
                        toastr["info"]("To view one of the employees schedules please click on the select schedule button in the top right.","Info");
                    }
                }
            },
            eventClick: function(booking){
                if(booking.canCancel && booking.canView) {
                    bookingToViewOrCancelInput.val(booking.id);
                    scheduleDayClickContextOption.modal('show');
                } else {
                    // check if client then save the current date of calendar and time and preload it for them
                    if(localStorage.getItem('role') === 'client') {
                        if(booking.available) {
                            localStorage.setItem('startDate', booking.date)
                            localStorage.setItem('startTime', booking.time);
                            // start booking process
                            __startBookingProcess();
                        } else {
                            bookingToViewOrCancelInput.val("");
                            toastr["info"]("This slot is not available for booking. Please try one that says Available.","Info");
                        }
                    }
                }
            },
            viewRender: function(){
                __loadConfigurations();
            }
        });
    }

    function __getAllBookingsForSchedule(start, end, timezone, callback) {
        $.ajax({
            url: baseScheduleUrl + '/bookings/' + localStorage.getItem('employeeId'),
            data: {
                start: start.format('YYYY-MM-DD'),
                end: end.format('YYYY-MM-DD'),
                _: Math.random()
            },
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (responseEvents) {
            var events = [];

            _.each(responseEvents, function (booking) {
                if(booking.depositPaid) {
                    events.push({
                        title: booking.title,
                        start: booking.startTime,
                        description: booking.description,
                        employeeFullName: booking.employeeFullName,
                        id: booking.bookingId,
                        end: booking.endTime,
                        canCancel: booking.canCancel,
                        canView: booking.canView,
                        color: 'blue'
                    });
                } else {
                    events.push({
                        title: booking.title,
                        start: booking.startTime,
                        description: booking.description,
                        employeeFullName: booking.employeeFullName,
                        id: booking.bookingId,
                        end: booking.endTime,
                        canCancel: booking.canCancel,
                        canView: booking.canView
                    });
                }
            });
            callback(events);
        }).fail(function (response) {
            __handleAjaxError(response)
        });
    }

    function __loadTimeSlotsForBooking(start, callback) {
        if(localStorage.getItem('employeeId') && localStorage.getItem('employeeId') !== "") {
            var formattedDate = moment(start).format('YY-MM-DD');

            $.ajax({
                url: baseScheduleUrl + "/times/" + localStorage.getItem('employeeId') + "/" + formattedDate,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                var events = [];

                _.each(response, function (time) {
                    events.push({
                        title: time.available? 'Available' : 'Not Available',
                        start: time.time,
                        date: formattedDate,
                        time: time.time,
                        available: time.available,
                        backgroundColor: time.available? '#C5F861': 'grey',
                    });
                });
                callback(events);
            }).fail(function (response) {
                __handleAjaxError(response)
            });
        } else {
            toastr["info"]("To continue booking please click on the select schedule button in the top right corner and select the employee you will be visiting.","Info");
        }
    }

    function __checkIfClientHasAcceptedTerms() {
        $.ajax({
            url: baseScheduleUrl + "/accepted-terms",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            if (response.userHasAcceptedTerms === false) {
                termsAndAgreementModal.modal({
                    show: true,
                    backdrop: 'static'
                });
            }
        }).fail(function (response) {
            __handleAjaxError(response)
        });
    }

    function __clientAcceptTerms() {
        $.ajax({
            url: baseScheduleUrl + "/accept-terms",
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");
            termsAndAgreementModal.modal('hide');
            employeesScheduleToView.fullCalendar( 'refetchEvents');
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __displayEmployeesToScheduleWith() {
        $.ajax({
            url: baseScheduleUrl + "/employees",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = "";

            if(localStorage.getItem('employeeId') === undefined) {
                localStorage.setItem('employeeId', response[0].employeeId); // this is a hack
            }

            _.each(response, function (employee) {
                if(localStorage.getItem('employeeId') !== undefined && localStorage.getItem('employeeId') === employee.employeeId) {
                    html += '<button type="button" class="list-group-item list-group-item-action primary-color accent-color" data-employeeid="'+employee.employeeId+'" onclick="scheduleModule.viewScheduleForEmployee(this)">' + employee.employeeTitle + '(' + employee.employeeFullName + ')</button>';
                } else {
                    html += '<button type="button" class="list-group-item list-group-item-action" data-employeetype="'+employee.employeeTitle+'" data-employeeid='+employee.employeeId+' onclick="scheduleModule.viewScheduleForEmployee(this)">' + employee.employeeTitle + '(' + employee.employeeFullName + ')</button>';
                }
            });

            employeesScheduleToViewResultList.empty().append(html);

            employeesScheduleToViewModal.modal('show');
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __viewScheduleForEmployee(e) {
        var employeeId = $(e).data('employeeid');
        var employeeType = $(e).data('employeetype');
        localStorage.setItem('employeeId', employeeId);
        if (employeeType) {
            localStorage.setItem('employeeType', employeeType);
        }

        employeesScheduleToViewModal.modal('hide');

        employeesScheduleToView.fullCalendar( 'refetchEvents');
    }

    function __viewBookingDetails() {
        scheduleDayClickContextOption.modal('hide');
        $.ajax({
            url: baseScheduleUrl + "/booking/"+bookingToViewOrCancelInput.val(),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = '<div class="row">';
                html += '<div class="col-12">';
                    if(response.description !== null && response.description !== "") {
                        html += '<span style="font-weight: bold;"> Booking for ' + response.title + '</span>';
                    } else {
                        html += '<span style="font-weight: bold;">' + response.title + '</span>';
                    }
                    if(response.description !== null && response.description !== "") {
                        html += '<br><span style="font-weight: bold;">Treatments: </span> ' + response.description;
                    }
                    html += '<br><span style="font-weight: bold;">Times: </span>' + response.startTime + ' - ' + response.endTime;
                    if(response.employeeFullName !== null && response.employeeFullName !== "") {
                        html += '<br><br> <span style="font-style: italic;">Coming to ' + response.employeeFullName;
                    }
                    if(response.depositPaid) {
                        html += '<br><br> <span style="font-weight: bold;">Deposit was paid for booking';
                    }
                html += '</div>';
            html += '</div>';
            bookingSpecificView.empty().append(html);

            scheduleSpecificViewOptionModal.modal('show');
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __cancelBooking() {
        $.ajax({
            url: baseScheduleUrl + "/cancel/"+bookingToViewOrCancelInput.val(),
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");
            scheduleDayClickContextOption.modal('hide');
            employeesScheduleToView.fullCalendar( 'refetchEvents');
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __loadConfigurations(){
        $.ajax({
            url: "/api/public/web/operatingTime"
        }).done(function(response){

            $("td.fc-today").css('backgroundColor','rgba(255, 255, 255, 0.5) !important').css('color','black');
            _.each(response.workingDays, function(workingDayModel) {
                if(workingDayModel.workingDay === "Sunday"){
                    $(".fc-day.fc-sun").css('backgroundColor','rgba(207, 221, 224, 0.5)').css('color','black');
                }else if(workingDayModel.workingDay === "Monday"){
                    $(".fc-day.fc-mon").css('backgroundColor','rgba(207, 221, 224, 0.5)').css('color','black');
                }else if(workingDayModel.workingDay === "Tuesday"){
                    $(".fc-day.fc-tue").css('backgroundColor','rgba(207, 221, 224, 0.5)').css('color','black');
                }else if(workingDayModel.workingDay === "Wednesday"){
                    $(".fc-day.fc-wed").css('backgroundColor','rgba(207, 221, 224, 0.5)').css('color','black');
                }else if(workingDayModel.workingDay === "Thursday"){
                    $(".fc-day.fc-thu").css('backgroundColor','rgba(207, 221, 224, 0.5)').css('color','black');
                }else if(workingDayModel.workingDay === "Friday"){
                    $(".fc-day.fc-fri").css('backgroundColor','rgba(207, 221, 224, 0.5)').css('color','black');
                }else if(workingDayModel.workingDay === "Saturday"){
                    $(".fc-day.fc-sat").css('backgroundColor','rgba(207, 221, 224, 0.5)').css('color','black');
                }
            });
        }).fail(function(){
            toastr["info"]("No operating time was provided. Please contact the business to find out what time they open and close.","Hint!");
        });
    }

    function __loadDepositThreshold() {
        $.ajax({
            url: "/api/public/web/deposit-threshold"
        }).done(function(response){
            depositThreshHold = response.depositThreshold;
            depositPercentage = response.depositPercentage;
        }).fail(function(){
            toastr["info"]("No deposit was provided. Please contact the business to find out what time they open and close.","Hint!");
        });
    }

    function __initialiseModule() {
        __loadConfigurations();

        if(localStorage.getItem('role') !== 'client') {
            initiateJoinCancellationQueueButton.text('VIEW CANCELLATION QUEUE');
        } else {
            initiateBlockoutDayButton.hide();
            __checkIfClientHasAcceptedTerms();
        }

        setTimeout(function () {


            if(localStorage.getItem('bookingDate')) {
                localStorage.removeItem('bookingDate');
            }

            if(localStorage.getItem('bookingTime')) {
                localStorage.removeItem('bookingTime');
            }
            // calendar options
            __initialiseDefaultSchedule();
            __loadDepositThreshold();
        }, 500);
    }

    //public functions
    return{
        initialiseModule        : __initialiseModule,
        viewScheduleForEmployee : __viewScheduleForEmployee,
        setDateTimeFields       : __setDateTimeForBlockedOutDay,
        viewBookingDetails      : __viewBookingDetails,
        cancelBooking           : __cancelBooking,
        decreaseBookingTreatmentTotal: __decreaseBookingTreatmentTotal,
        increaseBookingTreatmentTotal: __increaseBookingTreatmentTotal
    }
})();
