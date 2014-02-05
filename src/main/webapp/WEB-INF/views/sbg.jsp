<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html class="no-js">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<title></title>
<meta name="description" content="">
<meta name="viewport" content="width=device-width">

<link rel="stylesheet" href="resources/css/normalize.min.css">
<link rel="stylesheet" href="resources/css/main.css">

<script src="resources/js/dygraph-combined.js"></script>
</head>
<body>

	<div class="header-container">
		<header class="wrapper clearfix">
			<h1 class="title">Time of Emissions</h1>
			<nav>
				<ul>
					<li><a href="./sbg">Surplus Baseload Generation</a></li>
					<li><a href="#">Ontario Generation Mix</a></li>
				</ul>
			</nav>
		</header>
	</div>

	<div class="main-container">
		<div class="main wrapper clearfix">

			<article>
				<header>
					<h1>Surplus Baseload Generation (SBG)</h1>
					<p>
						Ontario's Independent Electricity System Operator (IESO) <a
							href="http://www.ieso.ca/imoweb/marketdata/sbg.asp"
							target="_blank">defines</a> surplus baseload generation as, 
						"electricity production from baseload facilities (such as nuclear,
						hydro and wind) is greater than Ontario demand."
					</p>
				</header>
				<section>
					<h2>
						SBG Forecast from
						<fmt:formatDate type="date" value="${reportDate}"
							dateStyle="medium" />
					</h2>
					<div id="myChart" style="width: 100%; height: 475px;"></div>
					<script>
						var labelsJSON = ${labels};
						var dataJSON = ${data};

						for (var i = 0; i < dataJSON.length; i++) {
							var dateStr = dataJSON[i][0];
							dataJSON[i][0] = new Date(dateStr);
						}

						new Dygraph(
								document.getElementById("myChart"),
								dataJSON,
								{
									title : 'Surplus Baseload Generation Forecast',
									xlabel : 'Date',
									ylabel : 'Megawatts (MW)',
									drawPoints : true,
									valueRange : [ 0.0, 3250.0 ],
									labels : labelsJSON
								});
					</script>
					<ol>
						<li>labels = ${labels}</li>
						<li>data = ${data}</li>
					</ol>
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

	<script>
		window.jQuery
				|| document
						.write('<script src="resources/js/vendor/jquery-1.10.1.min.js"><\/script>');
	</script>

	<script src="resources/js/main.js"></script>
</body>
</html>
