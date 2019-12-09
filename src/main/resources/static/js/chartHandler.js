/**
 * ON WINDOW LOAD
 */
$(document).ready(function () {
    firstChartInitialized = false;
    secondChartInitialized = false;
    weekChartInitialized = false;

    requestFirstChartDataset("telki_center", "szell_kalman");
    requestFirstChartDataset("telki_center", "petofi_hid_budai");

    requestSeondChartDataset("telki_center", "szell_kalman");
    requestSeondChartDataset("telki_center", "petofi_hid_budai");

    requestWeekChartDataset("telki_center", "szell_kalman");
    requestWeekChartDataset("telki_center", "petofi_hid_budai");
});

/**
 * Global variables
 */
 //all data
var firstConfig = {
    type: 'line',
    data: {
        labels: [],
        datasets: []
    }
};
var firstChartInitialized;
var firstChart;

//today
var secondConfig = {
    type: 'line',
    data: {
        labels: [],
        datasets: []
    }
};
var secondChartInitialized;
var secondChart;

//weekChart
var weekConfig = {
    type: 'line',
    data: {
        labels: [],
        datasets: []
    }
};
var weekChartInitialized;
var weekChart;

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

function requestWeekChartDataset(from, to) {
    $.ajax({
        type: 'GET',
        dataType: "json",
        contentType: "application/json",
        async: true,
        url: "/weekly",
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

                timestamps.push(parseWeekdayChar(Number(entry.dayOfWeek)) + " " + entry.hourOfDay);
                newDataset.data.push(entry.inTraffic);
            }

            weekConfig.data.labels = timestamps;
            weekConfig.data.datasets.push(newDataset);
            updateWeekChart();
        },
        error: function (e) {
            console.log("ajax error!");
            console.log(e.message);
        }
    });
}

function initWeekChart() {
    var ctx = document.getElementById('weekChart').getContext('2d');
    weekChart = new Chart(ctx, weekConfig);
}

function updateWeekChart() {
    if (!weekChartInitialized) {
        initWeekChart();
        weekChartInitialized=true;
    } else {
        weekChart.update();
    }
}

function parseWeekdayChar(dayNum) {
    switch(dayNum) {
        case 1:
            return 'H';
        case 2:
            return 'K';
        case 3:
            return 'Sz';
        case 4:
            return 'Cs';
        case 5:
            return 'P';
        case 6:
            return 'Sz';
        case 7:
            return 'V';
        default:
            return 'H';
    }
}