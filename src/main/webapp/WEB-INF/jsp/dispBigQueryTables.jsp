<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<head>
<script src="./assets/global/plugins/jquery.min.js"></script>
<script src="./assets/global/plugins/bootstrap.min.js"></script>
<script src="./assets/global/plugins/jquery.dataTables.min.js"></script>
<style>
.modal .modal-dialog {
	z-index: 10051;
	margin: 80px auto;
}

#createDatatemplateId {
	margin-left: 30%;
}
</style>

</head>


<div class="portlet-body">
	<table id="frm-exp"
		class="table table-striped table-bordered
 				table-hover dataTable no-footer"
		style="width: 100%;">
		<thead>
			<tr></tr>
			<tr>
				<th style="width: 20px"></th>
				<th>Table Names</th>

			</tr>
		</thead>
		<tbody>
			<c:forEach items="${tableNames}" var="tbls">
				<tr>
					<td><input type="checkbox" class="md-check editControl"
						name="selectedTables" id="selectedTables" value="${tbls}">
					</td>
					<td>${tbls}</td>
				</tr>
			</c:forEach>
		</tbody>
	</table>
	<div class="form-actions noborder align-center">
		<button type="submit" id="submitRoleId" class="btn blue Submit">Create Template</button>
		&nbsp;&nbsp;&nbsp;
	</div>

	<br> <br>

</div>

<script>
	var selectedTableName;
	var selectedTableNameArray=[];
	
	function dynamiccheckidf(e){
		var inputcheck=document.getElementById(e);
			
		console.log(inputcheck.value);
		
		if(inputcheck.checked==true){	
			modaldocs.style.display="block";
			
		}else{
			inputcheck="";
			modaldocs.style.display="none";
		}
	}
	
	var isReload = false;
	$(".editControl").on("click", function() {

		var checked = $(this).is(':checked');
		 var checkedValue =checked ? document.querySelector('.editControl:checked').value: '';
		
		if(checked == true){
			$('.editControl:checked').each(function(i){
				selectedTableNameArray[i] = $(this).val();
				});	
			console.log(selectedTableNameArray);
		}
	 	console.log(selectedTableNameArray); 	
	});
	
	
	$('#submitRoleId').click(
			function() {
			    $("#submitRoleId").addClass("hidden");
				var postParamValues = JSON.stringify(selectedTableNameArray);
				console.log("postParamValues: "+postParamValues);
			 $.ajax({
				url:'./createBigQueryDataTemplate',
				type:'POST',
				datatype:'json',
				headers: { 'token':$("#token").val()},
				contentType : 'application/json',
				data:postParamValues,
				success:function(message){
					success:function(data){
						var w = window.open('/quickStartTaskTracking');
	                        w.document.open();
	                        w.document.write(data);
	                        w.document.close();
					}	
				}
			});
 		});
	
	$(document).ready(function() {
	    $('#frm-exp').DataTable( {
	    	
	        columnDefs: [ {
	            orderable: false,
	           
	            targets:   0
	        } ],
	        select: {
	            style:    'os',
	            selector: 'td:first-child'
	        },
	        order: [[ 1, 'asc' ]]
	    } );
	} ); 
	
</script>
