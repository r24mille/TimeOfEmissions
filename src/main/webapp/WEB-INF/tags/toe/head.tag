<%@ attribute name="pageTitle" required="true" type="java.lang.String"%>
<%@ attribute name="description" required="true" type="java.lang.String"%>
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title>Time of Emissions : ${pageTitle} : ${description}</title>
<meta name="description" content="${description}">
<meta name="viewport" content="width=device-width">

<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/normalize.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/main.css">
<link rel="stylesheet"
	href="<%=request.getContextPath()%>/resources/css/ui-lightness/jquery-ui-1.10.4.custom.css">
<script
	src="<%=request.getContextPath()%>/resources/js/jquery-1.10.1.min.js"></script>
<script src="<%=request.getContextPath()%>/resources/js/main.js"></script>
<script
	src="<%=request.getContextPath()%>/resources/js/jquery-dateFormat.js"></script>
<script
	src="<%=request.getContextPath()%>/resources/js/jquery-ui-1.10.4.custom.js"></script>
<script
	src="<%=request.getContextPath()%>/resources/js/dygraph-combined.js"></script>
<script
	src="<%=request.getContextPath()%>/resources/js/vendor/d3/d3.min.js"></script>

<style>
body {
	font: 10px sans-serif;
}

.axis path, .axis line {
	fill: none;
	stroke: #000;
	shape-rendering: crispEdges;
}

.area {
	fill: steelblue;
}

.threshold {
	fill: orange;
	stroke: orange;
}
</style>
</head>