<%@include file="/libs/fd/af/components/guidesglobal.jsp" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.foundation.forms.FormsHelper,
             org.apache.sling.api.resource.ResourceUtil,
             org.apache.sling.api.resource.ValueMap" %>
<%@taglib prefix="sling"
                uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq"
                uri="http://www.day.com/taglibs/cq/1.0"
%>
<cq:defineObjects/>
<sling:defineObjects/>
<form method="post" action="<%= formHelper.getAction(currentPage) %>">
    <%= formHelper.getFormInputsHTML(form) %>

    <fieldset>
        <legend>Form Demo</legend>

        <div><%= form.getError("myField") %></div>
        <label <%= form.hasError("myField") ? "class=\"error\"" : "" %>>My Field:</label>
        <input type="text" name="myField" value="<%= form.get("myField") %>"/>
        <input type="submit" value="Submit"/>
    </fieldset>
</form>