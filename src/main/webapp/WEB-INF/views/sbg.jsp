<%@ page contentType="text/html; charset=UTF-8"%>
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

<script src="resources/js/vendor/modernizr-2.6.2-respond-1.1.0.min.js"></script>
<script src="resources/js/Chart.js"></script>
</head>
<body>

	<div class="header-container">
		<header class="wrapper clearfix">
			<h1 class="title">Time of Emissions</h1>
			<nav>
				<ul>
					<li><a href="#">Navigation One</a></li>
					<li><a href="#">Navigation Two</a></li>
					<li><a href="#">Navigation Three</a></li>
				</ul>
			</nav>
		</header>
	</div>
	
	${model.data}

	<div class="main-container">
		<div class="main wrapper clearfix">

			<article>
				<header>
					<h1>Demo</h1>
					<p>My first attempt at creating something with the Chart.js
						library.</p>
				</header>
				<section>
					<h2>Here Goes!</h2>
					<ol>
						<li>labels = ${labels}</li>
						<li>data = ${data}</li>
					</ol>
					<canvas id="myChart" width="2500" height="1000"></canvas>
					<script>
						var labelsJSON = ${labels};
						var dataJSON = ${data};
						var data = {
							labels : [1, "", 2, "", 3, "", 4, "", 5, "", 6, "", 7, ""],
							datasets : [ {
								fillColor : "rgba(220,220,220,0.5)",
								strokeColor : "rgba(220,220,220,1)",
								pointColor : "rgba(220,220,220,1)",
								pointStrokeColor : "#fff",
								data : dataJSON
							}]
						};

						//Get the context of the canvas element we want to select
						var ctx = document.getElementById("myChart")
								.getContext("2d");
						new Chart(ctx).Line(data);
					</script>
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

	<!-- 	<script -->
	<!-- 		src="https://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script> -->
	<script>
		window.jQuery
				|| document
						.write('<script src="js/vendor/jquery-1.10.1.min.js"><\/script>')
	</script>

	<script src="js/main.js"></script>
</body>
</html>
