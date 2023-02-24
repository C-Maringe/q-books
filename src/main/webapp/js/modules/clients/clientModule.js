var clientModule = (function(){
    //cache elements
    var clientManagementBaseUrl         = "/api/auth/clients";
    var miniLoader                      = $(".mini-loader");

    var editClientSection               = $("#editClient");
    var viewClientSection               = $("#viewClient");
    var viewSection                     = $(".viewSection");

    var clientList                      = $("#clientList");
    var searchClientInput               = $("#searchClient");
    var originalClientListToSearch      = [];
    var clientListToSearch              = [];

    var clientNotesModal                = $("#clientSpecificNotesViewOption");
    var clientNotesView                 = $("#clientNotesView");
    var clientNotesEditView             = $("#clientNotesEditView");
    var editForm                        = $("#updateForm");
    var newNoteForm                     = $("#newNoteForm");
    var clientNewNoteOptionModal        = $("#clientNewNoteOption");
    // update inputs
    var updateInputName                 = $("#editFirstName");
    var updateInputSurname              = $("#editLastName");
    var userId                          = $("#editClientId");
    var updateInputEmail                = $("#editEmailAddress");
    var updateMobileNumber              = $("#editMobileNumber");
    var updateDateOfBirth               = $("#editDateOfBirth");

    // Loyalty Points
    var pointsAvailable                 = $(".pointsAvailable");
    var voucherResultsFound             = $("#voucherResultsFound");
    //event binders
    viewSection.on('click',                     __toggleViewComponents);
    searchClientInput.on('input',               __searchClients);
    editForm.on('submit',                       __updateClient);

    //event handlers
    function __toggleViewComponents() {
        viewClientSection.show();
        editClientSection.hide();
        __loadAllClients();
    }

    function __toggleEditComponents() {
        viewClientSection.hide();
        editClientSection.show();
    }

    //private functions
    function __loadAllClients() {
        miniLoader.show();
        $.ajax({
            url : clientManagementBaseUrl,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (clients) {
            originalClientListToSearch = clients;
            clientListToSearch = clients;

            __filterClientList();
            searchClientInput.val("");

        }).fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __searchClients() {
        var filterBy = $(this).val().toLowerCase();

        if(filterBy.length === 0) {
            clientListToSearch = originalClientListToSearch;
        } else {
            clientListToSearch = _.filter(originalClientListToSearch, function(client){
                var fullName = client.firstName + ' ' + client.lastName;
                return fullName.toLowerCase().indexOf(filterBy)  >= 0;
            });
        }

        __filterClientList();
    }

    function __filterClientList() {
        var html = "";

        _.each(clientListToSearch, function(client) {

            if(client.active) {
                html += '<a href="#" class="list-group-item list-group-item-action">';
            } else {
                html += '<a href="#" class="list-group-item list-group-item-action list-group-item-danger">';
            }

            html += '<div class="row">';
            html += '<div class="col-12 col-sm-8">';
            html +=  client.firstName + " " +  client.lastName + " <span style='font-weight: bold'>("+ client.emailAddress+")</span>";
            html += '</div>';

            html += '<div class="col-12 col-sm-4">';
            if(client.active) {
                html += '<button class="btn btn-primary btn-block" data-clientid=' + client.id + ' onclick="clientModule.editClient(this)">Edit</button>';
                html += '<button class="btn btn-info btn-block" data-clientid=' + client.id + ' onclick="clientModule.viewClientNotes(this)">Notes</button>';
                html += '<button class="btn btn-danger btn-block" data-clientid=' + client.id + ' onclick="clientModule.disableAccount(this)">Disable</button>';
            } else {
                html += '<button class="btn btn-success btn-block" data-clientid=' + client.id + ' onclick="clientModule.enableAccount(this)">Enable</button>';
            }
            html += '</div>';
            html += '</div>';

            html += '</a>';
        });

        clientList.empty().append(html);
    }

    function __disableAccount(element){
        miniLoader.show();
        $.ajax({
            url: clientManagementBaseUrl + "/" + element.dataset.clientid + "/disable",
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
        .done(function (response) {
            toastr["success"](response.message, "Success");
            __loadAllClients();
            __searchClients();
        })
        .fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __enableAccount(element){
        miniLoader.show();
        $.ajax({
            url: clientManagementBaseUrl + "/" + element.dataset.clientid + "/enable",
            type: 'PUT',
            contentType: "application/json;charset=utf-8",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
        .done(function (response) {
            toastr["success"](response.message, "Success");
            __loadAllClients();
            __searchClients();
        })
        .fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __viewClientNotes(element) {
        miniLoader.show();
        $.ajax({
            url: clientManagementBaseUrl + "/" + element.dataset.clientid,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
            .done(function (response) {
                toastr["success"](response.message, "Success");
                var html = "";

                _.each(response.notes, function(note) {
                    html += '<tr>';
                    html += '<td>'+note.title+'</td>';
                    html += '<td>'+note.description+'</td>';
                    html += '<td>'+note.createdByName+'</td>';
                    html += '<td>'+moment(new Date(note.dateCreated)).format("MM-DD-YYYY")+'</td>';
                    html += '</tr>'
                });

                clientNotesView.empty().append(html);

                clientNotesModal.modal('show');
            })
            .fail(function(response){
                __handleAjaxError(response)
            }).always(function () {
            miniLoader.hide();
        })
    }

    function __editClient(element) {
        miniLoader.show();
        $.ajax({
            url: clientManagementBaseUrl + "/" + element.dataset.clientid,
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
        .done(function (response) {
            userId.val(response.id);
            updateInputName.val(response.firstName);
            updateInputSurname.val(response.lastName);
            updateInputEmail.val(response.emailAddress);
            updateMobileNumber.val(response.mobileNumber);
            updateDateOfBirth.val(moment(response.dateOfBirth, "DD/MM/YYYY").format("YYYY/MM/DD"));

            var html = "";

            _.each(response.notes, function(note) {
                html += '<tr>';
                html += '<td>'+note.title+'</td>';
                html += '<td>'+note.description+'</td>';
                html += '<td>'+note.createdByName+'</td>';
                html += '<td>'+moment(new Date(note.dateCreated)).format("MM-DD-YYYY")+'</td>';
                html += '</tr>'
            });

            __loadProfileLoyaltyPoints(response);
            clientNotesEditView.empty().append(html);
            __toggleEditComponents();
        })
        .fail(function(response){
            __handleAjaxError(response)
        }).always(function () {
            miniLoader.hide();
        })
    }

    function __loadProfileLoyaltyPoints(response) {
        console.log(response)
        let html = '';
        _.each(response.vouchers,function (voucher) {
            html += '<tr>';
            html += '<td>' + voucher.voucherNumber + '</td>';
            html += '<td>' + voucher.valid + '</td>';
            html += '<td>' + moment(voucher.createdDate).format('YYYY/MM/DD HH:mm') + '</td>';
            html += '<td>' + moment(voucher.expiryDate).format('YYYY/MM/DD') + '</td>';
            if(voucher.redeemedDate) {
                html += '<td>' + moment(voucher.redeemedDate).format('YYYY/MM/DD') + '</td>';
            } else {
                html += '<td></td>';
            }

            html += '<td>' + voucher.redeemed + '</td>';
            html += '</tr>';
        })

        voucherResultsFound.empty().append(html);
        pointsAvailable.empty().append('Total points available ' + response.loyaltyPoints);
    }

    function __updateClient(e) {

        e.preventDefault();

        var updatedClient = $(this).serializeJSON();

        $.ajax({
            url : clientManagementBaseUrl + "/" + userId.val(),
            type: 'PUT',
            data: JSON.stringify(updatedClient),
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

    function __displayNewNoteModal() {
        newNoteForm[0].reset();
        clientNewNoteOptionModal.modal('show');
    }

    function __addClientNote() {
        var clientNote = newNoteForm.serializeJSON();

        $.ajax({
            url : clientManagementBaseUrl + "/" + userId.val() + "/notes",
            type: 'PUT',
            data: JSON.stringify(clientNote),
            contentType: "application/json;charset=utf-8",
            dataType:"json",
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        }).done(function (response) {
            toastr["success"](response.message, "Success");
            __refreshClientDetails();
            clientNewNoteOptionModal.modal('hide');
        }).fail(function(response){
            __handleAjaxError(response)
        });

    }

    function __refreshClientDetails() {
        miniLoader.show();
        $.ajax({
            url: clientManagementBaseUrl + "/" + userId.val(),
            beforeSend: function (xhr){
                xhr.setRequestHeader('Authorization', localStorage.getItem('token'));
            }
        })
            .done(function (response) {
                userId.val(response.id);
                updateInputName.val(response.firstName);
                updateInputSurname.val(response.lastName);
                updateInputEmail.val(response.emailAddress);
                updateMobileNumber.val(response.mobileNumber);
                updateDateOfBirth.val(moment(response.dateOfBirth, "DD/MM/YYYY").format("YYYY/MM/DD"));

                var html = "";

                _.each(response.notes, function(note) {
                    html += '<tr>';
                    html += '<td>'+note.title+'</td>';
                    html += '<td>'+note.description+'</td>';
                    html += '<td>'+note.createdByName+'</td>';
                    html += '<td>'+moment(new Date(note.dateCreated)).format("MM-DD-YYYY")+'</td>';
                    html += '</tr>'
                });

                clientNotesEditView.empty().append(html);
            })
            .fail(function(response){
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

        __loadAllClients();
    }

    //public functions
    return{
        initialiseModule    : __initialiseModule,
        enableAccount       : __enableAccount,
        disableAccount      : __disableAccount,
        viewClientNotes     : __viewClientNotes,
        editClient          : __editClient,
        displayNewNoteModal : __displayNewNoteModal,
        addClientNote       : __addClientNote
    }
})();







