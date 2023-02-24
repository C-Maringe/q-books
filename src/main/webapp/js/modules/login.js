var loginModule = (function(){
    // cache
    var fullscreenOverlay   = $(".fullscreen-loader");
    var loginForm           = $("#loginForm");

    var onStringToTrue = function(val) {
        if (val === "on") return true; // parse on strings as true boolean
        return val;
    };

    // event handlers
    loginForm.on("submit", __handleLogin);

    function __handleLogin(e) {
        e.preventDefault();

        var loginClient = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });

        $.ajax({
            url         : "/api/public/web/login",
            type        : "PUT",
            dataType    : "json",
            data        : JSON.stringify(loginClient),
            contentType : "application/json;charset=UTF-8"
        }).done(function(response){
            toastr["success"]("Thank you for signing into our booking system.", "Welcome");

            localStorage.setItem("token", response.token);
            localStorage.setItem("fullName", response.fullName);
            localStorage.setItem("role", response.role);
            localStorage.setItem("rememberMe", loginClient.keepMeLoggedIn);
            localStorage.setItem("userPermissions", JSON.stringify(response.userPermissionList));

            loginForm[0].reset();
            window.location.href = "/views/layout/index.html";
        }).fail(function(response){
            toastr["error"](response.responseJSON.detail, response.responseJSON.title);
        });
    }

    //private functions
    function __initialiseModule() {
        // check if user has remember me option enable and a valid token then login
        if(localStorage.getItem("token") !== undefined && localStorage.getItem("token") !== null && localStorage.getItem("token") !== "") {
            if(localStorage.getItem("rememberMe") !== undefined && localStorage.getItem("rememberMe") !== null && localStorage.getItem("token") !== false) {
                window.location.href = "/views/layout/";
            }
        }

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
    }

    //closure
    return {
        initialiseModule:   __initialiseModule
    };
})();
