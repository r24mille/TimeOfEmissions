<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<!DOCTYPE html>
<html class="no-js">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title></title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width">

<link rel="stylesheet" href="resources/css/normalize.css">
<link rel="stylesheet" href="resources/css/main.css">
<link rel="stylesheet"
	href="resources/css/ui-lightness/jquery-ui-1.10.4.custom.css">
<script src="resources/js/jquery-1.10.1.min.js"></script>
<script src="resources/js/main.js"></script>
<script src="resources/js/jquery-dateFormat.js"></script>
<script src="resources/js/jquery-ui-1.10.4.custom.js"></script>
<script src="resources/js/dygraph-combined.js"></script>
</head>
<body>

	<div class="header-container">
		<header class="wrapper clearfix">
			<h1 class="title"><a href="./">Time of Emissions</a></h1>
			<nav>
				<ul>
					<li><a href="./sbg">Surplus Baseload Generation</a></li>
					<li><a href="./generator_output">Ontario Generation Mix</a></li>
					<li><a href="./emissions">Emissions</a></li>
				</ul>
			</nav>
		</header>
	</div>

	<div class="main-container">
		<div class="main wrapper clearfix">
			<article>
				<header>
					<h1>About This Project</h1>
					<p>This Time-of-Emissions project is currently under
						development. Eventually it intends to visualize electricity
						production in North America using data from independent system
						operators.</p>
					<p>
						It will also implement <a
							href="http://energychallenge.energy.gov/a/dtd/Green-Energy-Tracker/12606-26122"
							target="_blank">a RESTful API to this data</a>, pursuant to the <a
							href="http://energychallenge.energy.gov/" target="_blank">Department
							of Energy's American Energy Data Challenge #2</a>.
					</p>
					<p>
						The source code of this application is available <a
							href="https://github.com/r24mille/TimeOfEmissions"
							target="_blank">on github under an Apache license</a>.
					</p>
					<p>Use the navigation in the upper-right to browse through the
						three views of electricity data. This design will improve and
						sections will be more informative soon.</p>
				</header>
				<section>
					<h2>Data Sources</h2>
					<p>
						Currently, the primary data source for this application is from
						the <a href="http://www.ieso.ca" target="_blank">Independent
							Electricity System Operator</a>. The library for parsing this library
						is available <a
							href="https://github.com/r24mille/IesoPublicReportBindings"
							target="_blank">on github under an Apache license</a>.
					</p>
				</section>
			</article>

		</div>
		<!-- #main -->
	</div>
	<!-- #main-container -->

	<div class="footer-container">
		<footer class="wrapper">
			<h3>Work in Progress</h3>
		</footer>
	</div>
</body>
</html>