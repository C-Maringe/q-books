var paymentModule = (function(){
    var body = $("body");
    body.ready(__checkPaymentHandled);

    //event handlers
    function __checkPaymentHandled() {
        var sPageURL = window.location.search.substring(1),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        var bookingId = '';
        var transactionId = '';
        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === 'id') {
                transactionId =  sParameterName[1] === undefined ? '' : decodeURIComponent(sParameterName[1]);
            }

            if(sParameterName[0] === 'bookingId') {
                bookingId =  sParameterName[1] === undefined ? '' : decodeURIComponent(sParameterName[1]);
            }
        }

        if(transactionId !== '' && bookingId !== '') {
            $.ajax({
                url: "/api/schedule/payments/status/" + transactionId + "/" + bookingId,
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                if (response.transactionApproved) {
                    $(".titleMessage").empty().append("Congratulations!!")
                    $("#js-whoops").empty().append("The booking was successfully processed. Please check the app to view your booking.")
                } else {
                    $(".titleMessage").empty().append("400 User Error")
                    $("#js-whoops").empty().append("The booking was not completed. " + response.result.description)
                }

            }).fail(function (response) {
                __handleAjaxError(response)
            }).always(function () {
                setTimeout(function () {
                    window.location = 'https://washandvac.q-book.co.za'
                }, 60000);
            })
        } else {
            toastr["error"]("The booking was not completed, due to the transaction not being completed. Please contact the administrator.", "Result");
        }
    }

    function __handleAjaxError(response) {
        var parsedResponse = JSON.parse(response.responseText);
        $(".titleMessage").empty().append(parsedResponse.status + " Server Error")
        $("#js-whoops").empty().append(parsedResponse.detail)
    }

    function __initialiseModule() {

    }

    //public functions
    return{
        initialiseModule        : __initialiseModule
    }
})();
