<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<head>

<script src="./assets/global/plugins/jquery.min.js"></script>
<script src="./assets/global/plugins/bootstrap.min.js"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"></script>
<link rel="stylesheet" href="./assets/global/plugins/bootstrap.min.css">

<style>
.marinTop {
	margin-top: 12px;
	padding-top: 5%;
	height: 100%;
}

.modal-dialog {
	display: inline-block;
	text-align: left;
	vertical-align: middle;
}
</style>
</head>

<div class="portlet-body">
	<table id="jobs"
		class="table table-striped table-bordered
 				table-hover dataTable display nowrap no-footer"
		style="width: 100%;">
		<thead>
			<tr>
				<th width="5%" align="center">Check</th>
				<th>Bucket Name</th>
				<th>Single File</th>
				<th>Date Created</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${buckets}" var="buckets" varStatus="status">
				<tr>
					<td align="center"><input type="checkbox"
						name="EditCheck_${buckets.itrid}" class="md-check editControl"
						value="${buckets.check}" id="buckets_${buckets.itrid}"
						onchange="disp(`${buckets.name}`,${buckets.itrid},`${buckets.creationDate}`)"
						value="${buckets.name}" data-id="${buckets.name}"></td>
					<td id="bucket_name_${buckets.itrid}">${buckets.name}</td>

					<td align="center"><input type="checkbox"
						id="singleFile_${buckets.itrid}" value="${buckets.isSingle()}"
						class="single1" name="progress"
						onchange="disp(`${buckets.name}`,${buckets.itrid},`${buckets.creationDate}`)">
					</td>

					<td id="creationDate_${buckets.itrid}">${buckets.creationDate}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<div class="form-actions noborder align-center">
  		<button type="submit" id="submitRoleId" class="btn blue Submit" disabled>Health Check</button>
		&nbsp;&nbsp;&nbsp;
	</div>
	
	<div class="form-actions noborder align-center hidden">
		<form id="quickStartTaskForm"  method="post" action="quickStartTaskStatus"> 
			<input id="taskTaskerListId" type="hidden" name="taskTrackerList"/>
			<input id="quickStartSourceId" type="hidden" name="quickStartSource" value="awsQuickStart"/>
		</form>
	</div>
	<br> <br>
</div>
<!-- <script>
        $(document).ready(function() {
        	$('#quickStartTaskForm').ajaxForm({
        		headers: {'token':$("#token").val()}
        	});
        });
 </script> -->
<script type="text/javascript">

var selectedBucketName;

function closef(){
    
    var modaldocs=document.getElementById('myModal');
    
    modaldocs.style.display="none";
    
}
var isReload = false;
var countBox;

var data_elements = [];
var table = $('#jobs').DataTable();
var datatable;
 $(document).ready(function(){
	 datatable=  $('#jobs').DataTable();
 });
 
  $('#submitRoleId').click(
			function() {
			    $("#submitRoleId").addClass("hidden");
				var postParamValues = JSON.stringify(data_elements);
				console.log("postParamValues: "+postParamValues);
			 $.ajax({
				url:'./createAWSS3DataTemplate',
				type:'POST',
				headers: { 'token':$("#token").val()},
				datatype:'json',
				contentType : 'application/json',
				data:postParamValues,
				success:function(data){
                        if(data!=''){
                            $("body").html(data);
                        }else{
                            window.location.reload();
                        }
				}
			});
 		});
  
function ckChange($event){
	
	var table = document.getElementById('jobs');
	
    var rows = table.getElementsByTagName('tr');
    console.log("rows")
    console.log(rows)
    for (i = 0, j = rows.length; i < j; ++i) {
        cells = rows[i].getElementsByTagName('td')
    }
    
    if (checked.checked) {
      for(var i=0; i < ckName.length; i++){

          
      } 
    }
       
}

function disp(name,val,date){
	console.log("val",val);
	console.log("date",date);
	var createddate = date
	$('#submitRoleId').removeAttr('disabled');
	$('#Cancel').removeAttr('disabled');
	var check_checked = $("#buckets_"+val).is(":checked");
	var is_single = $("#singleFile_"+val).is(":checked");
	console.log(name,check_checked,is_single);
	
	var flag_is_single = '';
	if(is_single == false){
		flag_is_single = false;
		
	}
	else{
		flag_is_single = true;
	}
	 
		var bucket_name = "#bucket_name_"+ val;
		console.log('bucket_name',bucket_name);
		var created_date = "#creationDate_"
				+ val;

		if (check_checked) {
			
			 if(data_elements.length > 0) {
				 const foundEle = data_elements.find(el => el.bucket_name == name);
				 
				 if (foundEle) {
					 foundEle.is_single = flag_is_single;
					 foundEle.created_date = createddate
				  } else {
					  data_elements.push({
							"bucket_name" : name,
							"is_single" : flag_is_single,
							"created_date" : createddate
						});
				  }
				 
			 } else {
				 data_elements.push({
						"bucket_name" : name,
						"is_single" : flag_is_single,
						"created_date" : createddate
					});
			 }
		}
		
		if(!check_checked) {
			if(data_elements.length > 0) {
				data_elements = data_elements.filter((el) => el.bucket_name !== name);
			}
		}

	console.log(data_elements);
	 
}
</script>
