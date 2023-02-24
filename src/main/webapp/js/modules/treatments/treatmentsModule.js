var treatmentsModule = (function(){
    //cache elements
    var employeeTypeFilterDropDown      = $("#employeeTypeFilter");
    var employeeTypeDropDown            = $("#employeeType");
    var employeeTypeDropDownInModal     = $("#employeeTypeForModal");
    var registrationSectionNavigator    = $("#registerSection");
    var viewRegisteredTreatmentsNavigator = $(".viewSection");
    var editTreatmentsNavigator         = $("#editTreatments");
    var listAllTreatmentsView           = $("#listTreatments");
    var registerTreatmentsView          = $("#registerTreatments");
    var treatmentsList                  = $("#treatmentLists");
    var warningResponse                 = $("#warningResponse");
    var newTreatmentForm                = $("#newTreatmentForm");
    var editTreatmentForm               = $("#editTreatmentForm");
    var treatmentNameInput              = $("#treatmentName");
    var treatmentDescriptionInput       = $("#treatmentDescription");
    var searchTreatmentInput            = $("#searchTreatment");
    var originalTreatmentListToSearch   = [];
    var treatmentListToSearch           = [];

    // custom add
    var specialCheckbox                 = $("#special");

    // Update Special Components
    var treatmentIdElementToUpdate            = $("#treatmentId");
    var treatmentNameElementToUpdate          = $("#updateTreatmentName");
    var treatmentDescriptionElementToUpdate   = $("#updateTreatmentDescription");
    var treatmentDoneByJuniorFlagToUpdate     = $("#updateIsDoneByJunior");
    var treatmentDoneBySeniorFlagToUpdate     = $("#updateIsDoneBySenior");
    var treatmentSeniorPriceToUpdate          = $("#updateSeniorPrice");
    var treatmentJuniorPriceToUpdate          = $("#updateJuniorPrice");
    var treatmentSpecialToUpdate              = $("#updateSpecial");
    var treatmentSpecialPriceToUpdate         = $("#updateSpecialPrice");
    var treatmentDurationToUpdate             = $("#updateDuration");
    var treatmentIsSpecial                    = $(".updateSpecialChecked");
    var newtTreatmentIsSpecial                = $(".specialChecked");
    var treatSpecialEndDate                   = $("#updateSpecialEndDate");

    var onStringToTrue = function(val) {
        if (val === "on") return true; // parse on strings as true boolean
        return val;
    };

    //event binders
    registrationSectionNavigator.on('click',    __toggleRegisterTreatmentsComponents);
    viewRegisteredTreatmentsNavigator.on('click', __toggleViewTreatmentsComponents);
    editTreatmentForm.on('submit',              __updateTreatment);
    newTreatmentForm.on('submit',               __registerTreatment);
    searchTreatmentInput.on('input',            __searchTreatments);
    treatmentNameInput.on('blur',               __modifyInput);
    treatmentDescriptionInput.on('blur',        __modifyInput);
    employeeTypeFilterDropDown.on('change',     __loadAllTreatmentsForEmployeeType);
    specialCheckbox.on('change',                __displaySpecialEndsOnDate);
    treatmentSpecialToUpdate.on('change',       __displayUpdateSpecialEndsOnDate);

    //event handlers
    function __toggleRegisterTreatmentsComponents() {
        listAllTreatmentsView.hide();
        registerTreatmentsView.show();
        editTreatmentsNavigator.hide();
        setTimeout(function(){
            __loadEmployeeType();

        }, 500);
    }

    function __toggleViewTreatmentsComponents() {
        newTreatmentForm[0].reset();
        listAllTreatmentsView.show();
        registerTreatmentsView.hide();
        editTreatmentsNavigator.hide();
        __loadEmployeeTypeForFilter();
        setTimeout(function(){
            treatmentsList.empty();
            __loadAllTreatmentsForEmployeeType();
        }, 500);

    }

    function  __modifyInput() {
        var textValue = $(this).val();
        $(this).val(textValue.substr(0,1).toUpperCase()+textValue.substr(1).toLowerCase());
    }

    function __searchTreatments() {
        var filterBy = $(this).val().toLowerCase();

        if(filterBy.length === 0) {
            treatmentListToSearch = originalTreatmentListToSearch;
        } else {
            treatmentListToSearch = _.filter(originalTreatmentListToSearch, function(treatment){
                return treatment.treatmentName.toLowerCase().indexOf(filterBy)  >= 0;
            });
        }

        __filterTreatmentList();
    }

    function __displaySpecialEndsOnDate() {
        if(this.checked) {
            newtTreatmentIsSpecial.show();
        } else {
            newtTreatmentIsSpecial.hide();
        }
    }

    function __displayUpdateSpecialEndsOnDate() {
        if(this.checked) {
            treatmentIsSpecial.show();
        } else {
            treatmentIsSpecial.hide();
        }
    }
    //private functions
    function __loadEmployeeType() {
        $.ajax({
            url: '/api/auth/treatments/employee-types',
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response) {
            var html = "";
            if (Array.isArray(response)) {
                for (var i = 0; i < response.length; i++) {
                    html += '<option value="'+response[i].employeeType+'">'+response[i].employeeType+'</option>';
                }
            }

            employeeTypeDropDown.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __loadEmployeeTypeForFilter() {
        $.ajax({
            url: '/api/auth/treatments/employee-types',
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response) {
            var html = "";
            if (Array.isArray(response)) {
                for (var i = 0; i < response.length; i++) {
                    html += '<option value="'+response[i].employeeType+'">'+response[i].employeeType+'</option>';
                }
            }

            employeeTypeFilterDropDown.empty().append(html);

        }).fail(function(response) {
            __handleAjaxError(response);
        });
    }

    function __loadAllTreatmentsForEmployeeType(){
        $.ajax({
            url : "/api/auth/treatments/filter/"+employeeTypeFilterDropDown.val(),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (data) {
            if(data.length === 0) {
                toastr["success"]("No services were found for the selected team.", "Success");
            } else {
                originalTreatmentListToSearch = data;
                treatmentListToSearch = data;

                __filterTreatmentList();
                searchTreatmentInput.val("");
            }
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __filterTreatmentList() {
        var html = "";

        _.each(treatmentListToSearch, function (treatment) {
            if (treatment.active)
                html += '<a class="list-group-item list-group-item-action">';
            else
                html += '<a class="list-group-item list-group-item-action list-group-item-danger">';

            html += '<div class="row">';
            html += '<div class="col-12 col-sm-8">';
            html += '<span style="font-weight: bold">' + treatment.treatmentName + '</span><br><br><span style="font-weight: bold">Price:</span> (R' + treatment.seniorPrice + ')<br><span style="font-weight: bold"> Duration</span> (' + treatment.duration + ' in minutes)';
            html += '</div>';

            html += '<div class="col-12 col-sm-4">';
            if (treatment.active) {
                html += '<button class="btn btn-primary btn-block" data-treatmentid=' + treatment.treatmentId + ' onclick="treatmentsModule.editTreatment(this)">Edit</button>';
                html += '<button class="btn btn-danger btn-block" data-treatmentid=' + treatment.treatmentId + ' onclick="treatmentsModule.disableTreatment(this)">Disable</button>';
            } else {
                html += '<button class="btn btn-success btn-block" data-treatmentid=' + treatment.treatmentId + ' onclick="treatmentsModule.enableTreatment(this)">Enable</button>';
            }
            html += '</div>';
            html += '</div>';

            html += '</a>';
        });

        treatmentsList.empty().append(html);
    }

    function __disableTreatment(btn){
        $.ajax({
            url : "/api/auth/treatments/disable/"+btn.dataset.treatmentid,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'))
            }
        })
        .done(function(response) {
            if(response.success != "true"){
                toastr["success"]("The service was successfully disabled.", "Operation Successful");
                treatmentsList.empty();
                __loadAllTreatmentsForEmployeeType();
            }else{
                toastr["error"]("The service was not disabled. Please refresh the page and try again.", "Error");
                __loadAllTreatmentsForEmployeeType();
            }
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __enableTreatment(btn){
        $.ajax({
            url : "/api/auth/treatments/enable/"+btn.dataset.treatmentid,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'))
            }
        })
        .done(function(response) {
            if(response.success != "true"){
                toastr["success"]("The service was successfully enabled.", "Operation Successful");
                treatmentsList.empty();
                __loadAllTreatmentsForEmployeeType();
            }else{
                toastr["error"]("The service was not enabled. Please refresh the page and try again.", "Error");
                __loadAllTreatmentsForEmployeeType();
            }
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __loadEmployeeTypeForModal() {
        $.ajax({
            url: '/api/auth/treatments/employee-types',
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response) {
            var html = "";
            if (Array.isArray(response)) {
                for (var i = 0; i < response.length; i++) {
                    html += '<option value="'+response[i].employeeType+'">'+response[i].employeeType+'</option>';
                }
            }

            employeeTypeDropDownInModal.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __editTreatment(btn){
        __loadEmployeeTypeForModal();

        editTreatmentsNavigator.show();
        listAllTreatmentsView.hide();

        $.ajax({
            url : "/api/auth/treatments/"+btn.dataset.treatmentid,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (data) {
            treatmentIdElementToUpdate.val(btn.dataset.treatmentid);
            treatmentNameElementToUpdate.val(data.treatmentName);
            treatmentDescriptionElementToUpdate.val(data.treatmentDescription);

            if(data.doneByJunior){
                treatmentDoneByJuniorFlagToUpdate.attr('checked', true)
            }else{
                treatmentDoneByJuniorFlagToUpdate.attr('checked', false)
            }
            if(data.doneBySenior){
                treatmentDoneBySeniorFlagToUpdate.attr('checked', true)
            } else{
                treatmentDoneBySeniorFlagToUpdate.attr('checked', false)
            }
            treatmentSeniorPriceToUpdate.val(data.seniorPrice);
            treatmentJuniorPriceToUpdate.val(data.juniorPrice);
            treatmentDurationToUpdate.val(data.duration);

            if(data.special === true){
                treatmentSpecialToUpdate.prop('checked', true);
                treatmentSpecialPriceToUpdate.val(data.specialPrice);
                treatmentIsSpecial.show();
                treatSpecialEndDate.val(data.specialEndDate);
            } else{
                treatmentSpecialToUpdate.prop('checked', false)
                treatmentIsSpecial.hide();
            }

            setTimeout(function () {
                employeeTypeDropDownInModal.children().each(function () {
                    if(this.value === data.employeeType) {
                        employeeTypeDropDownInModal.val(data.employeeType);
                    }
                });
            }, 500);

        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __registerTreatment(e){
        e.preventDefault();

        var form = document.getElementById('newTreatmentForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            newTreatmentForm.addClass('was-validated');
        } else {
            var newTreatment = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });
            $.ajax({
                url         : "/api/auth/treatments",
                type        : "POST",
                dataType    : "json",
                data        : JSON.stringify(newTreatment),
                contentType : "application/json;charset=UTF-8",
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function(results){
                if(results.success){
                    toastr["success"]("Your service was successfully registered.", "Success");
                } else {
                    toastr["error"]("Something went wrong trying to register the item. Please refresh the page and try again.", "Error");
                }

                __toggleViewTreatmentsComponents();

            }).fail(function(response){
                console.log(JSON.stringify(newTreatment))
                __handleAjaxError(response)
            });
        }
    }

    function __updateTreatment(e) {
        e.preventDefault();

        var form = document.getElementById('editTreatmentForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            editTreatmentForm.addClass('was-validated');
        } else {
            var updatedTreatment = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });

            $.ajax({
                url : "/api/auth/treatments",
                type: 'PUT',
                data: JSON.stringify(updatedTreatment),
                contentType: "application/json;charset=utf-8",
                dataType:"json",
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                if(response.success){
                    toastr["success"]("Your service was successfully updated.", "Success");
                    editTreatmentsNavigator.hide();
                    listAllTreatmentsView.show();

                    setTimeout(function(){
                        __loadAllTreatmentsForEmployeeType();
                    }, 500);
                } else {
                    toastr["error"](response.message, "Error");
                }
            }).fail(function(response){
                __handleAjaxError(response)
            });
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

        __loadEmployeeTypeForFilter();
        setTimeout(function(){
            __loadAllTreatmentsForEmployeeType();
        }, 500);
    }

    //public functions
    return{
        initialiseModule      : __initialiseModule,
        editTreatment         : __editTreatment,
        disableTreatment      : __disableTreatment,
        enableTreatment       : __enableTreatment
    }
})();


