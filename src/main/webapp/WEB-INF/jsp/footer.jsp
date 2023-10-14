<%@ page import = "java.util.ResourceBundle" %>
<% ResourceBundle resource = ResourceBundle.getBundle("version");
  String _version=resource.getString("app.version");
%>
<!--                                                                                                                     -->
	<!-- ====================BEGIN FOOTER =========================================-->
	<div class="page-footer">
		<div class="page-footer-inner"> <%= new java.util.Date().getYear()+1900 %> &copy; FirstEigen. </div>
		<div align="right"> App Version : <%=_version %> </div>
		<div class="scroll-to-top">
			<i class="icon-arrow-up"></i>
		</div>
	</div>
	<!-- END FOOTER -->




	<!--============================== BEGIN CORE PLUGINS =====================-->

	<script src="./assets/global/plugins/jquery.min.js"
		type="text/javascript"></script>
	<!--<script src="" type="text/javascript"></script>-->
	<script src="./assets/global/plugins/bootstrap/js/bootstrap.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/js.cookie.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/bootstrap-hover-dropdown/bootstrap-hover-dropdown.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/jquery-slimscroll/jquery.slimscroll.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/jquery.blockui.min.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/uniform/jquery.uniform.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/bootstrap-switch/js/bootstrap-switch.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/bootstrap-confirmation/bootstrap-confirmation.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/bootstrap-timepicker/js/bootstrap-timepicker.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/bootstrap-datepicker/js/bootstrap-datepicker.min.js"
		type="text/javascript"></script>

	<script src="./assets/global/scripts/datatable.js"
		type="text/javascript"></script>
	<script src="./assets/global/plugins/datatables/datatables.min.js"
		type="text/javascript"></script>
	<script
		src="./assets/global/plugins/datatables/plugins/bootstrap/datatables.bootstrap.js"
		type="text/javascript"></script>
		
	<script
		src="./assets/global/plugins/bootstrap/js/bootstable.min.js"
		type="text/javascript"></script>
	<!-- ===============================END CORE PLUGINS ================================================-->


	<!-- ================BEGIN THEME GLOBAL SCRIPTS============================ -->
	<script src="./assets/global/scripts/app.min.js" type="text/javascript"></script>
	<script
		src="./assets/pages/scripts/components-date-time-pickers.min.js"
		type="text/javascript"></script>
	<script src="./assets/pages/scripts/table-datatables-responsive.min.js"
		type="text/javascript"></script>
	<!-- ========================END THEME GLOBAL SCRIPTS =========================-->

	<script type="text/javascript"
		src="./assets/global/plugins/jquery.dataTables.min.js"></script>
	<script type="text/javascript"
		src="./assets/global/plugins/dataTables.bootstrap.min.js"></script>
	<script type="text/javascript"
		src="./assets/global/plugins/dataTables.hideEmptyColumns.min.js"></script>	
	<script type="text/javascript"
		src="./assets/global/plugins/dataTables.responsive.min.js"></script>
		<script type="text/javascript" src="./assets/global/scripts/multiselect.min.js"></script>
	<!-- <script type="text/javascript"  src="https://cdn.datatables.net/responsive/2.0.2/js/responsive.bootstrap.min.js"></script> -->

	<!-- =========================BEGIN PAGE LEVEL SCRIPTS============================== -->
	<script src="./assets/pages/scripts/table-datatables-managed.min.js"
		type="text/javascript"></script>
	<script src="./assets/pages/scripts/table-datatables-editable.js?2"
		type="text/javascript"></script>

	<!--=========================EGIN THEME LAYOUT SCRIPTS================================ -->
	
	<script src="./assets/layouts/layout4/scripts/layout.min.js"
		type="text/javascript"></script>
	<script src="./assets/layouts/layout4/scripts/demo.min.js"
		type="text/javascript"></script>
	<script src="./assets/layouts/global/scripts/quick-sidebar.min.js"
		type="text/javascript"></script>
	<script src="./assets/js/toastr.min.js" type="text/javascript"></script>
	<script src="./assets/js/maindb98.js?db98" type="text/javascript"></script>
	<!-- ==================END THEME LAYOUT SCRIPTS=========================== -->
	<!--=========================Google Chart================================ -->
		<script src="./assets/pages/scripts/google-line-chart.js" type="text/javascript"></script>
		<script src="./assets/js/bootstrap-multiselect.js" type="text/javascript"></script> 
		<!--=========================Google Chart End================================ -->
		
	

</body>
</html>