/**
 *  this code for advance filter at first it hit
 *  /microDataColumn  to get column name and create select option label
 *  then /microDataValue select option data
 */
var strSearchValue = "";
	var column_name = [];
	var _this = this;
	var intSeperators = 0;
	var uniqueArray = [];
	var tableName; 
	var api; 
	var idApp;
	var dataTableId;
	var strSearchValue;
	var globalTableName;
function advancefilter(idApp, tableName, api, dataTableId){
	window.tableName = tableName;
	console.log('advancefilter :: '+tableName);
	globalTableName = tableName;
	window.idApp = idApp;
	window.api = api;
	window.dataTableId = dataTableId;
	console.log(window.tableName+' '+window.api+' '+window.tableName+' '+window.idApp+' '+window.dataTableId);
	var no_error = 1;
	$('.input_error').remove();
	var microDataValueData = {
		tableName : tableName
	};
	var form_data = {
		idApp :idApp
	};

	$.ajax({
				url : './microDataColumn',
				type : 'POST',
				headers: { 'token':$("#token").val()},
				datatype : 'json',
				data : form_data,
				success : function(message) {
				var j_obj = $.parseJSON(message);
				if (j_obj.hasOwnProperty('success')) {
					var temp = {};
					column_array = JSON.parse(j_obj.success);
					// empty the list from previous selection
					//$('#Microsegval').empty();
					intSeperators = column_array.length - 1;
					console.log(" seperators :: "+ intSeperators);
					var navButtons = document.getElementById("nav_button-bar");

					$.each(column_array,function(i, obj) {
						var column_details = column_array[i];
						var div = document.createElement("div");
						div.setAttribute("id",column_details+"_"+ i);
						div.setAttribute("class","col-md-2 form-group-sm");
						div.setAttribute("style","float:left;")

						_this.column_name.push(column_details);
						var button = document.createElement("button");
						button.setAttribute("id",column_details);
						button.setAttribute("class","btn btn-group-justified btn-primary margin-bottom-10");

						button.innerHTML = column_details;

						var input = document.createElement("select");
						input.setAttribute("id","col_value"+ i);
						if (i > 0) {
							input.setAttribute("class","col-md-8 form-group-sm");
						} else {
							input.setAttribute("class","col-md-8 form-group-sm");
						}

						//input.setAttribute("class","form-control");

						div.appendChild(button);
						div.appendChild(input);

						navButtons.appendChild(div);
						/*  $("#col_value"+i).fastselect({
								toggleButtonClass: 'form-control fstToggleBtn',
								queryInputClass: 'form-control fstQueryInput',
								placeholder: 'value',
								itemClass:'form fstResultItem',
								searchPlaceholder: 'search..',
							}); */

							$("#col_value"+ i).append('<option value= >All</option>');
								//alert(obj.value+":"+obj.text);
								//  var div_data="<option value="+column_details+">"+column_details+"</option>";
								// alert(div_data);
								//   $(div_data).appendTo('#Microsegval'); 
														});
								var divbar = document.createElement("div");
								divbar.setAttribute("id", "divbar");
								divbar.setAttribute("class","col-md-2 form-group-sm");

								var refresh = document.createElement("button");
								refresh.setAttribute("id","r_efresh");
								refresh.setAttribute("class","btn");
								refresh.setAttribute("onClick","getUpdateTableValues()");
								refresh.innerHTML = "Search";

								divbar.appendChild(refresh)
								navButtons.appendChild(divbar);
								
								/* add refresh button to load actual data*/
								
								var loaddivbar = document.createElement("div");
								loaddivbar.setAttribute("id", "loaddivbar");
								loaddivbar.setAttribute("class","col-md-2 form-group-sm");

								var loadrefresh = document.createElement("button");
								loadrefresh.setAttribute("id","load_refresh");
								loadrefresh.setAttribute("class","btn");
								loadrefresh.setAttribute("onClick","destroyandloaddata()");
								loadrefresh.setAttribute("hidden", true); 
								loadrefresh.innerHTML = "Refresh";
								
								loaddivbar.appendChild(loadrefresh);
								navButtons.appendChild(loaddivbar);
								
								/* refresh data button code done*/
								
								console.log("start...........");
								console.log("before ./microDataValue call formdatatableName :: "+ formdatatableName);
								$.ajax({
									url : './microDataValue',
									type : 'POST',
									headers: { 'token':$("#token").val()},
									datatype : 'json',
									data : microDataValueData,
									success : function(message) {

									console.log("Mid.......");
									var j_objv = $.parseJSON(message);
									if (j_objv.hasOwnProperty('success')) {
										console.log("after ./microDataValue call formdatatableName :: "+ formdatatableName);
										value_array = JSON.parse(j_objv.success);
										uniqueArray = value_array.filter(onlyUnique);
										console.log("value details");
										console.log(uniqueArray);
										var value_m = [];
										$.each(uniqueArray,function(i,obj) {
											var value_details = uniqueArray[i];
											var arrRawData = value_details.split("-");
											/*console.log("arrRawData :: "+ arrRawData);
											var intIndexOfChar = value_details.indexOf("-",intSeperators);
											var minusRawDataStr = value_details.slice(intIndexOfChar + 1,value_details.length);
											console.log("minusRawDataStr :: "+ minusRawDataStr)
											arrRawData[arrRawData.length - 1] = minusRawDataStr;*/
											console.log("value details : "+ arrRawData);
											value_m = arrRawData;
											//	value_m = value_details.split(/-(.+)/)[intSeperators];
											console.log("print item and index");
											value_m.forEach(function(item,index) {
											console.log(item,index);
											if (!$("#col_value"+index+" option[value='" + item + "']").length) {
										        console.log(item + ' is a new value!');
										     //   $('<option>').item.appendTo($('#col_value'+index));
										        
										    	var select = $("#col_value"+ index);
												select.append('<option value="'+item+'">'+ item+ '</option>');
										    }
											
										});
									});
								} else if (j_objv.hasOwnProperty('fail')) {
									console.log(j_objv.fail);
									toastr.info(j_objv.fail);
								}
							}
						});
					} else if (j_obj.hasOwnProperty('fail')) {
						console.log(j_obj.fail);
						toastr.info(j_obj.fail);
					}
				},
				error : function(xhr, textStatus,errorThrown) {
					$('#initial').hide();
					$('#fail').show();
				}
			});
}

