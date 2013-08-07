<%@ page import="org.wso2.carbon.event.processor.stub.EventProcessorAdminServiceStub" %>
<%@ page import="org.wso2.carbon.event.processor.ui.UIUtils" %>
<%@ page import="java.util.ResourceBundle" %>


<%@ taglib uri="http://wso2.org/projects/carbon/taglibs/carbontags.jar" prefix="carbon" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>


<fmt:bundle basename="org.wso2.carbon.event.processor.ui.i18n.Resources">

<carbon:breadcrumb
        label="eventprocessor.edit.details"
        resourceBundle="org.wso2.carbon.event.processor.ui.i18n.Resources"
        topPage="false"
        request="<%=request%>"/>

<script type="text/javascript" src="../admin/js/breadcrumbs.js"></script>
<script type="text/javascript" src="../admin/js/cookies.js"></script>
<script type="text/javascript" src="../admin/js/main.js"></script>

<script type="text/javascript" src="global-params.js"></script>

<script src="../editarea/edit_area_full.js" type="text/javascript"></script>

<link type="text/css" href="../dialog/js/jqueryui/tabs/ui.all.css" rel="stylesheet"/>
<script type="text/javascript" src="../dialog/js/jqueryui/tabs/jquery-1.2.6.min.js"></script>
<script type="text/javascript"
        src="../dialog/js/jqueryui/tabs/jquery-ui-1.6.custom.min.js"></script>
<script type="text/javascript" src="../dialog/js/jqueryui/tabs/jquery.cookie.js"></script>

<!--Yahoo includes for dom event handling-->
<script src="../yui/build/yahoo-dom-event/yahoo-dom-event.js" type="text/javascript"></script>

<%--end of newly added--%>


<%
    String status = request.getParameter("status");
    ResourceBundle bundle = ResourceBundle.getBundle(
            "org.wso2.carbon.event.processor.ui.i18n.Resources", request.getLocale());

    if ("updated".equals(status)) {
%>
<script type="text/javascript">
    jQuery(document).ready(function () {
        CARBON.showInfoDialog('<%=bundle.getString("activated.configuration")%>');
    });
</script>
<%

    }
%>


<%
    String executionPlanName = request.getParameter("execPlanName");
    String executionPlanPath = request.getParameter("execPlanPath");

    String executionPlanFile = "";
    if (executionPlanName != null) {
        EventProcessorAdminServiceStub stub = UIUtils.getEventProcessorAdminService(config, session, request);
        executionPlanFile = stub.readExecutionPlanConfigurationFile(executionPlanName);

    } else if (executionPlanPath != null) {
        EventProcessorAdminServiceStub stub = UIUtils.getEventProcessorAdminService(config, session, request);
        executionPlanFile = stub.readNotDeployedExecutionPlanConfigurationFile(executionPlanPath);

    }

    Boolean loadEditArea = true;
    String executionPlanFileConfiguratoin = executionPlanFile;

%>

<% if (loadEditArea) { %>
<script type="text/javascript">
    editAreaLoader.init({
                            id:"rawConfig"        // text area id
                            , syntax:"xml"            // syntax to be uses for highlighting
                            , start_highlight:true  // to display with highlight mode on start-up
                        });
</script>
<% } %>

<script type="text/javascript">
    function updateConfiguration(form, executionPlanName) {
        var newExecutionPlanConfig = "";

        if (document.getElementById("rawConfig") != null) {
            newExecutionPlanConfig = editAreaLoader.getValue("rawConfig");
        }

        var parameters = "?execPlanName=" + executionPlanName + "&execPlanConfig=" + newExecutionPlanConfig;

        jQuery.ajax({
                        type:"POST",
                        url:"edit_execution_plan_ajaxprocessor.jsp" + parameters,
                        contentType:"application/json; charset=utf-8",
                        dataType:"text",
                        data:{},
                        async:false,
                        success:function (msg) {
                            if (msg.trim() == "true") {
                                form.submit();
                            } else {
                                CARBON.showErrorDialog("Failed to add execution plan, Exception: " + msg);
                            }
                        }
                    });

    }

    function updateNotDeployedConfiguration(form, executionPlanPath) {
        var newExecutionPlanConfig = "";

        if (document.getElementById("rawConfig") != null) {
            newExecutionPlanConfig = editAreaLoader.getValue("rawConfig");
        }

        var parameters = "?execPlanPath=" + executionPlanPath + "&execPlanConfig=" + newExecutionPlanConfig;

        jQuery.ajax({
                        type:"POST",
                        url:"edit_execution_plan_ajaxprocessor.jsp" + parameters,
                        contentType:"application/json; charset=utf-8",
                        dataType:"text",
                        data:{},
                        async:false,
                        success:function (msg) {
                            if (msg.trim() == "true") {
                                form.submit();
                            } else {
                                CARBON.showErrorDialog("Failed to add execution plan, Exception: " + msg);
                            }
                        }
                    });

    }


    function resetConfiguration(form) {

        CARBON.showConfirmationDialog(
                "Are you sure you want to reset?", function () {
                    editAreaLoader.setValue("rawConfig", document.getElementById("rawConfig").value.trim());
                });

    }

</script>

<div id="middle">
    <h2><fmt:message key="edit.execution.plan.configuration"/></h2>

    <div id="workArea">
        <form name="configform" id="configform" action="index.jsp" method="get">
            <div id="saveConfiguration">
                            <span style="margin-top:10px;margin-bottom:10px; display:block;_margin-top:0px;">
                                <fmt:message key="save.advice"/>
                            </span>
            </div>
            <table class="styledLeft" style="width:100%">
                <thead>
                <tr>
                    <th>
                        <fmt:message key="execution.plan.configuration"/>
                    </th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td class="formRow">
                        <table class="normal" style="width:100%">
                            <tr>
                                <td id="rawConfigTD">
                                    <textarea name="rawConfig" id="rawConfig"
                                              style="border:solid 1px #cccccc; width: 99%; height: 400px; margin-top:5px;"><%=executionPlanFileConfiguratoin%>
                                    </textarea>

                                    <% if (!loadEditArea) { %>
                                    <div style="padding:10px;color:#444;">
                                        <fmt:message key="syntax.disabled"/>
                                    </div>
                                    <% } %>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
                <tr>
                    <td class="buttonRow">
                        <%
                            if (executionPlanName != null) {
                        %>

                        <button class="button"
                                onclick="updateConfiguration(document.getElementById('configform'),'<%=executionPlanName%>'); return false;">
                            <fmt:message
                                    key="update"/></button>

                        <%
                        } else if (executionPlanPath != null) {
                        %>
                        <button class="button"
                                onclick="updateNotDeployedConfiguration(document.getElementById('configform'),'<%=executionPlanPath%>'); return false;">
                            <fmt:message
                                    key="update"/></button>

                        <%
                            }
                        %>
                        <button class="button"
                                onclick="resetConfiguration(document.getElementById('configform')); return false;">
                            <fmt:message
                                    key="reset"/></button>
                    </td>
                </tr>
                </tbody>

            </table>

        </form>

    </div>
</div>
</fmt:bundle>