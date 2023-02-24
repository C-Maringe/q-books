var appModule = (function(){
    // cache
    var fullscreenOverlay   = $(".fullscreen-loader");
    var mainNav             = $("#mainNav");
    var registerForm        = $("#registerForm");
    var inputs              = $("#inputName, #inputSurname");

    var timeAvailable       = $("#timeAvailable");
    var operatingHours      = $("#operatingHours");
    var daysAvailable       = $("#daysAvailable");

    // event handlers
    registerForm.on("submit", __handleRegistration);
    inputs.on("blur", __capitaliseInputs);

    function __handleRegistration(e) {
        e.preventDefault();

        var newClient = $(this).serializeJSON();

        $.ajax({
            url         : "/api/public/web/register",
            type        : "POST",
            dataType    : "json",
            data        : JSON.stringify(newClient),
            contentType : "application/json;charset=UTF-8"
        }).done(function(){
            toastr["success"]("Thank you for registering with us. Please proceed by signing in.", "Welcome");
            registerForm[0].reset();
            location.href = "/views/login/index.html";
        }).fail(function(response){
            toastr["error"](response.responseJSON.detail, response.responseJSON.title);
        });
    }

    function __capitaliseInputs() {
        var textValue = $(this).val();
        $(this).val(textValue.substr(0,1).toUpperCase()+textValue.substr(1).toLowerCase());
    }

    //private functions
    function __initialiseModule() {
        // check if user has remember me option enable and a valid token then login
        if(localStorage.getItem("token") !== undefined && localStorage.getItem("token") !== null && localStorage.getItem("token") !== "") {
            if(localStorage.getItem("rememberMe") !== undefined && localStorage.getItem("rememberMe") !== null && localStorage.getItem("token") !== false) {
                window.location.href = "/views/layout/index.html";
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

        mainNav.onePageNav({
            currentClass: "active",
            changeHash: false,
            scrollSpeed: 950,
            scrollThreshold: 0.2,
            filter: "",
            easing: "swing",
            begin: function () {
                //I get fired when the animation is starting
            },
            end: function () {
                //I get fired when the animation is ending
                if(!$("#main-nav ul li:first-child").hasClass("active")){
                    $(".header").addClass("addBg");
                }else{
                    $(".header").removeClass("addBg");
                }

            },
            scrollChange: function ($currentListItem) {
                //I get fired when you enter a section and I pass the list item of the section
                if(!$("#main-nav ul li:first-child").hasClass("active")){
                    $(".header").addClass("addBg");
                }else{
                    $(".header").removeClass("addBg");
                }
            }
        });

        $("a[href='#top']").click(function () {
            $("html, body").animate({ scrollTop: 0 }, "slow");
            return false;
        });
        $("a[href='#basics']").click(function () {
            $("html, body").animate({ scrollTop: $("#services").offset().top - 75 }, "slow");
            return false;
        });

        __loadOperatingTimes();
    }

    function __loadOperatingTimes() {
        $.ajax({
            url: "/api/public/web/operatingTime"
        }).done(function(response){
            //set blocked out day times
            timeAvailable.empty().append("<span class='font-weight:bold'>Office Hours:</span>");

            var daysString = "";

            _.each(response.workingDays, function(workingDayModel) {
                daysString += workingDayModel.workingDay + ": " + workingDayModel.workStartTime + " - " + workingDayModel.workEndTime + ", ";
            });

            daysAvailable.empty().append(daysString.substr(0,daysString.length-2));

            operatingHours.show();
        }).fail(function(){
            //if configurations failed to load notify the user that no settings added or server failure
            toastr["error"]("No operating time was provided. Please contact the business to find out what time they open and close.","Application Error");
        }).always(function () {
            setTimeout(function () {
                fullscreenOverlay.hide();
            }, 2500);
        });
    }

    //closure
    return {
        initialiseModule:   __initialiseModule
    };
})();
