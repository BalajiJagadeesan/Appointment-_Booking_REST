/**
 * Created by Balaji Jagadeesan on 03/17/2017.
 */

$(document).ready(function() {
    $('select').material_select();

    $(".button-collapse").sideNav();

    $('.datepicker').pickadate({
        selectMonths: true, // Creates a dropdown to control month
        selectYears: 15 // Creates a dropdown of 15 years to control year
    });

    $('.timepicker').pickatime({
        default: 'now',
        twelvehour: false, // change to 12 hour AM/PM clock from 24 hour
        donetext: 'OK',
        autoclose: false,
        vibrate: true // vibrate the device when dragging clock hand
    });
    $('#addfield').click(function(){
        $('#addinputs').append("<input id='labtest' name='labtest[]' type='text' class='validate' placeholder='testcode/dxcode'> <label for='labtest'></label>");
    });
});
