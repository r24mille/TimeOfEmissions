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

<link rel="stylesheet" href="resources/css/normalize.min.css">
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
			<h1 class="title">Time of Emissions</h1>
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
					<h1>Surplus Baseload Generation (SBG)</h1>
					<p>
						Ontario's Independent Electricity System Operator (IESO) <a
							href="http://www.ieso.ca/imoweb/marketdata/sbg.asp"
							target="_blank">defines</a> surplus baseload generation as,
						"electricity production from baseload facilities (such as nuclear,
						hydro and wind) is greater than Ontario demand."
					</p>
					<p>
						<form:form method="POST">
							<form:label path="startDateString">Forecast Date</form:label><form:input path="startDateString" id="startDateString"/>
							 <script>
								$(function() {
									$( "#startDateString" ).datepicker({
									changeMonth: true,
									changeYear: true
									});
								});
							</script>
							<input type="submit" value="Update"/>
						</form:form>
					</p>
				</header>
				<section>
					<h2>SBG Forecast from ${reportDate}</h2>
					<div id="sbg_tou_chart" style="width: 100%; height: 475px;"></div>
					<script>
						var labelsJSON = ${labels};
						var dataJSON = ${data};
						var offpeak_color = "rgba(153, 193, 61, 0.6)";
						var midpeak_color = "rgba(250, 201, 15, 0.6)";
						var onpeak_color = "rgba(201, 90, 39, 0.6)";

						for (var i = 0; i < dataJSON.length; i++) {
							var dateStr = dataJSON[i][0];
							dataJSON[i][0] = new Date(dateStr);
						}

						var sbg_graph = new Dygraph(
						document.getElementById("sbg_tou_chart"),
						dataJSON,
						{
							title : "Surplus Baseload Generation Forecast (Hourly)",
							titleHeight : 75,
							xlabel : "Date",
							ylabel : "Megawatts (MW)",
							drawPoints : true,
							fillGraph: false,
							fillAlpha: 0.25,
							strokeWidth: 1.5,
							pointSize: 2,
							yAxisLabelWidth: 65,
							colors: ["#000000"],
							labelsDivWidth: 300,			
							labels : labelsJSON,
							axes: {
								x: {
									axisLabelFormatter: function(d) { return $.format.date(d, "MM/dd hp").replace(/\./g,""); }, 
								},
								y: {
									axisLabelFormatter: function(d) { 
										if (d!=0) {
											return d;
										} else {
											return '';
										}
									}
								}
							},
							underlayCallback : function(canvas, area, sbg_graph) {
								function highlight_period(x_start,
										x_end) {
									var canvas_left_x = sbg_graph
											.toDomXCoord(x_start);
									var canvas_right_x = sbg_graph
											.toDomXCoord(x_end);
									var canvas_width = canvas_right_x
											- canvas_left_x;
									canvas.fillRect(canvas_left_x,
											area.y, canvas_width,
											area.h);
								}
									
								for (var i = 0; i < sbg_graph.numRows(); i++) {
									var d = new Date(sbg_graph.getValue(i, 0));
									var dow = d.getDay();
									var hod = d.getHours();
									
									var start_x_highlight = sbg_graph.getValue(i, 0);
									var end_x_highlight = start_x_highlight + 3600 * 1000;
									
									if (dow == 0 || dow == 6 || hod < 7 || hod > 18) {
										canvas.fillStyle = offpeak_color;
									} else if ((hod > 6 && hod < 11) || (hod > 16 && hod < 19)) {
										canvas.fillStyle = onpeak_color;
									} else if (hod > 10 && hod < 17) {
										canvas.fillStyle = midpeak_color;
									}

									highlight_period(start_x_highlight,
											end_x_highlight);
								}
							}
						});
						
						$("div#sbg_tou_chart div.dygraph-title").parent().append("<div class=\"top_legend\"></div>");
						$("div#sbg_tou_chart div.top_legend")
							.append("<ul class=\"top_legend_row\"><li class=\"period_offpeak\">Off-peak Hours</li><li class=\"period_midpeak\">Mid-peak Hours</li><li class=\"period_onpeak\">On-peak Hours</li></ul>");
						$("div#sbg_tou_chart div.top_legend")
							.append("<ul class=\"top_legend_row\"><li class=\"plot_line\">Surplus Baseload Generation</li></ul>");

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
</body>
</html>