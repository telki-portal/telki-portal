/**
 * ON WINDOW LOAD
 */
$(document).ready(function () {
    chartInitialized = false;

    requestChartDataset("szell_kalman");
    requestChartDataset("petofi_hid_budai");

});

/**
 * Global variables
 */
var config = {
    type: 'line',
    data: {
        labels: [],
        datasets: []
    }
};
var chartInitialized;
var myChart;

function requestChartDataset(dest) {
    $.ajax({
        type: 'GET',
        dataType: "json",
        contentType: "application/json",
        async: true,
        url: "/route",
        data: {dest: dest},
        success: function (json) {

            var newDataset = {
                label: 'telki-' + dest,
                data: [],
                borderColor: '#'+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6),
                fill: true
            };

            var timestamps = [];
            for (var i = 0; i < json.length; i++) {
                var entry = json[i];
                timestamps.push(entry.time.substring(11, 16));
                newDataset.data.push(entry.inTraffic);
            }

            config.data.labels = timestamps;
            config.data.datasets.push(newDataset);
            updateChart();
        },
        error: function (e) {
            console.log("ajax error!");
            console.log(e.message);
        }
    });
}

function initChart() {
    var ctx = document.getElementById('myChart').getContext('2d');
    myChart = new Chart(ctx, config);
}

function updateChart() {
    if (!chartInitialized) {
        initChart();
        chartInitialized=true;
    } else {
       myChart.update();
    }
}

