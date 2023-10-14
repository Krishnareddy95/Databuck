<jsp:include page="header.jsp" />
<script>
    var currentWindow;
    $(document).ready(function () {
        var averReportUI_url = '${averReportUILink}';
        if (currentWindow != undefined) {
            currentWindow.close();
        }
        if(averReportUI_url !=="N"){
        $.ajax({
            type: 'GET',
            url: 'dbconsole/generateToken',
            datatype: 'json',
            success: function (response) {
                    currentWindow = window.open(averReportUI_url + response, '_self');                
            },
            error: function (error) {
                console.log('error: ', error);
            },
            complete: function (complete) {
            }
        });
      }else{
    	  alert('Requested page not found. [404]'); 
      }
    });
</script>