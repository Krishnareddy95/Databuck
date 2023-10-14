
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>


<jsp:include page="header.jsp" />

<jsp:include page="container.jsp" />



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
							<span class="caption-subject bold ">Customize Values </span>
						</div>
					</div>
					<div class="portlet-body">
						<table class="table table-striped table-bordered table-hover"
							id="showDataTable">
							<thead>
							</thead>
							<div class="portlet-body form">
								<input type="hidden" id="tourl" value="createForm" />
								<!--  <form role="form" method="post" action="">  -->
								<h1>${updatestatus }</h1>	
								<%  
								System.out.println(request.getParameter("updatestatus"));
								
								%>							
							

						</table>
					</div>
				</div>
				<div class="form-actions noborder align-center"></div>
														<!--       *************END EXAMPLE TABLE PORTLET*************************-->

			</div>
		</div>
	</div>
</div>



<!--********     BEGIN CONTENT ********************-->

<jsp:include page="footer.jsp" />