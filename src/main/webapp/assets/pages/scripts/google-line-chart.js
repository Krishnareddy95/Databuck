//dropdown with select javascript code ends here
var mainJsonObject = null;

// on page load set page nav actvive class

$("#tabfortrade").click(function() {

	$("#chart-content").show();
	$("#dgroupValTables").hide();
	$("#dup-stat-table-data").show();
});
$("#tabfortable").click(function() {

	$("#chart-content").hide();
	$("#dgroupValTables").show();
});

// to hide chart and show table -->
function closeModal() {
	$('#dup-stat-table-data').show();
	$("#chart-content").hide();
	$('#filter-chart-content').hide();
	$("#dgroupbodyId").show();
	globalbooleanTableStatus = true;

}

google.charts.load('current', {packages: ['corechart', 'line']});
google.charts.setOnLoadCallback(drawChart);
// function called from loadChart() and providing dGroupVal (for header) and
// char array for chart
function drawChart(array, xaxis, yaxis) {

	booleanHideGraphLoadStatus = true;
	console.log("drawChart :: array :: " + array);
	// var jsonObj = JSON.parse(array);
	var header = array.header;
	var chartdata = array.chart;
	console.log("header :: " + header);
	console.log("chart :: " + chartdata);
	// Define the chart to be drawn.
	var data = new google.visualization.DataTable();
	// changecat(dGroup);
	data.addColumn('string', 'Date');
	for (i = 0; i < header.length; i++) {
		data.addColumn('number', header[i]);
	}

	// data.addColumn('string', 'dGroupCol');
	data.addRows(chartdata);
	
	   data.sort({
	         column: 0,
	         desc: true
	       }); 
	
	// Set chart options
	var options = {
		'title' : '',
//		legend: { position: 'bottom' },
		hAxis : {
			title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
		},
		vAxis : {
			title : yaxis,
			vAxis:{viewWindowMode: "explicit", viewWindow:{ min: 0 }}
		},
		'width' : 	950,
		'height' : 750,legend: {
		      maxLines: 1,
		      textStyle: {
		        fontSize: 15
		      }
		    },
		    chartArea:{left:80,top:10,width:'70%'},
		    isStacked: true
	};
	console.log("before clicking on legend");
	// Instantiate and draw the chart.
	var chart = new google.visualization.LineChart(document.getElementById('modal-body')); // on this element google chart
											// will load
	chart.draw(data, options);
	
	var columns = [];
    var series = {};
    for (var i = 0; i < data.getNumberOfColumns(); i++) {
        columns.push(i);
        if (i > 0) {
            series[i - 1] = {};
        }
    }
	
    var options = {
			'title' : '',
	//		legend: { position: 'bottom' },
			hAxis : {
				title : xaxis, direction:-1, slantedText:true, slantedTextAngle:45
			},
			vAxis : {
				title : yaxis,
				vAxis:{viewWindowMode: "explicit", viewWindow:{ min: 0 }}
			},
			'width' : 	950,
			'height' : 750,
			legend: {
			      maxLines: 1,
			      textStyle: {
			        fontSize: 15
			      }
			    },
			    chartArea:{left:80,top:10,width:'70%'},
			    isStacked: true,
			series: series
		};
    
	google.visualization.events.addListener(chart, 'select', function () {
		console.log("trying to hide line on click of legend");
        var sel = chart.getSelection();
        // if selection length is 0, we deselected an element
        console.log("sel");
        console.log(sel);
        if (sel.length > 0) {
            // if row is undefined, we clicked on the legend
            if (sel[0].row === null) {
                var col = sel[0].column;
                console.log("column : ");
                console.log(col);
                if (columns[col] == col) {
                    // hide the data series
                	 console.log("column condition true ");
                	 console.log(data.getColumnLabel(col));
                	 console.log(data.getColumnType(col));
                    columns[col] = {
                        label: data.getColumnLabel(col),
                        type: data.getColumnType(col),
                        calc: function () {
                            return 0;
                        }
                    };
                    
                    // grey out the legend entry
                    series[col - 1].color = '#CCCCCC';
                }
                else {
                    // show the data series
                    columns[col] = col;
                    series[col - 1].color = null;
                }
                console.log("column dfn ");
                console.log(columns);
                var view = new google.visualization.DataView(data);
                view.setColumns(columns);
                chart.draw(view, options);
            }
        }
    });

	if(booleanHideGraphLoadStatus){
		$("#nochartdata").hide();
	}
//	$('#segment-dropdown').show();
	
	
}

// to hit /googleChart controller from paginationController.java
function loadChart() {

	$("#dgroupbodyId").hide();
	var table;
	console.log("hello");
	console.log($("#tableName2").val());
	// var tableName = $("#tableName").val();

	$(function() {
		// check table is not null and not blanck
		if ($("#tableName2").val() === "DATA_QUALITY_Transactionset_sum_dgroup"
				&& $("#tableName2").val() !== "") {

			var form_data = {
				tableName : $("#tableName2").val(),
				idApp : $("#idApp").val()
			};
			var request = $.ajax({
				url : './googleChart',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				data : form_data,
				dataType : 'json'

			});
			request.done(function(msg) {
				// json = $.parseJSON(msg); // create an object with the key of
				// the array
				mainJsonObject = msg;
				console.log(msg);
			//	dropdownForChartLines(msg);
				drawChart(msg, xaxis, yaxis)

			});
			request.fail(function(jqXHR, textStatus) {
				console.log("Request failed: " + textStatus);
			});
		} else {
			alert("Count resionalibility without micrsignment call ... ");
		}

	});
}

function loadChart(apicall, tablename, idApp, xaxis, yaxis) {
	console.log('api call :' + apicall + '| table name : ' + tablename +'| idApp : '+idApp);
	$("#dgroupbodyId").hide();
	var table;
	console.log("hello");
	// console.log( $("#tableName2").val());
	// var tableName = $("#tableName").val();
	$(function() {
		// check table is not null and not blanck
		// / if($("#tableName2").val() ===
		// "DATA_QUALITY_"+$("#idApp").val()+"_Transactionset_sum_dgroup" &&
		// $("#tableName2").val() !== ""){
		console.log(apicall, tablename, xaxis, yaxis);
		var form_data = {
			tableName : tablename,
			idApp : idApp
		};
		var request = $.ajax({
			url : './' + apicall,
			type : 'POST',
			headers: { 'token':$("#token").val()},
			data : form_data,
			dataType : 'json'
		});
		request.done(function(msg) {
			// json = $.parseJSON(msg); // create an object with the key of the
			// array
			mainJsonObject = msg;
			console.log(msg);
		//	dropdownForChartLines(msg);
			drawChart(msg, xaxis, yaxis)

		});
		request.fail(function(jqXHR, textStatus) {
			console.log("Request failed: " + textStatus);
		});
		/*
		 * }else{ alert("Count resionalibility without micrsignment call ... "); }
		 */
	});
}