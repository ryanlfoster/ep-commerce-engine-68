<%
final String image = request.getParameter("image");
String alt = request.getParameter("alt");
if (alt == null) {
	alt = "";
}
%>
<html>
<head>
<title></title>
<script language="javascript"><!--
var i=0;
function resize() {
if (navigator.appName == 'Netscape') i=40;
window.resizeTo(document.images[0].width +30, document.images[0].height+60-i);
}
//--></script>
</head>
<body onload="resize();">
<% if (image != null) { %>
<a href="javascript:parent.close()"><img src="<%=image%>" alt="<%=alt%>" border="0"></a>
<% } %>
</body>
</html>