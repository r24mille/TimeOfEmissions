<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="toe" tagdir="/WEB-INF/tags/toe"%>
<!DOCTYPE html>
<html class="no-js">
<toe:head  pageTitle="Introduction" description="Explore public data from North America's independent system operators" />
<body>
	<toe:pageHeader/>
	<toe:pageContent>
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
	</toe:pageContent>
	<toe:pageFooter/>
</body>
</html>