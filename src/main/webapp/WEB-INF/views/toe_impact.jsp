<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="toe" tagdir="/WEB-INF/tags/toe"%>
<!DOCTYPE html>
<html class="no-js">
<toe:head pageTitle="Time-of-Emissions Forecast Impact Simulation"
	description="Simulate the ideal impact of a time-of-emissions signal with automated demand responsef" />
<body>
	<h1>Day-Ahead Electricity Generation Forecast in Ontario for June 5, 2013</h1>
	<script src="<%=request.getContextPath()%>/resources/js/toe_impact.js"></script>
	<script>
		chartImpact("${pageContext.request.contextPath}", "IESO", "${date}");
	</script>
</body>
</html>