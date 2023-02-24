var analyticsModule = (function(){
    var baseGoalUrl                  = "/api/auth/goals/company";
    var miniLoader                   = $(".mini-loader");
    //cache elements
    var topTreatmentsBookedContainer = $("#topTreatmentsBooked");
    var topClientsBookedContainer    = $("#topClientsBooked");
    // chart views
    var barChartContext              = document.getElementById("barChart").getContext("2d");
    var pieChartContext              = document.getElementById("bookingChart").getContext("2d");
    var lineChartContext             = document.getElementById("timeChart").getContext("2d");

    // grand totals views
    var totalClientsToDateCard       = $("#clientsToDate");
    var totalBookingsToDateCard      = $("#bookingsToDate");
    var totalWorkToDateCard          = $("#workDone");

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

    //private functions
    var onStringToTrue = function(val) {
        if (val === "on") return true; // parse on strings as true boolean
        return val;
    };

    //private functions
    function __navigateView(viewName) {
        if(viewName === 'analytics') {
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

    function __loadEmployeeGoals() {
        $.ajax({
            url : baseGoalUrl,
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
            url : baseGoalUrl + '/current',
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
            url : baseGoalUrl + '/' + currentGoal.goalId,
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

    function __fetchTopBookedTreatments(){
        $.ajax({
            url: "/api/auth/analytics/topBookedServiceItem",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response){
            var html = '';
            var counter = 0;
            var topBookings = response;
            for(var i = topBookings.length - 1; i >= 0 ; i--){
                html += '<li class="list-group-item d-flex">';
                html += '<div class="col-10">';
                html += '<b>'+ ++counter + '</b>) ' + topBookings[i].treatmentName;
                html += '</div>';
                html += '<div class="col-2">';
                html += '<span class="badge badge-success badge-pill float-right">'+topBookings[i].count+'</span>';
                html += '</div>';
                html += '</li>';
            }
            topTreatmentsBookedContainer.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __fetchTopBookedClients(){
        $.ajax({
            url: "/api/auth/analytics/topBookedClient",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(response){
            var html = '';
            var counter = 0;
            var topClients = response;
            for(var i = topClients.length - 1; i >= 0 ; i--){
                html += '<li class="list-group-item d-flex">';
                html += '<div class="col-10">';
                html += '<b>'+ ++counter + '</b>) ' + topClients[i].clientName;
                html += '</div>';
                html += '<div class="col-2">';
                html += '<span class="badge badge-success badge-pill float-right">'+topClients[i].count+'</span>';
                html += '</div>';
                html += '</li>';
            }
            topClientsBookedContainer.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        });
    }

    function __fetchTotalClientsToDate() {
        $.ajax({
            url: "/api/auth/analytics/totalClientsToDate",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            totalClientsToDateCard.append(JSON.parse(response).totalClients);
        }).fail(function (response) {
            __handleAjaxError(response);
        });
    }

    function __fetchTotalBookingsToDate() {
        $.ajax({
            url: "/api/auth/analytics/totalBookingsToDate",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            totalBookingsToDateCard.append(JSON.parse(response).totalBookings);
        }).fail(function (response) {
            __handleAjaxError(response);
        });
    }

    function __fetchTotalWorkToDate() {
        $.ajax({
            url: "/api/auth/analytics/totalWorkedToDate",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            totalWorkToDateCard.append(JSON.parse(response).totalWorkDone);
        }).fail(function (response) {
            __handleAjaxError(response);
        });
    }

    function __fetchNewSignupsChartData() {
        $.ajax({
            url: "/api/auth/analytics/signups",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            new Chart(barChartContext).Bar(JSON.parse(response));
        }).fail(function (response) {
            __handleAjaxError(response);
        });
    }

    function __fetchBookingsChartData() {
        $.ajax({
            url: "/api/auth/analytics/bookings",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            new Chart(pieChartContext).Pie(JSON.parse(response));
        }).fail(function (response) {
            __handleAjaxError(response);
        });
    }

    function __fetchTimeWorkedChartData() {
        $.ajax({
            url: "/api/auth/analytics/timeWorked",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            new Chart(lineChartContext).Line(JSON.parse(response));
        }).fail(function (response) {
            __handleAjaxError(response);
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
        //Global Chart Properties
        Chart.defaults.global.responsive = true;

        // load totals
        __fetchTotalClientsToDate();
        __fetchTotalBookingsToDate();
        __fetchTotalWorkToDate();

        // load charts
        setTimeout(function () {
            __fetchNewSignupsChartData();
            __fetchBookingsChartData();
            __fetchTimeWorkedChartData();
        }, 500);

        // load insights
        setTimeout(function () {
            __fetchTopBookedTreatments();
            __fetchTopBookedClients();
        }, 1500);
    }

    //public functions
    return{
        initialiseModule        : __initialiseModule,
        navigateView            : __navigateView,
        addEmployeeGoal         : __addEmployeeGoal,
        closeModal              : __closeModal,
        editEmployeeGoal        : __editEmployeeGoal
    }
})();
