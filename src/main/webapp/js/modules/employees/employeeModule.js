var employeeModule = (function(){
    var baseGoalUrl                     = "/api/auth/goals";
    //cache elements
    var miniLoader                      = $(".mini-loader");
    var registrationSectionNavigator    = $("#registerSection");
    var viewRegisteredSpecialsNavigator = $(".viewSection");
    var viewWorkHoursSection            = $("#viewWorkHoursSection");
    var listEmployeesView               = $("#viewEmployees");
    var registerEmployeesView           = $("#registerEmployees");
    var editEmployeeView                = $("#editEmployee");
    var manageWorkingDaysView           = $("#manageWorkingDays");
    var employeeTypeDropDown            = $("#employeeTypeOptions");
    var employeeType                    = $("#employeeType");
    var employeeLists                   = $("#employeeLists");
    var newEmployeeForm                 = $("#registerForm");
    var editEmployeeForm                = $("#updateForm");
    var inputListeners                  = $("#inputName, #inputSurname, #updateInputName, #updateInputSurname");
    // update inputs
    var updateInputName                 = $("#editFirstName");
    var updateInputSurname              = $("#editLastName");
    var userId                          = $("#editUserId");
    var updateInputEmail                = $("#editEmailAddress");
    var updateMobileNumber              = $("#editMobileNumber");
    var updateEmployeeType              = $("#editEmployeeType");
    var updateEmployeeTypeOptions       = $("#editEmployeeTypeOptions");
    var updateEmployeeLevel             = $("#editEmployeeLevel");
    var updateMustBookConsultationFirstTime = $("#editMustBookConsultationFirstTime");

    // working days elements
    var workingYearSelect               = $("#workingYear");
    var workingMonthSelect              = $("#workingMonth");
    var btnLoadWorkingDays              = $("#btnLoadWorkingDays");
    var daysInMonthTable                = $("#daysInMonth");
    var selectEmployeeWorkDayModal      = $("#selectEmployeeWorkDay");
    var confirmWorkingDayButton         = $("#confirmWorkingDayButton");
    var employeeWorkingDayStartTime     = $("#employeeWorkingDayStartTime");
    var employeeWorkingDayEndTime       = $("#employeeWorkingDayEndTime");
    var employeeWorkingDayLunchStartTime= $("#employeeWorkingDayLunchStartTime");
    var employeeWorkingDayLunchDuration = $("#employeeWorkingDayLunchDuration");
    var applyToWholeWeekOption          = $("#applyToWholeWeek");
    var currentDateInMonthToAllocate    = 0;
    var allocatedEmployeeWorkDayModal   = $("#allocatedEmployeeWorkDay");
    var removeWorkingDayButton          = $("#removeWorkingDayButton");
    var dataLoader                      = $("#dataLoader");

    let companyGoalsView             = $(".companyGoals");
    let companyAnalyticsView         = $(".companyAnalytics");
    let btnGoals                     = $(".btn-goals");
    let btnAnalytics                 = $(".btn-analytics");
    let goalCaptureForm              = $("#goalCaptureForm");
    let captureGoalModal             = $("#captureGoalModal");
    let employeeList                 = $("#employeeList");

    let currentGoalCard              = $(".current-goal-card");
    let currentGoalName              = $(".current-goal-name");
    let currentGoalMeasure           = $(".current-goal-measure");
    let currentGoalRevenue           = $(".current-goal-revenue");
    let currentProgress              = $("#current-progress");
    let currentGoal                  = {};
    let pastGoalsResults             = $("#pastGoalsResults");
    let pastGoalCard                 = $(".past-goal-card");

    let editGoalModal                = $("#editGoalModal");
    let goalEditForm                 = $("#goalEditForm");
    let goalEditFormGoalId           = $("#goalEditForm input[name='goalId']")
    let goalEditFormGoalName         = $("#goalEditForm input[name='goalName']")
    let goalEditFormGoalStartDate    = $("#goalEditForm input[name='goalStartDate']")
    let goalEditFormGoalMeasureDate  = $("#goalEditForm input[name='goalMeasureDate']")
    let goalEditFormGoalBestCase     = $("#goalEditForm input[name='revenueGoalBestCase']")
    let goalEditFormGoalWorstCase    = $("#goalEditForm input[name='revenueGoalWorstCase']")

    goalCaptureForm.on('submit',     __captureGoal);
    goalEditForm.on('submit',        __updateEmployeeGoal);
    employeeList.on('change',        __loadEmployeeCurrentGoal);

    //private functions
    var onStringToTrue = function(val) {
        if (val === "on") return true; // parse on strings as true boolean
        return val;
    };

    //event binders
    employeeTypeDropDown.on('change',           __updateEmployeeTypeOption);
    updateEmployeeTypeOptions.on('change',      __updatedEmployeeTypeOption);
    inputListeners.on('blur',                   __capitaliseCharacters);
    registrationSectionNavigator.on('click',    __toggleRegisterComponents);
    viewRegisteredSpecialsNavigator.on('click', __toggleViewComponents);
    viewWorkHoursSection.on('click',            __toggleWorkingDayComponents);
    newEmployeeForm.on('submit',                __registerNewEmployee);
    editEmployeeForm.on('submit',               __updateEmployee);
    btnLoadWorkingDays.on('click',              __loadEmployeeWorkingDays);
    confirmWorkingDayButton.on('click',         __submitEmployeeWorkingDayDetails);

    //event handlers
    function __navigateView(viewName) {
        if(viewName === 'employees') {
            companyAnalyticsView.show();
            companyGoalsView.hide();
            btnAnalytics.hide();
            btnGoals.show();
        } else if(viewName === 'goals') {
            companyAnalyticsView.hide();
            companyGoalsView.show();
            btnAnalytics.show();
            btnGoals.hide();
            __loadEmployeeCurrentGoal();
        }
    }

    function __addEmployeeGoal() {
        captureGoalModal.modal('show');
    }

    function __captureGoal(e) {
        e.preventDefault();

        var form = document.getElementById('goalCaptureForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            goalCaptureForm.addClass('was-validated');
        } else {
            let goalCapture = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });
            goalCapture.employeeId = employeeList.val();

            miniLoader.show();

            $.ajax({
                url : baseGoalUrl,
                type: 'POST',
                data: JSON.stringify(goalCapture),
                contentType: "application/json;charset=utf-8",
                dataType:"json",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function () {
                toastr["success"]("We have successfully added the goal.", "Success");
                goalCaptureForm[0].reset();
                __closeModal();
            }).fail(function(response){
                if(response.status !== 201) {
                    __handleAjaxError(response);
                }
                __closeModal();
                miniLoader.hide();
            }).always(function(){
                __closeModal();
                miniLoader.hide();
                __loadEmployeeCurrentGoal();
            });
        }
    }

    function __editEmployeeGoal() {
        __loadEmployeeSpecificGoal();
    }

    function __updateEmployeeGoal(e) {
        e.preventDefault();

        var form = document.getElementById('goalEditForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            goalCaptureForm.addClass('was-validated');
        } else {
            let goalCapture = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });
            goalCapture.employeeId = employeeList.val();

            miniLoader.show();

            $.ajax({
                url : baseGoalUrl,
                type: 'PUT',
                data: JSON.stringify(goalCapture),
                contentType: "application/json;charset=utf-8",
                dataType:"json",
                beforeSend: function (xhr) {
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function (response) {
                toastr["success"](response.message, "Success");
                goalEditForm[0].reset();
            }).fail(function(response){
                if(response.status !== 200) {
                    __handleAjaxError(response)
                }
            }).always(function(){
                __loadEmployeeCurrentGoal();
                editGoalModal.modal('hide');
                miniLoader.hide();
            });
        }
    }

    function __closeModal() {
        goalCaptureForm[0].reset();
        captureGoalModal.modal('hide');
        goalEditForm[0].reset();
        editGoalModal.modal('hide');
    }

    function __loadAllDropdownEmployees() {
        miniLoader.show();
        $.ajax({
            url : baseGoalUrl + '/employees',
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            let html = '';

            _.each(response, function(employee) {
                if(employee.active) {
                    html += '<option value=' + employee.userId + '>' + employee.firstName + " " +  employee.lastName + '</option>';
                }
            });

            employeeList.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __loadEmployeeGoals() {
        $.ajax({
            url : baseGoalUrl + '/' +  employeeList.val(),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {

            if(response.length > 0) {
                let html = '';
                _.each(response, function(goal) {
                    html += '<tr>';
                    html += '<td>1</td>';
                    html += '<td>' + goal.measureDate + '</td>';
                    html += '<td>R ' + goal.revenueGoal + '</td>';
                    html += '<td>R ' + goal.revenueActual + '</td>';
                    html += '<td>R ' + goal.revenueDifference + '</td>';
                    html += '</tr>';
                });

                pastGoalsResults.empty().append(html);
            } else {
                pastGoalsResults.empty();
            }
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __loadEmployeeCurrentGoal() {
        miniLoader.show();
        $.ajax({
            url : baseGoalUrl + '/' +  employeeList.val() + '/current',
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            currentGoal = response;
            currentGoalCard.show();
            currentGoalName.empty().append(response.employeeName);
            currentGoalMeasure.empty().append(response.goalName + '</br> Measure By: ' + response.measureDate);
            currentGoalRevenue.empty().append('Revenue Goal: R ' +  response.revenueGoal);

            pastGoalCard.removeClass('col-sm-12');
            pastGoalCard.addClass('col-sm-9');

            if(response.revenueGoalProgress < 25) {
                currentProgress.empty().append(
                    '<div class="progress-bar bg-danger" role="progressbar" style="width: '+response.revenueGoalProgress+'%;" aria-valuenow="'+response.revenueGoalProgress+'" aria-valuemin="0" aria-valuemax="100">'+response.revenueGoalProgress+'%</div>'
                );
            } else if(response.revenueGoalProgress >= 25 && response.revenueGoalProgress < 50) {
                currentProgress.empty().append(
                    '<div class="progress-bar bg-warning" role="progressbar" style="width: '+response.revenueGoalProgress+'%;" aria-valuenow="'+response.revenueGoalProgress+'" aria-valuemin="0" aria-valuemax="100">'+response.revenueGoalProgress+'%</div>'
                );
            } else if(response.revenueGoalProgress >= 50 && response.revenueGoalProgress < 99) {
                currentProgress.empty().append(
                    '<div class="progress-bar bg-info" role="progressbar" style="width: '+response.revenueGoalProgress+'%;" aria-valuenow="'+response.revenueGoalProgress+'" aria-valuemin="0" aria-valuemax="100">'+response.revenueGoalProgress+'%</div>'
                );
            } else if(response.revenueGoalProgress >= 100) {
                currentProgress.empty().append(
                    '<div class="progress-bar bg-success" role="progressbar" style="width: '+response.revenueGoalProgress+'%;" aria-valuenow="'+response.revenueGoalProgress+'" aria-valuemin="0" aria-valuemax="100">'+response.revenueGoalProgress+'%</div>'
                );
            }

        }).fail(function(response) {
            // hide
            pastGoalCard.removeClass('col-sm-9');
            pastGoalCard.addClass('col-sm-12');
            currentGoalCard.hide();
            currentGoalName.empty();
            currentGoalMeasure.empty();
            currentGoalRevenue.empty();
            currentProgress.empty();
            __handleAjaxError(response)
        }).always(function () {
            __loadEmployeeGoals();
        })
    }

    function __loadEmployeeSpecificGoal() {
        $.ajax({
            url : baseGoalUrl + '/' + currentGoal.goalId + '/' + employeeList.val(),
            type: 'GET',
            beforeSend: function (xhr) {
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            // load into form
            goalEditFormGoalId.val(currentGoal.goalId);
            goalEditFormGoalName.val(response.goalName);
            goalEditFormGoalStartDate.val(response.goalStartDate);
            goalEditFormGoalMeasureDate.val(response.goalMeasureDate);
            goalEditFormGoalBestCase.val(response.revenueGoalBestCase);
            goalEditFormGoalWorstCase.val(response.revenueGoalWorstCase);
            editGoalModal.modal('show');
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __updateEmployeeTypeOption() {
        var selectedDropDownValue = $(this).val();
        employeeType.val(selectedDropDownValue);
    }

    function __updatedEmployeeTypeOption() {
        var selectedDropDownValue = $(this).val();
        updateEmployeeType.val(selectedDropDownValue);
    }

    function __toggleRegisterComponents() {
        listEmployeesView.hide();
        registerEmployeesView.show();
        editEmployeeView.hide();
        newEmployeeForm[0].reset();
        employeeLists.empty();
    }

    function __toggleViewComponents() {
        listEmployeesView.show();
        registerEmployeesView.hide();
        editEmployeeView.hide();
        manageWorkingDaysView.hide();
        __loadAllEmployees();
    }

    function __toggleWorkingDayComponents() {
        listEmployeesView.hide();
        registerEmployeesView.hide();
        editEmployeeView.hide();
        manageWorkingDaysView.show();
        __loadWorkingYears();
    }

    function __loadWorkingYears() {
        daysInMonthTable.empty();

        var html = '';
        var startingYear = moment().year();
        for(i = 0; i <= 20; i++) {
            if(i === 0) { //current year
                html += '<option value="'+(startingYear+i)+'" selected>'+(startingYear+i)+'</option>';
            } else {
                html += '<option value="'+(startingYear+i)+'">'+(startingYear+i)+'</option>';
            }
        }

        workingYearSelect.empty().append(html);
    }

    function __capitaliseCharacters() {
        var textValue = $(this).val();
        $(this).val(textValue.substr(0,1).toUpperCase()+textValue.substr(1).toLowerCase());
    }

    //private functions
    function __loadEmployeeWorkingDays() {
        dataLoader.show();
        var selectedMonth = workingMonthSelect.val();
        var selectedYear = workingYearSelect.val();
        var employeeId = userId.val();

        var daysInCurrentMonth = moment(selectedYear +"-"+selectedMonth, "YYYY-MM").daysInMonth();

        var currentMonthStartWeekDay = moment(selectedYear +"-"+selectedMonth, "YYYY-MM").startOf('month').weekday();

        setTimeout(function(){
            $.ajax({
                url: '/api/auth/employees/working-day/'+employeeId +'/'+ selectedMonth +'/'+ selectedYear,
                beforeSend: function (xhr){
                    xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                }
            }).done(function(response) {

                var html = '<tr>';
                var currentDateInMonth = 1;
                for(i = 1; i <= (daysInCurrentMonth + currentMonthStartWeekDay); i++) {

                    if(i <= currentMonthStartWeekDay) { // disable this cell because its part of the old month
                        html += '<td style="background-color: grey"></td>';
                    } else {

                        var workingDay = isAWorkingDay(response, currentDateInMonth);
                        if( workingDay !== null ) {

                            html += '<td style="padding: 10px">';
                                html += '<span class="dayInMonth">'+(currentDateInMonth++)+'</span>';
                                html += '<div class="workingDay" style="background-color: #0dc5c1; z-index: 100; padding: 5px; border-radius: 10px; cursor: pointer" onclick=employeeModule.loadWorkingDayDetails('+JSON.stringify(workingDay)+')>Working Day</div>';
                            html += '</td>';
                        } else {
                            // here we need to figure out what the current date is to display
                            html += '<td style="padding: 10px" onclick="employeeModule.showEmployeeWorkDayModal('+currentDateInMonth+')"><span class="dayInMonth">'+(currentDateInMonth++)+'</span></td>';
                        }


                    }

                    if(i % 7 === 0 && i !== 1) { // create new row every time
                        html += '</tr><tr>'
                    }
                }
                daysInMonthTable.empty().append(html + '</tr>');

                dataLoader.hide();
            }).fail(function(response){
                __handleAjaxError(response)
            });
        }, 2000);
    }

    function isAWorkingDay(workingDays, dayToCheck) {
        var isWorkingDay = null;
        for(j = 0; j < workingDays.length; j++) {
            if (moment(workingDays[j].workingDayStartTime).format("D") == dayToCheck) {
                isWorkingDay = workingDays[j];
                break;
            }
        }

        return isWorkingDay;
    }

    function __loadWorkingDayDetails(employeeWorkingDay) {
        var html = '<div class="col-12">';

            html += '<div><b>START TIME</b></div>';
            html += '<div>'+moment(employeeWorkingDay.workingDayStartTime).local().format("DD/MM/YYYY HH:mm")+'</div>';

            html += '<div><b>LUNCH TIME ('+employeeWorkingDay.workingDayLunchDuration+' minutes)</b></div>';
            html += '<div>'+moment(employeeWorkingDay.workingDayLunchStartTime).local().format("DD/MM/YYYY HH:mm")+'</div>';

            html += '<div><b>END TIME</b></div>';
            html += '<div>'+moment(employeeWorkingDay.workingDayEndTime).local().format("DD/MM/YYYY HH:mm")+'</div>';

            html += '</div>';

        allocatedEmployeeWorkDayModal.find('.modal-body > .row').empty().append(html);

        var buttonHtml = '<button type="button" class="btn btn-danger col-sm-6" onclick=employeeModule.removeWorkingDay("'+employeeWorkingDay.employeeWorkingDayId+'")>REMOVE</button>';
        buttonHtml += '<button type="button" class="btn btn-dark col-sm-6" data-dismiss="modal">CLOSE</button>';

        removeWorkingDayButton.empty().append(buttonHtml);
        allocatedEmployeeWorkDayModal.modal('show');
    }

    function __removeWorkingDay(employeeWorkingDayId) {
        removeWorkingDayButton.hide();
        $.ajax({
            url: '/api/auth/employees/working-day/'+userId.val()+'/'+employeeWorkingDayId,
            type: 'DELETE',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response) {
            // reset stuff
            toastr["success"](response.message, "Success");
            allocatedEmployeeWorkDayModal.modal('hide');
            __loadEmployeeWorkingDays();
            removeWorkingDayButton.show();
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __showEmployeeWorkDayModal(currentDateInMonth) {
        currentDateInMonthToAllocate = currentDateInMonth;
        selectEmployeeWorkDayModal.modal('show');
    }

    function __submitEmployeeWorkingDayDetails() {
        var _selectedMonth = workingMonthSelect.val();
        var _selectedYear = workingYearSelect.val();
        var _employeeId = userId.val();
        var _employeeWorkingDayStartTime = employeeWorkingDayStartTime.val();
        var _employeeWorkingDayEndTime = employeeWorkingDayEndTime.val();
        var _employeeWorkingDayLunchStartTime = employeeWorkingDayLunchStartTime.val();
        var _employeeWorkingDayLunchDuration = employeeWorkingDayLunchDuration.val();
        
        var employeeWorkingDay = {
            employeeWorkingId: _employeeId,
            employeeWorkingDayStartTime: moment(_selectedYear +"-"+_selectedMonth+"-"+currentDateInMonthToAllocate + " " + _employeeWorkingDayStartTime, "YYYY-MM-DD HH:mm").valueOf(),
            employeeWorkingDayEndTime: moment(_selectedYear +"-"+_selectedMonth+"-"+currentDateInMonthToAllocate + " " + _employeeWorkingDayEndTime, "YYYY-MM-DD HH:mm").valueOf(),
            employeeWorkingDayLunchStartTime: moment(_selectedYear +"-"+_selectedMonth+"-"+currentDateInMonthToAllocate + " " + _employeeWorkingDayLunchStartTime, "YYYY-MM-DD HH:mm").valueOf(),
            employeeWorkingDayLunchDuration: parseInt(_employeeWorkingDayLunchDuration),
            applyToWholeWeek: (applyToWholeWeekOption.val() == 'true')
        };

        $.ajax({
            url: '/api/auth/employees/working-day',
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            data: JSON.stringify(employeeWorkingDay),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response) {
            // reset stuff
            toastr["success"](response.message, "Success");
            currentDateInMonthToAllocate = 0;
            selectEmployeeWorkDayModal.modal('hide');
            __loadEmployeeWorkingDays();
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __loadEmployeeType() {
        $.ajax({
            url: '/api/auth/employees/employee-types',
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

    function __loadEmployeeTypeForEditing() {
        $.ajax({
            url: '/api/auth/employees/employee-types',
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

            updateEmployeeTypeOptions.empty().append(html);

        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __loadUserPermissions() {
        $.ajax({
            url: '/api/auth/employees/permissions',
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response) {
            var html = "";
            if (Array.isArray(response)) {

                for (var i = 0; i < response.length; i++) {
                    html += '<div class="col-12 col-sm-4"><div class="checkbox"><label class="permissionLabel">'+response[i].permissionFeature+'</label> <br>';
                        html += '<label><input type="checkbox" name="userPermissions['+response[i].permissionFeature.toLowerCase()+'][canRead]"> Can Read</label><br>';
                        html += '<label><input type="checkbox" name="userPermissions['+response[i].permissionFeature.toLowerCase()+'][canWrite]"> Can Write</label>';
                    html += "</div></div>";
                }
            }
            $(".permissions-list").empty().append(html);

        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __loadAllEmployees() {
        miniLoader.show();
        $.ajax({
            url : "/api/auth/employees",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            var html = "";

            _.each(response, function(employee) {
                if(employee.active) {
                    html += '<a href="#" class="list-group-item list-group-item-action">';
                } else {
                    html += '<a href="#" class="list-group-item list-group-item-action list-group-item-danger">';
                }

                html += '<div class="row">';
                    html += '<div class="col-12 col-sm-8">';
                        html +=  employee.firstName + " " +  employee.lastName + " <span style='font-weight: bold'>("+ employee.emailAddress+")</span>";
                    html += '</div>';

                    html += '<div class="col-12 col-sm-4">';
                        if(employee.active) {
                            html += '<button class="btn btn-primary btn-block" data-employeeid=' + employee.userId + ' onclick="employeeModule.editEmployee(this)">Edit</button>';
                            html += '<button class="btn btn-danger btn-block" data-employeeid=' + employee.userId + ' onclick="employeeModule.disableEmployee(this)">Disable</button>';
                        } else {
                            html += '<button class="btn btn-success btn-block" data-employeeid=' + employee.userId + ' onclick="employeeModule.enableEmployee(this)">Enable</button>';
                        }
                    html += '</div>';
                html += '</div>';

                html += '</a>';
            });

            employeeLists.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __disableEmployee(btn){
        $.ajax({
            url : "/api/auth/employees/disable/"+btn.dataset.employeeid,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (data) {
            var response = data;
            if(response.success){
                toastr["success"](response.message, "Operation Success");
            }else{
                toastr["error"](response.message, "Operation Error");
            }

            __loadAllEmployees();
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __enableEmployee(btn){
        $.ajax({
            url : "/api/auth/employees/enable/"+btn.dataset.employeeid,
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (data) {
            var response = data;
            if(response.success){
                toastr["success"](response.message, "Operation Success");
            }else{
                toastr["error"](response.message, "Operation Error");
            }

            __loadAllEmployees();
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __editEmployee(btn){
        __loadEmployeeTypeForEditing();
        listEmployeesView.hide();
        registerEmployeesView.hide();
        editEmployeeView.show();

        $.ajax({
            url : "/api/auth/employees/"+btn.dataset.employeeid,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            userId.val(response.userId);
            updateInputName.val(response.firstName);
            updateInputSurname.val(response.lastName);
            updateInputEmail.val(response.contactDetails.emailAddress);
            updateMobileNumber.val(response.contactDetails.mobileNumber);
            updateEmployeeType.val(response.employeeType);
            if(response.mustBookConsultationFirstTime)
                updateMustBookConsultationFirstTime.prop('checked',true);
            else
                updateMustBookConsultationFirstTime.prop('checked',false);
            setTimeout(function () {
                updateEmployeeLevel.val(response.employeeLevel);
                updateEmployeeTypeOptions.val(response.employeeType);
            }, 600);

            var html = "";

            _.each(response.userPermissionList, function(userPermission) {
                html += '<div class="col-12 col-sm-4"><div class="checkbox"><label class="permissionLabel">'+userPermission.permissionFeature+'</label> <br>';
                if(userPermission.canRead)
                    html += '<label><input type="checkbox" name="userPermissions['+userPermission.permissionFeature.toLowerCase()+'][canRead]" checked> Can Read</label><br>';
                else
                    html += '<label><input type="checkbox" name="userPermissions['+userPermission.permissionFeature.toLowerCase()+'][canRead]"> Can Read</label><br>';

                if(userPermission.canWrite)
                    html += '<label><input type="checkbox" name="userPermissions['+userPermission.permissionFeature.toLowerCase()+'][canWrite]" checked> Can Write</label>';
                else
                    html += '<label><input type="checkbox" name="userPermissions['+userPermission.permissionFeature.toLowerCase()+'][canWrite]"> Can Write</label>';
                html += "</div></div>";
            });

            $(".edit-permission-list").empty().append(html);

        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __registerNewEmployee(e) {

        e.preventDefault();

        var form = document.getElementById('registerForm');
        if (form.checkValidity() === false) {
            e.preventDefault();
            e.stopPropagation();
            newEmployeeForm.addClass('was-validated');
        } else {
            var newEmployee = newEmployeeForm.serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });

            if(newEmployee !== undefined) {
                $.ajax({
                    url : "/api/auth/employees",
                    type: 'POST',
                    data: JSON.stringify(newEmployee),
                    contentType: "application/json;charset=utf-8",
                    dataType:"json",
                    beforeSend: function (xhr){
                        xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
                    }
                }).done(function (response) {
                    if(response.success) {
                        toastr["success"](response.message, "Operation Success");
                        listEmployeesView.show();
                        registerEmployeesView.hide();
                    } else {
                        toastr["error"](response.message, "Operation Error");
                    }

                    __loadAllEmployees();
                }).fail(function(response){
                    __handleAjaxError(response)
                });
            } else {
                toastr["error"]("Please ensure you enter all the valid information for the new employee.", "Error");
            }
        }
    }

    function __updateEmployee(e) {

        e.preventDefault();

        var updatedEmployee = $(this).serializeJSON({parseWithFunction: onStringToTrue, checkboxUncheckedValue: false });

        $.ajax({
            url : "/api/auth/employees",
            type: 'PUT',
            data: JSON.stringify(updatedEmployee),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");
            __toggleViewComponents();
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

        __loadAllEmployees();

        __loadEmployeeType();

        __loadUserPermissions();

        __loadAllDropdownEmployees();
    }

    //public functions
    return{
        initialiseModule        : __initialiseModule,
        editEmployee            : __editEmployee,
        disableEmployee         : __disableEmployee,
        enableEmployee          : __enableEmployee,
        showEmployeeWorkDayModal: __showEmployeeWorkDayModal,
        loadWorkingDayDetails   : __loadWorkingDayDetails,
        removeWorkingDay        : __removeWorkingDay,
        navigateView            : __navigateView,
        addEmployeeGoal         : __addEmployeeGoal,
        closeModal              : __closeModal,
        editEmployeeGoal        : __editEmployeeGoal
    }
})();







