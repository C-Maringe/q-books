var marketingModule = (function () {
    var miniLoader          = $(".mini-loader");
    var viewAllCampaigns    = $("#viewAllCampaigns");
    var viewAllClients      = $("#viewAllClients");
    var setupCampaignEmail  = $("#setupCampaignEmail");

    var campaignList        = $("#campaignList");
    var clientList          = $("#clientList");
    var checkToSelectAll    = $("#checkToSelectAll");

    var batchEmailId        = $("#batchEmailId");
    var emailTitle          = $("#emailTitle");
    var emailContent        = $("#emailContent");
    var batchEmailIdToResend= $("#batchEmailIdToResend");

    var sendCampaignButton  = $("#sendCampaignButton");
    var saveCampaignContent = $("#saveCampaignContent");

    // breadcrumbs
    var viewAllMarketingEmails = $(".allMarketingEmails");
    var viewAllClientBaseForSpecificBatchEmail = $(".selectClientBase");

    var viewApplication        = $("#body");

    checkToSelectAll.on('click', __preSelectAllClients);
    viewAllMarketingEmails.on('click', __navigateBackToViewAllMarketingEmails);
    viewAllClientBaseForSpecificBatchEmail.on('click', __navigateBackToViewAllClients);

    function __navigateBackToViewAllMarketingEmails() {
        viewApplication.empty().append(render('marketing','index',{})); // this sets our default view to display when login happens
    }

    function __navigateBackToViewAllClients() {
        miniLoader.show();
        viewAllClients.show();
        setupCampaignEmail.hide();

        $.ajax({
            url : "/api/auth/marketing/batchEmail/"+$("#batchEmailId").val(),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            if(response.resending) {
                batchEmailIdToResend.val(response.batchEmailId);
                // change views
                viewAllCampaigns.hide();
                viewAllClients.show();
            }

            var html = "";
            _.each(response.clients ,function(client) {
                html += '<a href="#" class="list-group-item list-group-item-action">';
                html += '<div class="row">';

                html += '<div class="col-10">';
                html +=  client.fullName + "("+ client.emailAddress +")";
                html += '</div>';
                html += '<div class="col-2">';
                if(response.resending) {
                    if(client.existsInList) {
                        html += '<input data-userid='+client.userId+' type="checkbox" class="form-control clientToSendTo" checked="true"/>';
                    } else {
                        html += '<input data-userid='+client.userId+' type="checkbox" class="form-control clientToSendTo"/>';
                    }
                } else {
                    html += '<input data-userid='+client.userId+' type="checkbox" class="form-control clientToSendTo"/>';
                }
                html += '</div>';

                html += '</div>';
                html += '</a>';
            });
            clientList.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __preSelectAllClients(e) {
        if(e.target.checked === true) {
            $(".clientToSendTo").attr('checked', true);
        } else {
            $(".clientToSendTo").attr('checked', false);
        }
    }

    function __initialiseModule() {
        __loadAllCampaigns();
    }

    function __loadAllCampaigns(){
        miniLoader.show();
        $.ajax({
            url : "/api/auth/marketing/batchEmails",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (data) {
            var response = data;
            if(response.length === 0){
                toastr["success"]("No promotion campaigns were found, Start adding a few.", "Operation Success");
            } else {
                var html = '';

                _.each(response, function (batchEmail) {
                    if(batchEmail.batchEmailStatus === "SETUP")
                        html += '<a href="#" class="list-group-item list-group-item-action">';
                    else
                        html += '<a href="#" class="list-group-item list-group-item-action list-group-item-success">';

                    html += '<div class="row">';

                    html += '<div class="col-12 col-sm-8">';
                    if(batchEmail.batchEmailStatus === "SETUP")
                        html +=  batchEmail.batchEmailTitle + ' <span class="badge badge-info badge-pill">'+batchEmail.batchEmailActualSentTo+'</span>';
                    else
                        html +=  batchEmail.batchEmailTitle + ' <span class="badge badge-success badge-pill">Sent to '+batchEmail.batchEmailActualSentTo+'</span>';

                    html += '</div>';

                    html += '<div class="col-12 col-sm-4">';
                    if(batchEmail.batchEmailStatus !== "COMPLETED") {
                        html += '<button class="btn btn-dark btn-block" data-campaignid=' + batchEmail.batchEmailId + ' onclick=marketingModule.loadClientBase(this)>Continue</button>';
                    }

                    html += '</div>';
                    html += '</div>';

                    html += '</a>';
                });


                campaignList.empty().append(html);
            }
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __setupClientBaseToEmail() {
        viewAllCampaigns.hide();
        viewAllClients.show();

        __loadClientBase();
    }

    function __setupEmail() {
        miniLoader.show();
        // get all checked clients
        var clientsArray = [];
        var clientsToIterate = clientList.children().find('.clientToSendTo');

        _.each(clientsToIterate, function (client) {
            if($(client).prop('checked') === true) {
                clientsArray.push(
                    $(client).data('userid')
                );
            }
        });

        var clients = {};
        clients.clientIds = clientsArray;
        if(batchEmailIdToResend.val() != '' && batchEmailIdToResend.val()) {
            clients.batchEmailId = batchEmailIdToResend.val();
        }

        var clientsToSendTo = JSON.stringify(clients);

        $.ajax({
            url         : "/api/auth/marketing/setupBatchList",
            type        : "POST",
            dataType    : "json",
            data        : clientsToSendTo,
            contentType : "application/json;charset=UTF-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function(results){
            console.log(clientsToSendTo)
            toastr["success"]("Your campaign email list was successfully setup.", "Success");
            // load next screen
            viewAllClients.hide();
            setupCampaignEmail.show();
            // load the response data
            batchEmailId.val(results.batchEmailId);
            if(results.resending) {
                emailTitle.val(results.batchEmailTitle);
                emailContent.text(results.batchEmailMessage);
            }

        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })

    }

    function __saveEmailContent() {
        miniLoader.show();

        // post to the server and then show send campaign button
        var emailContentToSave = {
            "batchEmailId" : batchEmailId.val(),
            "batchEmailTitle" : emailTitle.val(),
            "batchEmailMessage" : emailContent.val()
        };

        $.ajax({
            url : "/api/auth/marketing/setupBatchEmailContent",
            type: 'PUT',
            data: JSON.stringify(emailContentToSave),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function () {
            toastr["success"]("Your email content was successfully setup. Please proceed by clicking send button.", "Success");
            sendCampaignButton.show();
            saveCampaignContent.hide();
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __sendCampaign() {
        miniLoader.show();
        $.ajax({
            url : "/api/auth/marketing/sendBatchEmail/"+batchEmailId.val(),
            type: 'PUT',
            data: JSON.stringify({}),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function () {
            toastr["success"]("Your promotional campaign was successfully sent out. ", "Success");
            sendCampaignButton.hide();
            saveCampaignContent.show();

            viewAllCampaigns.show();
            setupCampaignEmail.hide();

            setTimeout(function () {
                __loadAllCampaigns();
            }, 550);
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __loadClientBase(resend){
        miniLoader.show();

        var url = "";
        if($(resend).data('campaignid')) {
            url = "/api/auth/marketing/batchEmail/"+$(resend).data('campaignid');
        } else {
            url = "/api/auth/marketing/clientBase";
        }
        $.ajax({
            url : url,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            console.log(url,response)
            if(response.resending) {
                batchEmailIdToResend.val(response.batchEmailId);
                // change views
                viewAllCampaigns.hide();
                viewAllClients.show();
            }

            var html = "";
            _.each(response.clients ,function(client) {
                html += '<a href="#" class="list-group-item list-group-item-action">';
                html += '<div class="row">';

                html += '<div class="col-10">';
                html +=  client.fullName + "("+ client.emailAddress +")";
                html += '</div>';
                html += '<div class="col-2">';
                if(response.resending) {
                    if(client.existsInList) {
                        html += '<input data-userid='+client.userId+' type="checkbox" class="form-control clientToSendTo" checked="true"/>';
                    } else {
                        html += '<input data-userid='+client.userId+' type="checkbox" class="form-control clientToSendTo"/>';
                    }
                } else {
                    html += '<input data-userid='+client.userId+' type="checkbox" class="form-control clientToSendTo"/>';
                }
                html += '</div>';

                html += '</div>';
                html += '</a>';
            });
            clientList.empty().append(html);
        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
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

    return {
        initialiseModule        : __initialiseModule,
        setupClientBaseToEmail  : __setupClientBaseToEmail,
        setupEmail              : __setupEmail,
        sendCampaign            : __sendCampaign,
        saveEmailContent        : __saveEmailContent,
        loadClientBase          : __loadClientBase
    }
})();