$(function(){

    // Init calendar for input of start day and time of reservation
    $('.date-timepicker-start').datetimepicker({
        format: 'd.m.Y H:i',
        todayButton: false,
        scrollMonth: false,
        scrollInput: false,
        onShow: function(ct, $input){
            let formClass = $input.parents("form").attr('class');
            var dateTimepickerFinish =  $('.' + formClass + ' .date-timepicker-finish').val();
            if (dateTimepickerFinish) {
                var minDateTime = moment(dateTimepickerFinish, "DD.MM.YYYY HH:mm");
                var maxDateTime = moment(dateTimepickerFinish, "DD.MM.YYYY HH:mm");
                minDateTime.subtract(24, 'hours');
                maxDateTime.subtract(30, 'minutes');
            }
            this.setOptions({
                minDateTime: dateTimepickerFinish ? minDateTime.format('DD.MM.YYYY HH:mm') : false,
                maxDateTime:dateTimepickerFinish ? maxDateTime.format('DD.MM.YYYY HH:mm') : false,
            })
        },
        timepicker: true
    });

    // Init calendar for input of finish day and time of reservation
    $('.date-timepicker-finish').datetimepicker({
        format: 'd.m.Y H:i',
        todayButton: false,
        scrollMonth: false,
        scrollInput: false,
        onShow: function(ct, $input){
            let formClass = $input.parents("form").attr('class');
            var dateTimepickerStart =  $('.' + formClass + ' .date-timepicker-start').val();
            if (dateTimepickerStart) {
                var minDateTime = moment(dateTimepickerStart, "DD.MM.YYYY HH:mm");
                var maxDateTime = moment(dateTimepickerStart, "DD.MM.YYYY HH:mm");
                minDateTime.add(30, 'minutes');
                maxDateTime.add(24, 'hours');
                maxDateTime.add(15, 'minutes');
            }
            this.setOptions({
                minDateTime: dateTimepickerStart ? minDateTime.format('DD.MM.YYYY HH:mm') : false,
                maxDateTime: dateTimepickerStart ? maxDateTime.format('DD.MM.YYYY HH:mm') : false,
            })
        },
        timepicker: true
    });

    // Show modal window with info of selected reservation
    $(".calendar-reservation").on('click', function (event) {
        event.preventDefault();
        $.get($(this).data("url"), function (reservation, status) {
            let dataStart = new Date(reservation.dateStart).toLocaleString().slice(0, -3).replace(',', '')
            let dataFinish = new Date(reservation.dateFinish).toLocaleString().slice(0, -3).replace(',', '')

            $(".event-room").text(reservation.meetingRoom.name);
            $(".event-begin").text(dataStart);
            $(".event-end").text(dataFinish);
            $(".event-owner").text(reservation.user.username);
            $(".event-desc").text(reservation.descriptions);
            $(".confirmation-delete").data("id", reservation.id);

            // fill reservation update form
            $("#reservationId").val(reservation.id);
            $("#editUser").val(reservation.user.id);
            $("#editMeetingRoom").val(reservation.meetingRoom.id);
            $('#update-event .date-timepicker-start').val(dataStart);
            $('#update-event .date-timepicker-finish').val(dataFinish);
            $("#editDescription").val(reservation.descriptions);

            if ($("#view-event").data("current-user") == reservation.user.id) {
                $("#view-event .event-delete, #view-event .event-edit").show();
            } else {
                $("#view-event .event-delete, #view-event .event-edit").hide();
            }
        });
        $("#view-event").modal();
    });

    // Show modal window with confirmation of reservation deletion
    $(".event-delete").on('click', function (event) {
        event.preventDefault();
        $("#remove-event .modal-body").height($("#view-event .modal-body").height());
        $("#remove-event").modal();
    });

    // Processing of form removal of reservation
    $(".confirmation-delete").on('submit', function (event) {
        event.preventDefault();
        let url = $(this).attr("action") + $(this).data("id");
        $.ajax({
            url: url,
            type: "DELETE",
            data: $(this).serialize(),
            success: function(result) {
                $(".reservation_" + result).hide();
                $("#remove-event").modal('hide');
                $("#view-event").modal('hide');
            },
            error: function (e) {
                console.log(e);
            }
        });
    });

    // Processing of form add/edit reservation
    $(".form-add-reservation, .form-update-reservation").on('submit', function (event) {
        event.preventDefault();

        const formClass = $(this).attr("class");

        let error = false;
        if (!$("." + formClass + " .date-timepicker-start").val()) {
            $("." + formClass + " .date-timepicker-start").addClass("is-invalid");
            error = true;
        }
        if (!$("." + formClass + " .date-timepicker-finish").val()) {
            $("." + formClass + " .date-timepicker-finish").addClass("is-invalid");
            error = true;
        }
        if (error) {
            return;
        }

        let url = $(this).attr("action");
        $.ajax({
            url: url,
            type: "POST",
            data: $(this).serialize(),
            success: function(result) {
                location.reload();
            },
            error: function (e) {
                console.log(e);
                $("." + formClass + " .form-field-error").text(e.responseText);
            }
        });
    });

    // Show modal window to update selected reservation
    $(".event-edit").on('click', function (event) {
        event.preventDefault();
        $("#update-event").modal();
    });

    // Show modal window to send reservation to google calendar
    $(".confirmation-google-send").on('submit', function (event) {
        event.preventDefault();
        let url = $(this).attr("action");
        $(".google_event_warning").addClass("d-none");
        $(".google_event_spinner").removeClass("d-none");
        $.ajax({
            url: url,
            type: "POST",
            data: $(this).serialize(),
            success: function(result) {
                console.log(result);
                $(".google_event_spinner").addClass("d-none");
                if (result == "success") {
                    $(".google_event_success").removeClass("d-none");
                } else {
                    $(".google_event_success_text").text(result).removeClass("d-none");
                }
            },
            error: function (e) {
                console.log(e);
                $(".google_event_error").text(e.responseText);
                $(".google_event_spinner").addClass("d-none");
                $(".google_event_error").removeClass("d-none");
            }
        });
    });

});