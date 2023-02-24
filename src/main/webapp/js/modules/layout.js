var layoutModule = (function(){
    // cache
    var fullscreenOverlay       = $(".fullscreen-loader");
    var viewLogout              = $(".nav-logout");
    var viewProfile             = $(".nav-profile");
    var viewApplication         = $("#body");
    var sideMenu                = $("#menu-toggle");
    var sideMenuWrapper         = $("#wrapper");

    // event handlers
    viewLogout.on('click', __logoutUser);
    sideMenu.on('click', __handleSideBarShow);

    function __handleSideBarShow(e) {
        e.preventDefault();
        sideMenuWrapper.toggleClass("active");
    }

    function __logoutUser() {
        localStorage.clear();
        window.location.href = "/views/login/index.html";
    }

    //private functions
    function __initialiseModule() {
        // validate that the user has a token and profile to login otherwise redirect to login
        if(localStorage.getItem("token") === undefined || localStorage.getItem("token") === null || localStorage.getItem("token") === "") {
            toastr["success"]("Please sign into the booking system to start making bookings.", "Info");
            localStorage.clear();
            window.location.href = "/views/login/index.html";
        }

        var userPermissions = JSON.parse(localStorage.getItem("userPermissions"));

        if(localStorage.getItem("role") === "client") {
            _.each(userPermissions, function(userPermission) {
                if(!userPermission.canRead && !userPermission.canWrite) {
                    $(".nav-" + userPermission.permissionFeature.toLowerCase()).remove();
                }
            });
        } else if (localStorage.getItem("role") === "employee"  || localStorage.getItem("role") === "admin") {
            viewProfile.remove();

            _.each(userPermissions, function(userPermission) {
                if(!userPermission.canRead && !userPermission.canWrite) {
                    $(".nav-" + userPermission.permissionFeature.toLowerCase()).remove();
                }
            });
        } else { // we have no role here
            console.log("should never come here");
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

        if(localStorage.getItem('role') === 'client') {
            viewApplication.empty().append(render('profile', 'index', {})); // this sets our default view to display when login happens
        } else {
            viewApplication.empty().append(render('schedule', 'index', {})); // this sets our default view to display when login happens
        }
        setTimeout(function () {
            fullscreenOverlay.hide();
        }, 2500);

        var width = Math.max(document.documentElement.clientWidth, window.innerWidth || 0);

        if(width <= 1440) {
            sideMenuWrapper.toggleClass("active");
        }
    }


    //closure
    return {
        initialiseModule:   __initialiseModule
    };
})();
