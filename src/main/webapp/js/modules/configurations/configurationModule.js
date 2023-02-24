var configurationModule = (function(){
    //cache elements
    var allCheckedBoxes             = $(".daysAvailable");
    var saveConfigurationsButton    = $("#saveConfigurations");
    var configurationForm           = $("#configurationForm");
    var updateConfigurationsButton  = $("#updateConfigurations");
    var appConfigId                 = $("#id");
    var attendeesPerSession         = $("#attendeesPerSession");
    var sessionDurations            = $("#sessionDurations");
    var workStartTime               = $("#workStartTime");
    var workEndTime                 = $("#workEndTime");
    var bookingNotice               = $("#bookingNotice");
    var cancelNotice                = $("#cancelNotice");
    var depositThreshold            = $("#depositThreshold");
    var depositPercentage           = $("#depositPercentage");

    //event binders
    saveConfigurationsButton.on('click', __handleConfigurationForm);
    updateConfigurationsButton.on('click', __handleConfigurationFormUpdate);
    //event handlers
    // function __handleDaysAvailable() {
    //     if($(this).prop("checked") === true){
    //         allCheckedBoxes.each(function(){
    //             $(this).prop( "checked", true );
    //         });
    //     }else{
    //         allCheckedBoxes.each(function(){
    //             $(this).prop( "checked", false );
    //         });
    //     }
    // }

    function __handleConfigurationForm() {
        var configForm = configurationForm.serializeJSON();
        var form = document.getElementById('configurationForm');
        if (form.checkValidity() === false) {
            configurationForm.addClass('was-validated');
        } else {
            var configFormAmended = {}
            configFormAmended.attendeesPerSession = configForm.attendeesPerSession;
            configFormAmended.bookingNotice = configForm.bookingNotice;
            configFormAmended.cancelNotice = configForm.cancelNotice;
            configFormAmended.depositPercentage = configForm.depositPercentage;
            configFormAmended.depositThreshold = configForm.depositThreshold;
            configFormAmended.id = configForm.id;
            configFormAmended.sessionDurations = configForm.sessionDurations;
            configFormAmended.workingDays = [];

            $('input:checkbox[class*="daysAvailable-"]').each(function(i, el){
                if(el.type === 'checkbox' && el.checked === true){
                    configFormAmended.workingDays.push({
                        workEndTime: $('input[class*="endAvailable-'+el.id+'"]').val(),
                        workStartTime: $('input[class*="startAvailable-'+el.id+'"]').val(),
                        workingDay: $(el).val()
                    });
                }
            });

            // console.log(JSON.stringify(configFormAmended))

            $.ajax({
                url: '/api/auth/configurations',
                type: 'POST',
                data: JSON.stringify(configFormAmended),
                dataType: 'json',
                contentType: 'application/json;charset=UTF-8',
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function(response){
                toastr["success"](response.message, "Success");
                __loadConfigurations();
            }).fail(function(response){
                console.log(JSON.stringify(configFormAmended))
                __handleAjaxError(response)
            });
        }
    }

    function __handleConfigurationFormUpdate() {
        var configForm = configurationForm.serializeJSON();
        var form = document.getElementById('configurationForm');
        if (form.checkValidity() === false) {
            configurationForm.addClass('was-validated');
        } else {
            var configFormAmended = {}
            configFormAmended.attendeesPerSession = configForm.attendeesPerSession;
            configFormAmended.bookingNotice = configForm.bookingNotice;
            configFormAmended.cancelNotice = configForm.cancelNotice;
            configFormAmended.depositPercentage = configForm.depositPercentage;
            configFormAmended.depositThreshold = configForm.depositThreshold;
            configFormAmended.id = configForm.id;
            configFormAmended.sessionDurations = configForm.sessionDurations;
            configFormAmended.workingDays = [];

            $('input:checkbox[class*="daysAvailable-"]').each(function(i, el){
                if(el.type === 'checkbox' && el.checked === true){
                    configFormAmended.workingDays.push({
                        workEndTime: $('input[class*="endAvailable-'+el.id+'"]').val(),
                        workStartTime: $('input[class*="startAvailable-'+el.id+'"]').val(),
                        workingDay: $(el).val()
                    });
                }
            });

            $.ajax({
                url: '/api/auth/configurations',
                type: 'PUT',
                data: JSON.stringify(configFormAmended),
                dataType: 'json',
                contentType: 'application/json;charset=UTF-8',
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function(response){
                toastr["success"](response.message, "Success");
                __loadConfigurations();
            }).fail(function(response){
                __handleAjaxError(response)
            });
        }
    }

    //private functions
    function __initialiseModule(){
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

        __loadConfigurations();
    }

    function __loadConfigurations(){
        $.ajax({
            url: '/api/auth/configurations',
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response){
            updateConfigurationsButton.show();
            saveConfigurationsButton.hide();

            appConfigId.val(response.id);
            attendeesPerSession.val(response.attendeesPerSession);
            sessionDurations.val(response.sessionDurations);
            // workStartTime.val(response.workStartTime);
            // workEndTime.val(response.workEndTime);
            bookingNotice.val(response.bookingNotice);
            cancelNotice.val(response.cancelNotice);
            depositThreshold.val(response.depositThreshold);
            depositPercentage.val(response.depositPercentage);

            allCheckedBoxes.each(function(){
                $(this).prop( "checked", false );
            });

            _.each(response.workingDays, function(workingDayModel){
                $('input:checkbox[class*="daysAvailable-'+workingDayModel.workingDay.toLowerCase()+'"]').prop( "checked", true );
                $('input[class*="startAvailable-'+workingDayModel.workingDay.toLowerCase()+'"]').val(workingDayModel.workStartTime)
                $('input[class*="endAvailable-'+workingDayModel.workingDay.toLowerCase()+'"]').val(workingDayModel.workEndTime)
            });

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
            window.location.href = "/views/login/index.html";
        }, 1500);
    }

    //public functions
    return{
        initialiseModule    : __initialiseModule,
        loadConfigurations  : __loadConfigurations
    }
})();
