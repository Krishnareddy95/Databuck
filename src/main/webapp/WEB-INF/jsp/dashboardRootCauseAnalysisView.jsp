<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="org.springframework.jdbc.support.rowset.SqlRowSet"%>
<%@page
	import="org.springframework.jdbc.support.rowset.SqlRowSetMetaData"%>
	<jsp:include page="checkVulnerability.jsp" />
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css">
<style>
td.details-control {
	background:
		url('./assets/global/plugins/details_open.png')
		no-repeat center center;
	cursor: pointer;
}

tr.shown td.details-control {
	background:
		url('./assets/global/plugins/details_close.png')
		no-repeat center center;
}

td.details-control1 {
	background:
		url('./assets/global/plugins/details_open.png')
		no-repeat center center;
	cursor: pointer;
}

td.shown1 {
	background:
		url('./assets/global/plugins/details_close.png')
		no-repeat center center;
}

tr.highlightExpanded td {
	background-color: #fcf7fa !important;
}

tr.highlightExpanded3 td {
	background-color: #f5ffcd !important;
}

tr.highlightExpanded4 td {
	background-color: #fcfaff !important;
}

tr.highlightExpanded5 td {
	background-color: #ffffde !important;
}

tr.highlightExpanded6 td {
	background-color: #f5ffeb !important;
}

tr.highlightExpanded7 td {
	background-color: #fff9e1 !important;
}

tr.highlightExpanded8 td {
	background-color: #f6fcc9 !important;
}

tr.highlightExpanded9 td {
	background-color: #fffccc !important;
}

tr.highlightExpanded10 td {
	background-color: #fff0c2 !important;
}
</style>


</head>
<body>
	<jsp:include page="dashboardTableCommon.jsp" />


	<div class="row">
		<div class="col-md-12">
			<div class="tabcontainer">
				<ul class="nav nav-tabs responsive" role="tablist"
					style="border-top: 1px solid #ddd; border-bottom: 1px solid #ddd;">

					<li ><a onClick="validateHrefVulnerability(this)" 
						href="dashboard_table?idApp=${idApp}" style="padding: 2px 2px;">
							<strong>Count Reasonability</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="validity?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Microsegment Validity</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="dupstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Uniqueness</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="nullstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Completeness</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="badData?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>Conformity</strong> </a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="sqlRules?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>Custom Rules</strong> </a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="stringstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Drift & Orphan</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="numericstats?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Distribution Check</strong>
					</a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="recordAnomaly?idApp=${idApp}"
						style="padding: 2px 2px;"> <strong>Record Anomaly</strong></a></li>
					<li ><a onClick="validateHrefVulnerability(this)"  href="timelinessCheck?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>Sequence</strong> </a></li>
					<li ><a onClick="validateHrefVulnerability(this)" 
						href="exceptions?idApp=${idApp}" style="padding: 2px 2px;"><strong>Exceptions</strong> </a></li>
					<li class="active" ><a onClick="validateHrefVulnerability(this)"  href="rootCauseAnalysis?idApp=${idApp}"
						style="padding: 2px 2px;"><strong>RootCause Analysis</strong> </a></li>

				</ul>
			</div>
		</div>
	</div>

	<input type="hidden" id="tableName" value="${row_summary_TableName}">
	<input type="hidden" id="tableNameCsv" value="${row_summary_TableNameCsv}">
	<input type="hidden" id="rootCauseCsv"
		value="${root_cause_AnalysisCsv}">

	<div>
	    <div class="portlet-title">
			<div class="caption font-red-sunglo">
				<span class="caption-subject bold ">RootCause Analysis</span>
			</div>
	    </div>
	    <hr />
		<div id="rcatablehead" class="portlet-body">			 
			<table id="rootCauseAnalysis_table"
				class="table table-striped table-bordered  table-hover"
				style="width: 100%;">
				<thead>
					<tr></tr>
				</thead>
			</table>
		</div>
	</div>


	<span style="display: inline-block; height: 50px;"></span>

	<jsp:include page="downloadCsvReports.jsp" />
	<jsp:include page="footer.jsp" />

</body>



<script>

var table;

var vardata ="All";
var g_dataFull = [];
var tableNameCsvNew=$("#rootCauseCsv").val();
var tableNameCsv=$("#tableNameCsv").val();
			
