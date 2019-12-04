 $(function () {
    dayOfYearChartInitialized = false;
    var bindDatePicker = function() {
		$(".date").datetimepicker({
        format:'YYYY-MM-DD',
			icons: {
				time: "fa fa-clock-o",
				date: "fa fa-calendar",
				up: "fa fa-arrow-up",
				down: "fa fa-arrow-down"
			}
		}).find('input:first').on("blur",function () {
			// check if the date is correct. We can accept dd-mm-yyyy and yyyy-mm-dd.
			// update the format if it's yyyy-mm-dd
			var date = parseDate($(this).val());

			if (! isValidDate(date)) {
				//create date based on momentjs (we have that)
				date = moment().format('YYYY-MM-DD');
			}

			$(this).val(date);
		});
	}

   var isValidDate = function(value, format) {
		format = format || false;
		// lets parse the date to the best of our knowledge
		if (format) {
			value = parseDate(value);
		}

		var timestamp = Date.parse(value);

		return isNaN(timestamp) == false;
   }

   var parseDate = function(value) {
		var m = value.match(/^(\d{1,2})(\/|-)?(\d{1,2})(\/|-)?(\d{4})$/);
		if (m)
			value = m[5] + '-' + ("00" + m[3]).slice(-2) + '-' + ("00" + m[1]).slice(-2);

		return value;
   }

   bindDatePicker();
 });

 $("#submit").click(function(){
    var date = $("#datevalue").val();
    if (date == "") {
        alert("Üres dátum.");
    } else {
        //reset config
        dayOfYearConfig.data.datasets = [];
        dayOfYearConfig.data.labels = [];
        requestDayOfYearChartDataset("petofi_hid_budai", "telki_center", date);
    }
 });

var dayOfYearConfig = {
    type: 'line',
    data: {
        labels: [],
        datasets: []
    }
};

var dayOfYearChartInitialized;
var dayOfYearChart;

function requestDayOfYearChartDataset(from, to, date) {
    $.ajax({
        type: 'GET',
        dataType: "json",
        contentType: "application/json",
        async: true,
        url: "/dayOfYear",
        data: {from: from, to: to, date: date},
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