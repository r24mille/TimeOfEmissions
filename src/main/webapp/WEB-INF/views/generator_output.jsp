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
					<h1>Electricity Generator Mix</h1>
				</header>
				<section>
					<h2>
						Aggregate Generator Output starting ${reportDate}
					</h2>
					<p>
					<form:form method="POST">
							<form:input path="startDateString" id="startDateString"/>
							 <script>
								$(function() {
									$( "#startDateString" ).datepicker({
									changeMonth: true,
									changeYear: true
									});
								});
							</script>
							<form:input path="endDateString" id="endDateString"/>
							 <script>
								$(function() {
									$( "#endDateString" ).datepicker({
									changeMonth: true,
									changeYear: true
									});
								});
							</script>
							<input type="submit" value="Update"/>
						</form:form>
					</p>
					<div id="agg_gen_chart" style="width: 100%; height: 500px;"></div>
					<script>
						var aggregateLabelsJSON = ${aggregateLabels};
						var aggregateDataJSON = ${aggregateData};
						var aggregateColorsJSON = ${aggregateColors};
						var offpeak_color = "rgba(153, 193, 61, 0.6)";
						var midpeak_color = "rgba(250, 201, 15, 0.6)";
						var onpeak_color = "rgba(201, 90, 39, 0.6)";

						for (var i = 0; i < aggregateDataJSON.length; i++) {
							var dateStr = aggregateDataJSON[i][0];
							aggregateDataJSON[i][0] = new Date(dateStr);
						}

						var sbg_graph = new Dygraph(
						document.getElementById("agg_gen_chart"),
						aggregateDataJSON,
						{
							title : "Aggregate Generator Output by Fuel Type (Hourly)",
							titleHeight : 100,
							xlabel : "Date",
							ylabel : "Megawatts (MW)",
							drawPoints : true,
							fillGraph: false,
							fillAlpha: 1,
							strokeWidth: 1.5,
							pointSize: 0,
							yAxisLabelWidth: 65,
							colors: aggregateColorsJSON,
							labelsDivWidth: 300,			
							labels : aggregateLabelsJSON,
							axes: {
								x: {
									axisLabelFormatter: function(d) { return $.format.date(d, "MM/dd (") + $.format.date(d, "ddd").substring(0,3) + ")"; }
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
						
						$("div#agg_gen_chart div.dygraph-title").parent().append("<div class=\"top_legend\"></div>");
						$("div#agg_gen_chart div.top_legend")
							.append("<ul class=\"top_legend_row\"><li class=\"period_offpeak\">Off-peak Hours</li><li class=\"period_midpeak\">Mid-peak Hours</li><li class=\"period_onpeak\">On-peak Hours</li></ul>");
						$("div#agg_gen_chart div.top_legend")
							.append("<ul class=\"top_legend_row\"><li class=\"plot_line nuclear_line\">Nuclear</li><li class=\"plot_line hydro_line\">Hydroelectric</li><li class=\"plot_line wind_line\">Wind</li></ul>");
						$("div#agg_gen_chart div.top_legend")
						.append("<ul class=\"top_legend_row\"><li class=\"plot_line gas_line\">Gas</li><li class=\"plot_line coal_line\">Coal</li><li class=\"plot_line other_line\">Other</li></ul>");
					</script>
				</section>
				<section>
					<h2>
						Individual Generator Output starting ${reportDate} 
					</h2>
					<div id="ind_gen_chart" style="width: 100%; height: 550px;"></div>
					<script>
						var individualLabelsJSON = ${individualLabels};
						var individualDataJSON = ${individualData};
						var individualColorsJSON = ${individualColors};
						var offpeak_color = "rgba(153, 193, 61, 0.6)";
						var midpeak_color = "rgba(250, 201, 15, 0.6)";
						var onpeak_color = "rgba(201, 90, 39, 0.6)";

						for (var i = 0; i < individualDataJSON.length; i++) {
							var dateStr = individualDataJSON[i][0];
							individualDataJSON[i][0] = new Date(dateStr);
						}

						var sbg_graph = new Dygraph(
						document.getElementById("ind_gen_chart"),
						individualDataJSON,
						{
							title : "Individual Generator Output",
							titleHeight : 75,
							xlabel : "Date",
							ylabel : "Megawatts (MW)",
							drawPoints : true,
							fillGraph: false,
							fillAlpha: 0.25,
							strokeWidth: 1.5,
							pointSize: 2,
							yAxisLabelWidth: 65,
							colors: individualColorsJSON,
							labelsDivWidth: 300,			
							labels : individualLabelsJSON,
							axes: {
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
						
						$("div#ind_gen_chart div.dygraph-title").parent().append("<div class=\"top_legend\"></div>");
						$("div#ind_gen_chart div.top_legend")
							.append("<ul class=\"top_legend_row\"><li class=\"period_offpeak\">Off-peak Hours</li><li class=\"period_midpeak\">Mid-peak Hours</li><li class=\"period_onpeak\">On-peak Hours</li></ul>");
						$("div#ind_gen_chart div.top_legend")
							.append("<ul class=\"top_legend_row\"><li class=\"plot_line\">Power Generated</li></ul>");

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