var bookingsModule = (function () {
    var miniLoader          = $(".mini-loader");
    var dateToView          = $("#dateToView");
    var getBookingsBtn      = $("#getBookings");
    var bookingResultsTable = $("#bookingResults");
    var notifyAllBtn        = $("#notifyAllClients");

    getBookingsBtn.on('click', __handleGetBookingsForDate);
    notifyAllBtn.on('click', __handleNotifyAllClientsForDate);

    function __handleNotifyAllClientsForDate() {
        var dateSelected = new moment(dateToView.val()).format('YY-MM-DD');

        if(dateSelected === "Invalid date") {
            toastr["info"]("Please select a valid date to start.", "Hint");
            return;
        }

        miniLoader.show();
        $.ajax({
            url : "/api/auth/bookings/notify/"+dateSelected,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr){
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

    function __handleGetBookingsForDate() {
        var dateSelected = new moment(dateToView.val()).format('YY-MM-DD');

        if(dateSelected === "Invalid date") {
            toastr["info"]("Please select a valid date to start.", "Hint");
            return;
        }

        miniLoader.show();

        $.ajax({
            url: "/api/auth/bookings/" + dateSelected,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(bookings){
            var html = "";
            _.each(bookings ,function(booking){

                if(!booking.notificationSentAlready) {
                    html += '<a href="#" class="list-group-item list-group-item-action">';
                } else {
                    html += '<a href="#" class="list-group-item list-group-item-action list-group-item-success">';
                }

                html += '<div class="row">';
                html += '<div class="col-12 col-sm-8">';
                html +=  booking.clientFullName + " <span style='font-weight: bold; '>("+ booking.bookingSlot+")</span>";
                html += '<br>' + booking.treatments;
                html += '</div>';

                html += '<div class="col-12 col-sm-4">';
                    html += '<button class="btn btn-primary btn-block" data-bookingId='+booking.bookingId+' onclick="bookingsModule.sendBookingNotification(this)">Notify</button>';
                    html += '<button class="btn btn-danger btn-block" data-bookingId='+booking.bookingId+' onclick="bookingsModule.cancelBooking(this)">Cancel</button>';
                html += '</div>';
                html += '</div>';

                html += '</a>';
            });

            if(bookings.length > 0) {
                notifyAllBtn.show();
            } else {
                notifyAllBtn.hide();
            }

            bookingResultsTable.empty().append(html);
            toastr["success"]("Successfully loaded bookings for " + dateSelected, "Success");
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __sendBookingNotification(btn) {
        var dateSelected = new moment(dateToView.val()).format('YY-MM-DD');
        if(dateSelected === "Invalid date") {
            toastr["info"]("Please select a valid date to start.", "Hint");
            return;
        }

        miniLoader.show();

        var notifySpecificClient = {
            date: dateSelected,
            bookingId: btn.dataset.bookingid
        };

        // notify clients
        $.ajax({
            url : "/api/auth/bookings/notify",
            type: 'PUT',
            data: JSON.stringify(notifySpecificClient),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __cancelBooking(btn) {
        miniLoader.show();

        $.ajax({
            url: "/api/auth/bookings/"+btn.dataset.bookingid+"/cancel",
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Cancellation Success");
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
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
    }

    return {
        initialiseModule: __initialiseModule,
        sendBookingNotification: __sendBookingNotification,
        cancelBooking: __cancelBooking
    }
})();
