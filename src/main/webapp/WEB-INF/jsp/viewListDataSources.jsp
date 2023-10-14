
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp"/>
<jsp:include page="checkVulnerability.jsp" />
<jsp:include page="container.jsp"/>


		
		<!--============= BEGIN CONTENT BODY============= -->


                    <!--*****************      BEGIN CONTENT **********************-->
		
		<div class="page-content-wrapper">
			<!-- BEGIN CONTENT BODY -->
			<div class="page-content">
				<!-- BEGIN PAGE TITLE-->
				<!-- END PAGE TITLE-->
				<!-- END PAGE HEADER-->
				<div class="row">
					<div class="col-md-12">
					
                  <!--****         BEGIN EXAMPLE TABLE PORTLET       **********-->
						
						<div class="portlet light bordered">
							<div class="portlet-title">
								<div class="caption font-red-sunglo">
									<span class="caption-subject bold "> List DataSource Data </span>
								</div>
							</div>
							<div class="portlet-body">
								<table class="table table-striped table-bordered table-hover"
									id="showDataTable">
									<thead>
										<tr>
											<th>Name</th>
											<th>Description</th>
											<th>dataLocation</th>
											<th>createdAt</th>
											<th></th>
											
										</tr>
									</thead>
									<tbody>
											
											<tr>
												<td>${listDataSourceobj.name}</td>
												<td>${listDataSourceobj.description}</td>
												<td>${listDataSourceobj.dataLocation}</td>
												<td>${listDataSourceobj.createdAt}</td>
												<td><a onClick="validateHrefVulnerability(this)"  href="viewMetaData?idData=${listDataSourceobj.idData}">view</a></td>
				                            </tr>
									</tbody>
								</table>
							</div>
						</div>
		<!--       *************END EXAMPLE TABLE PORTLET*************************-->
	
					</div>
				</div>
			</div>
		</div>
	

	
	                  <!--********     BEGIN CONTENT ********************-->

<jsp:include page="footer.jsp"/>