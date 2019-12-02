/**
 * ON WINDOW LOAD
 */
$(document).ready(function () {
    firstChartInitialized = false;
    secondChartInitialized = false;
    dayOfYearChartInitialized = false;

    requestFirstChartDataset("telki_center", "szell_kalman");
    requestFirstChartDataset("telki_center", "petofi_hid_budai");

    requestSeondChartDataset("telki_center", "szell_kalman");
    requestSeondChartDataset("telki_center", "petofi_hid_budai");

    requestDayOfYearChartDataset("telki_center", "szell_kalman", year, dayOfYear);
    requestDayOfYearChartDataset("telki_center", "petofi_hid_budai", year, dayOfYear);

});

/**
 * Global variables
 */
var firstConfig = {
    type: 'line',
    data: {
        labels: [],
        datasets: []
    }
};
var firstChartInitialized;
var firstChart;

var secondConfig = {
    type: 'line',
    data: {
        labels: [],
        datasets: []
    }
};
var secondChartInitialized;
var secondChart;

var dayOfYearConfig = {
    type: 'line',
    data: {
        labels: [],
        datasets: []
    }
};

var dayOfYearChartInitialized;
var dayOfYearChart;

function requestFirstChartDataset(from, to) {
    $.ajax({
        type: 'GET',
        dataType: "json",
        contentType: "application/json",
        async: true,
        url: "/route",
        data: {from: from, to: to},
        success: function (json) {

            var newDataset = {
                label: from + '-' + to,
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

            firstConfig.data.labels = timestamps;
            firstConfig.data.datasets.push(newDataset);
            updateFirstChart();
        },
        error: function (e) {
            console.log("ajax error!");
            console.log(e.message);
        }
    });
}

function initFirstChart() {
    var ctx = document.getElementById('firstChart').getContext('2d');
    firstChart = new Chart(ctx, firstConfig);
}

function updateFirstChart() {
    if (!firstChartInitialized) {
        initFirstChart();
        firstChartInitialized=true;
    } else {
        firstChart.update();
    }
}

function requestSeondChartDataset(from, to) {
    $.ajax({
        type: 'GET',
        dataType: "json",
        contentType: "application/json",
        async: true,
        url: "/interval",
        data: {from: from, to: to},
        success: function (json) {

            var newDataset = {
                label: from + '-' + to,
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

            secondConfig.data.labels = timestamps;
            secondConfig.data.datasets.push(newDataset);
            updateSeondChart();
        },
        error: function (e) {
            console.log("ajax error!");
            console.log(e.message);
        }
    });
}

function initSecondChart() {
    var ctx = document.getElementById('secondChart').getContext('2d');
    secondChart = new Chart(ctx, secondConfig);
}

function updateSeondChart() {
    if (!secondChartInitialized) {
        initSecondChart();
        secondChartInitialized=true;
    } else {
        secondChart.update();
    }
}

function requestDayOfYearChartDataset(from, to) {
    $.ajax({
        type: 'GET',
        dataType: "json",
        contentType: "application/json",
        async: true,
        url: "/dayOfYear",
        data: {from: from, to: to, year: year, dayOfYear: dayOfYear},
        success: function (json) {

            var newDataset = {
                label: from + '-' + to,
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

            dayOfYearConfig.data.labels = timestamps;
            dayOfYearConfig.data.datasets.push(newDataset);
            updateDayOfYearChart();
        },
        error: function (e) {
            console.log("ajax error!");
            console.log(e.message);
        }
    });
}

function initDayOfYearChart() {
    var ctx = document.getElementById('dayOfYearChart').getContext('2d');
    dayOfYearChart = new Chart(ctx, dayOfYearConfig);
}

function updateDayOfYearChart() {
    if (!dayOfYearChartInitialized) {
        initDayOfYearChart();
        dayOfYearChartInitialized=true;
    } else {
        dayOfYearChart.update();
    }
}

