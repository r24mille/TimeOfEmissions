<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="toe" tagdir="/WEB-INF/tags/toe"%>
<!DOCTYPE html>
<html class="no-js">
<toe:head pageTitle="Emissions"
	description="Electricity generation emissions by fuel type" />
<body>
	<toe:pageHeader />
	<toe:pageContent>

		<div class="main wrapper clearfix">
			<article>
				<header>
					<h1>Carbon Dioxide Emissions</h1>
				</header>
				<section>
					<h2>Emissions by fuel type starting ${reportDate}</h2>
					<p>
						<form:form method="POST">
							<form:label path="startDateString">Start Date</form:label>
							<form:input path="startDateString" id="startDateString" />
							<script>
								$(function() {
									$( "#startDateString" ).datepicker({
									changeMonth: true,
									changeYear: true
									});
								});
							</script>
							<form:label path="endDateString">End Date</form:label>
							<form:input path="endDateString" id="endDateString" />
							<script>
								$(function() {
									$( "#endDateString" ).datepicker({
									changeMonth: true,
									changeYear: true
									});
								});
							</script>
							<input type="submit" value="Update" />
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
							title : "Emissions by Fuel Type (Hourly)",
							titleHeight : 100,
							xlabel : "Time of Day",
							ylabel : "Carbon Dioxide Emissions (metric tonnes)",
							drawPoints : true,
							fillGraph: false,
							fillAlpha: 1,
							strokeWidth: 1.5,
							pointSize: 0,
							yAxisLabelWidth: 80,
							colors: aggregateColorsJSON,
							labelsDivWidth: 300,			
							labels : aggregateLabelsJSON,
							stackedGraph : true,
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
						
						$("div#agg_gen_chart div.dygraph-title").parent().append("<div class=\"top_legend\"></div>");
						$("div#agg_gen_chart div.top_legend")
							.append("<ul class=\"top_legend_row\"><li class=\"period_offpeak\">Off-peak Hours</li><li class=\"period_midpeak\">Mid-peak Hours</li><li class=\"period_onpeak\">On-peak Hours</li></ul>");
						$("div#agg_gen_chart div.top_legend")
							.append("<ul class=\"top_legend_row\"><li class=\"plot_line nuclear_sq\">Nuclear</li><li class=\"plot_line hydro_sq\">Hydroelectric</li><li class=\"plot_line wind_sq\">Wind</li></ul>");
						$("div#agg_gen_chart div.top_legend")
						.append("<ul class=\"top_legend_row\"><li class=\"plot_line gas_sq\">Natural Gas</li><li class=\"plot_line coal_sq\">Coal</li><li class=\"plot_line other_sq\">Other</li></ul>");
					</script>
				</section>
			</article>

		</div>
		<!-- #main -->
	</toe:pageContent>
	<toe:pageFooter />
</body>
</html>