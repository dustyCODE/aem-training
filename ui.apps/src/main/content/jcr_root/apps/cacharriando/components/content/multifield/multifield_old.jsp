<%--
  Multifield component.
  Multifield for footer
--%>
<%@include file="/libs/foundation/global.jsp"%><%
%><%@page session="false" %>
<%@ taglib prefix="widgets" uri="http://www.adobe.com/consulting/acs-aem-commons/widgets" %>
<%@ taglib prefix="xss" uri="http://www.adobe.com/consulting/acs-aem-commons/xss" %>

<cq:includeClientLib categories="linksFooters" />
 <c:set var="links" value="${widgets:getMultiFieldPanelValues(resource, 'links')}"/>
<div id="sites-menu">
<ul>
<c:forEach items="${links}" var="link">
    <li><a href="<c:out value="${xss:encodeForHTML(xssAPI, link['url'])}"></c:out>.html" >${xss:encodeForHTML(xssAPI, link['label'])}</a></li>
</c:forEach>
</ul>
</div>