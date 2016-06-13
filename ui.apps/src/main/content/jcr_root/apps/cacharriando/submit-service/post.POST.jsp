<%@include file="/libs/fd/af/components/guidesglobal.jsp" %>
<%@include file="/libs/foundation/global.jsp"%>
<%@page import="com.day.cq.wcm.foundation.forms.FormsHelper,
             org.apache.sling.api.resource.ResourceUtil,
             org.apache.sling.api.resource.ValueMap,
             com.globant.julian.cacharriando.core.services.HandleForm" %>
<%@taglib prefix="sling"
                uri="http://sling.apache.org/taglibs/sling/1.0" %>
<%@taglib prefix="cq"
                uri="http://www.day.com/taglibs/cq/1.0"
%>
<cq:defineObjects/>
<sling:defineObjects/>
<%

    String contactName = request.getParameter("contactName");
    String descriptionName = request.getParameter("contactDescription");

    HandleForm hf = sling.getService(com.globant.julian.cacharriando.core.services.HandleForm.class);
    hf.processData(contactName, descriptionName);
 
 
%>