function getUpdateTableValues() {
	showFilterChart = true;
	
	$('#load_refresh').show();
	console.log('getUpdateTableValues');
	var arrDropDownCollection = [];
	var strSearchValue = "";
	for (var i = 0; i < intSeperators + 1; i++) {
		var e = document.getElementById("col_value" + i);
		var strData = e.options[e.selectedIndex].value;
		if(strData !=null && strData !== ''){
			if (i > 0) {
				strSearchValue = strSearchValue + "-" + strData;
			} else {
				strSearchValue = strSearchValue + strData;
			}
		}else{
			strData = '_'; // to replace it with % in java code
			if (i > 0) { // from second word onwords
				strSearchValue = strSearchValue + "-" + strData;
			} else { // for first word
				strSearchValue = strSearchValue + strData;
			}
		}
		

	}
	/*var n = strSearchValue.startsWith('-');
	if(n){
		strSearchValue = strSearchValue.substring(1);
	}*/
	console.log("strSearchValue :: " + strSearchValue);
	
		destroyCreateDatatable(window.dataTableId);
	 loadfreshdata(window.dataTableId, window.api, window.tableName, window.idApp, strSearchValue);
	 console.log(window.dataTableId,  window.api, window.tableName,  window.idApp);
	 if($('#chart-content').is(':visible')){
			$("#chart-content").hide();
			$("#nochartdata").hide();
			$('#filter-chart-content').show(); 			
		}
		filterChartCall(window.tableName, strSearchValue);
		
	 $('#loaddivbar').show();
}

function destroyandloaddata(){
	console.log('destroyandloaddata');
	//to reset dropdowns
	for (var i = 0; i < intSeperators + 1; i++) {
		$("#" + "col_value" + i).prop('selectedIndex', 0);
	}
	showFilterChart = false;
	 if($('#filter-chart-content').is(':visible')){
			$('#filter-chart-content').hide(); 
			$("#chart-content").show();
			openModal();
		}
	console.log(window.dataTableId+' '+window.api+' '+window.tableName+' '+window.idApp);
	 destroyCreateDatatable(window.dataTableId);
	 loadfreshdata(window.dataTableId, window.api, window.tableName, window.idApp, window.strSearchValue);
	
	 
}

function destroyCreateDatatable(tableid){
	var localTableid = '#'+tableid;
	console.log('destroyCreateDatatable :: '+localTableid);
	  if ( $.fn.DataTable.isDataTable(localTableid) ) {
		  $(localTableid).DataTable().destroy();
		}

		$(localTableid+' tbody').empty();
}

