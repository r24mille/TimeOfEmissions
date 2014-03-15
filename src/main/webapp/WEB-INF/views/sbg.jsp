<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="toe" tagdir="/WEB-INF/tags/toe"%>
<!DOCTYPE html>
<html class="no-js">
<toe:head pageTitle="Surplus Baseload Generation" description="Ontario's 10-day surplus baseload electricity generation forecast"/>
<body>
	<toe:pageHeader/>
	<toe:pageContent>
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
							<li>Generation: ${generation}</li>
							<li>Oversupply: ${oversupply}</li>
						</ul>
					</div>
				</section>
			</article>

		</div>
		<!-- #main -->
	</toe:pageContent>
	<toe:pageFooter/>
</body>
</html>