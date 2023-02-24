var forgotPasswordModule = (function(){
    // cache
    var fullscreenOverlay       = $(".fullscreen-loader");
    var forgotPasswordForm      = $("#forgotPasswordForm");
    var resetPasswordForm       = $("#resetPasswordForm");
    var userToken               = "";

    forgotPasswordForm.on('submit', __handleSendEmailReset);
    resetPasswordForm.on('submit', __handlePasswordReset);

    // event handlers
    function __handleSendEmailReset(e) {
        var form = document.getElementById('forgotPasswordForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            return
        } else {
            var forgotPassword = $(this).serializeJSON();

            $.ajax({
                url         : "/api/public/web/forgot-password",
                type        : "PUT",
                dataType    : "json",
                data        : JSON.stringify(forgotPassword),
                contentType : "application/json;charset=UTF-8"
            }).done(function(response){
                toastr["success"](response.message, "Info");
            }).fail(function(response){
                toastr["error"](response.responseJSON.detail, response.responseJSON.title);
            });
        }
    }

    function __handlePasswordReset(e) {
        var form = document.getElementById('resetPasswordForm');

        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            return;
        } else {
            var resetPasswordForm = $(this).serializeJSON();
            resetPasswordForm.token = userToken;

            $.ajax({
                url         : "/api/public/web/reset-password",
                type        : "PUT",
                dataType    : "json",
                data        : JSON.stringify(resetPasswordForm),
                contentType : "application/json;charset=UTF-8"
            }).done(function(response){
                toastr["success"](response.message + " You will know be automatically redirected to the login page or you can proceed by clicking on login in the menu.", "Success");

                setTimeout(function () {
                    location.href = "/views/login/index.html";
                }, 5000);
            }).fail(function(response){
                toastr["error"](response.responseJSON.detail, response.responseJSON.title);
            });
        }
    }

    //private functions
    function __initialiseModule() {
        toastr.options = {
            "closeButton": true,
            "debug": false,
            "newestOnTop": true,
            "progressBar": false,
            "positionClass": "toast-top-right",
            "preventDuplicates": false,
            "onclick": null,
            "showDuration": "500",
            "hideDuration": "1000",
            "timeOut": "5000",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };


        setTimeout(function () {
            fullscreenOverlay.hide();
        }, 2500);

        var queryStr = window.location.search;
        var paramPairs = queryStr.substr(1).split('&');

        var params = {};
        for (var i = 0; i < paramPairs.length; i++) {
            var parts = paramPairs[i].split('=');
            params[parts[0]] = parts[1];
        }

        if(params.tkid) {
            userToken = params.tkid;
        }
    }

    //closure
    return {
        initialiseModule:   __initialiseModule
    };
})();