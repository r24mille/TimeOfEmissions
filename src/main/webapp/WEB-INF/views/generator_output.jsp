<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="toe" tagdir="/WEB-INF/tags/toe"%>
<!DOCTYPE html>
<html class="no-js">
<toe:head pageTitle="Generator Output"
	description="Electricity generation by fuel type" />
<body>
	<toe:pageHeader />
	<toe:pageContent>
		<div class="main wrapper clearfix">
			<article>
				<header>
					<h1>Electricity Generator Mix</h1>
				</header>
				<section>
					<h2>Aggregate Generator Output starting ${reportDate}</h2>
					<p>
						<form:form method="POST">
							<form:label path="startDateString">Start Date</form:label>
							<form:input path="startDateString" id="startDateString" />
							<script>
								$(function() {
									$("#startDateString").datepicker({
										changeMonth : true,
										changeYear : true
									});
								});
							</script>
							<form:label path="endDateString">End Date</form:label>
							<form:input path="endDateString" id="endDateString" />
							<script>
								$(function() {
									$("#endDateString").datepicker({
										changeMonth : true,
										changeYear : true
									});
								});
							</script>
							<input type="submit" value="Update" />
						</form:form>
					</p>
					<div id="agg_gen_chart" style="width: 100%; height: 500px;">
						<ul>
							<li>Observations: ${observations}</li>
							<li>Forecast: ${forecast}</li>
						</ul>
					</div>
				</section>
			</article>

		</div>
		<!-- #main -->
	</toe:pageContent>
	<toe:pageFooter />
</body>
</html>