$.ajax({url: path+'/dynamicDataColumnCsv?tableNameCsv='+tableNameCsvNew,
    dataType: 'json',
    success: function(dataNew)
    {
    //column_array = JSON.parse(dataNew.success);
    var aryColNewChecked = dataNew.success;
    var aryJSONColNewTable = [];

    for (var i=0; i < aryColNewChecked.length; i++ )
    {
    	aryJSONColNewTable.push(
    {
    "sTitle": aryColNewChecked[i],
    "aTargets": [i]
    });
    };
    $(document).ready(function()
    {
    	$('#dynamic').html( '' );
    	var table = $('#rootCauseAnalysis_table').dataTable({
    	                    "paging": false,
    					  	"bInfo": false,
    					  	"bProcessing" : true,
    					 	"bServerSide" : true,
    					 	"bFilter": false,
    					 	"iDisplayStart" : 0,
    					 	'sScrollX' : true,
    					 	
    					 	 "ajax": {
    					          "url": path+'/dynamicDataSetCsv?tableNameCsv='+tableNameCsvNew  + "&vdata=" + vardata,
    					          "dataSrc": function(d){
		    				             g_dataFull = d.aaData;
		    				             var dataParent = []
		    				             $.each(d.aaData, function(){
		    				            	 if(this[4] === "1" ) {
		    				                   dataParent.push(this);  
		    				                }
		    				             });
		    				            
		    				             return dataParent;
		    				          }
    					        },
    					        
    					 
    					 	
    						"aoColumnDefs": aryJSONColNewTable,
    						 
    						
    					 	"dom" : 'C<"clear">lfrtip',
    					 	"columns": [
    				            {
    				                "className":      'details-control',
    				                "orderable":      false,
    				                "data":           null,
    				                "defaultContent": ''
    				            },
    				            {
    				                "className":      'hidden',
    				                "orderable":      false,
    				                "data":           null
    				                
    				            },
    				            {
    				                "className":      'hidden',
    				                "orderable":      false,
    				                "data":           null
    				                
    				            },
    				            {
    				                "className":      'hidden',
    				                "orderable":      false,
    				                "data":           null
    				                
    				            },
    				            {
    				                "className":      'hidden',
    				                "orderable":      false,
    				                "data":           null
    				                
    				            }
    				         
    				       
    				           
    				        ],
    				      
    				        "createdRow" : function ( row, data, index ) {
    				            if (data[2] === '') {
    				              var td = $(row).find("td:first");
    				              td.removeClass( 'details-control' );
    				              
    				            }
    				            $('td', row).css('background-color', '#f0faff');
    				           },
    					 	"targets": 'no-sort',
    					 	"bSort": false,
    					 	"order": [],
    					 	 
    						"dom" : 'Cf<"toolbar"">rtip'
    								

    	});	
  
    
    $('#rootCauseAnalysis_table tbody').on('click', 'td.details-control', function () {
        var tr = $(this).closest('tr');
        var row = $('#rootCauseAnalysis_table').DataTable().row(tr);

        if ( row.child.isShown() ) {
            // This row is already open - close it
            row.child.hide();
            tr.removeClass('shown');
           
        }
        else {
            // Open this row
            row.child( format(row.data())).show();  
            console.log("else block")
            tr.addClass('shown'); 
        }
    } );
    
 
    } );
   
    }
    });
    

function format ( d ) {
	console.log(d);
    //  var html='<table style="width: 100%;">';
    var html='';
    var className="";
    var hasChildren = false;
    $.each(g_dataFull, function(){
       if(this[5] === d[5]){
    	   var dataLength="";
    	   
    	   for (var i=5;i < this.length ; i++ )
    	    {    		   
    	   		dataLength +='<td>'+this[i]+'</td>'
    		}
    	   if (this[2] === "" && this[3] !=""  && this[7] === "" )
    		   {
             html += '<tr class="highlightExpanded" >'+
    		   
    		   '<td>'+this[0]+'</td>'+
    		   dataLength+
    		'</tr>'
       
    		   }
    	   if (this[2] != "" && this[3] !=""  && this[7] === "")
		   {
    		   html += '<tr class="highlightExpanded" data-toggle="collapse" data-target=".child'+this[1]+'">'+
    		   
    		   '<td class="details-control1">'+this[0]+'</td>'+
    		   dataLength+
    		'</tr>'
		   }
    	   if (this[2] != "" && this[3] !=""  && this[7] != "")
		   {
    		   html += '<tr   class="collapse child'+this[3]+' highlightExpanded'+this[4]+'"  data-toggle="collapse" data-target=".child'+this[1]+'">'+
    		   
    		   '<td class="details-control1">'+this[0]+'</td>'+
    		   dataLength+
    		'</tr>'
		   }
    	   if (this[2] === "" && this[3] !="" && this[7] != "" )
		   {
    		   html += '<tr class="collapse child'+this[3]+' highlightExpanded'+this[4]+'">'+
    		     		   '<td>'+this[0]+'</td>'+
    		   dataLength+
    		'</tr>'
		   }
	
          hasChildren = true;
       }
    });
  
    console.log("html: " + html);
    console.log("hasChildren: " + hasChildren);
    if(!hasChildren){
    	 html += '<tr><td>No Data</td></tr>';
     
    }
  
 
    //html += '</table>'
    return $(html).toArray();
}

    
</script>

<script> 

	$("#rmRootCauseAnalysis").click(function() {

		
		$("#rmRootCauseAnalysis").addClass('hidden');
		$("#rootCauseAnalysis").removeClass('hidden').show();
		$('#rcatablehead').addClass('hidden').show();
		
		
		});
	
$('#rootCauseAnalysis_table').on('click','td.details-control1', function (e) {
		
		   var td = $(this).closest('td');
		   td.removeClass('details-control1')
		  td.addClass('shown1');
		   e.startPropagation(); 
		   
	     } );
	  
 $('#rootCauseAnalysis_table').on('click','td.shown1', function (e) {
		   
		   var td = $(this).closest('td');
		    td.removeClass('shown1');
		  td.addClass('details-control1');
		  e.startPropagation();
		  
	   } );
	   
$('#rootCauseAnalysis_table').on ('click','td', function(e) {
		   e.stopPropagation();
		   
		});
	 

</script>