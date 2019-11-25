var timestamps = [];
var timeintraffic = [];

$( document ).ready(function() {
    //console.log( "ready!" );

    $.ajax({
        type: 'GET',
        dataType: "json",
        contentType: "application/json",
        async: true,
        url: "/route",
        data: {dest: "szell_kalman"},
        success: function(json) {
            //console.log(json);

            for(var i = 0; i < json.length; i++) {
                var entry = json[i];
                //console.log(entry.inTraffic);
                timestamps.push(entry.time.substring(11,16));
                timeintraffic.push(entry.inTraffic);
            }

            initChart();
        },
        error: function(e) {
            console.log( "ajax error!" );
            console.log(e.message);
        }
    })
});

function initChart() {
    var ctx = document.getElementById('myChart').getContext('2d');
    var myChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: timestamps,
            datasets: [{
                label: 'telki-szell_kalman',
                data: timeintraffic,

            }]
        },

    });
}