function loadfreshdata(tableid, api, tableName, idApp, strSearchValue){
	console.log('loadfreshdata');
	console.log(tableid, api, tableName, idApp, strSearchValue);
	 $('#DGroupTableId').dataTable(
				{
					"bPaginate" : true,
					"order" : [ 0, 'asc' ],
					"bInfo" : true,
					"iDisplayStart" : 0,
					"bProcessing" : true,
					"bServerSide" : true,
					"dataSrc" : "",
					'sScrollX' : true,
					"sAjaxSource" : path + "/"+api+"?tableName="
							+ tableName + "&idApp=" + idApp
							+ "&searchValue="+strSearchValue,
					"aoColumns": [
					    {"data" : "Id"},
                        {"data" : "Date"},
                        {"data" : "Run1"},
                        {"data" : "dayOfYear"},
                        {"data" : "month"},
                        {"data" : "dayOfMonth"},
                        {"data" : "dayOfWeek"},
                        {"data" : "hourOfDay"},
                        {"data" : "RecordCount1"},
                        {"data" : "RC_Std_Dev1"},
                        {"data" : "RC_Mean1"},
                        {"data" : "dGroupDeviation1"},
                        {"data" : "dgDqi"},
                        {"data" : "dGroupVal"},
                        {"data" : "dgroupCol"},
                        {"data" : "dGroupRcStatus",

                            "render": function(data, type, row, meta){
                                if (data === "passed") {
                                    data = "<span class='label label-success label-sm'>"
                                            + data + "</span>";

                                } else if (data === "failed") {
                                    data = "<span class='label label-danger label-sm'>"
                                            + data + "</span>";
                                }

                                return data;
                            }
                        },
                        {"data" : "Action",
                            "render": function(data, type, row, meta){
                                var dGroupVal =row.dGroupVal;

                                var dGroupCol =row.dgroupCol;
                                var Run1= row.Run1;
                                var Date =row.Date;
                                if (data === "Rejected") {
                                   data = "<font color='red'><b>Rejected</b></font>";
                                }
                                else if (data === "Accepted"){
									data = "<font color='red'><b>Accepted</b></font>";
								}
                                else{
                                    data = "<a href='rejectInd?dGroupCol=" + dGroupCol + "&tab=GBRCA&Date="
                                                + Date+ "&tableName=" + tableName + "&dGroupVal=" + dGroupVal
                                                + "&Run=" + Run1+ "&idApp=" + idApp + "'>"
                                                + "<i class='fa fa-thumbs-down'></i></a>" + "<style>.fa { font-size: 26px;}</style>";
                                  }
                                  return data;
                            }
                        },
                        {"data" : "dGroupDeviation1",
                         		"render": function(data, type, row, meta){
					                   if (data != null && data != undefined && data.length>0){
					                        data = "<a onClick=\"javascript:updateRecordCountAnomalyThreshold("+idApp+",'Microsegment Record Count Anomaly','','"+data+"')\" class=\"fa fa-thumbs-down\"></a>";
					                   }
					                   
					                   return data;
							     }
                        },
                        {"data" : "UserName"},
                        {"data" : "Time"}
                    ],
					"dom" : 'C<"clear">lfrtip',
					colVis : {
						"align" : "right",
						restore : "Restore",
						showAll : "Show all",
						showNone : "Show none",
						order : 'alpha'

					},
					"language" : {
						"infoFiltered" : ""
					},
					"dom" : 'Cf<"toolbar"">rtip',

				});
	 $('#loaddivbar').hide();
}

function filterChartCall(globalTableName, strSearchValue){
//	console.log('api call :' + apicall + '| table name : ' + tablename);
	var table;
	console.log("hello");
	// console.log( $("#tableName2").val());
	// var tableName = $("#tableName").val();
	$(function() {
		// check table is not null and not blanck
		// / if($("#tableName2").val() ===
		// "DATA_QUALITY_"+$("#idApp").val()+"_Transactionset_sum_dgroup" &&
		// $("#tableName2").val() !== ""){
	//	console.log(apicall, globalTableName, xaxis, yaxis);
		var form_data = {
			tableName : globalTableName,
			dGroupVal : strSearchValue
		};
		var request = $.ajax({
			url : './dGroupValGoogleChart',
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
			filterChart(msg, 'Date', 'RecordCounts');

		});
		request.fail(function(jqXHR, textStatus) {
			console.log("Request failed: " + textStatus);
		});
		/*
		 * }else{ alert("Count resionalibility without micrsignment call ... "); }
		 */
	});
}

function filterChart(array, xaxis, yaxis){

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

	// Set chart options
	var options = {
		'title' : '',
//		legend: { position: 'bottom' },
		hAxis : {
			title : xaxis
		},
		vAxis : {
			title : yaxis,
			vAxis:{viewWindowMode: "explicit", viewWindow:{ min: 0 }}
		},
		'width' : 1100,
		'height' : 750,legend: {
		      maxLines: 1,
		      textStyle: {
		        fontSize: 15
		      }
		    },
		    chartArea: {
		    	  left: 15,
		    	  top: 5,
		    	  width: '80%',
		    	  height: '60%'
		    	}
	};
	console.log("before clicking on legend");
	// Instantiate and draw the chart.
	var chart = new google.visualization.LineChart(document
			.getElementById('filter-modal-body')); // on this element google chart
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
				title : xaxis
			},
			vAxis : {
				title : yaxis,
				vAxis:{viewWindowMode: "explicit", viewWindow:{ min: 0 }}
			},
			'width' : 1100,
			'height' : 750,
			legend: {
			      maxLines: 1,
			      textStyle: {
			        fontSize: 15
			      }
			    },
			    chartArea: {
			    	  left: 15,
			    	  top: 5,
			    	  width: '80%',
			    	  height: '60%'
			    	},
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
		$("#filter-nochartdata").hide();
	}
	
	if(booleanHideGraphLoadStatus){
		$("#nochartdata").hide();
	}
//	$('#segment-dropdown').show();
	

}