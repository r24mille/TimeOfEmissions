// Extra right padding to fit legend
var _margin = {
	top : 0,
	right : 115,
	bottom : 75,
	left : 115
};
var _width = 1200 - _margin.left - _margin.right;
var _height = 600 - _margin.top - _margin.bottom;

var _x = d3.time.scale().range([ 0, _width ]);
var _y = d3.scale.linear().range([ _height, 0 ]);
var _xAxis = d3.svg.axis().scale(_x).orient("bottom").ticks(d3.time.hour, 2);
var _yAxis = d3.svg.axis().scale(_y).orient("left");

var _transitionDuration = 3000;

// Generator stack
var _stackArea = d3.svg.area().x(function(d) {
	return _x(d.date);
}).y0(function(d) {
	return _y(d.y0);
}).y1(function(d) {
	return _y(d.y0 + d.y);
});

var _stack = d3.layout.stack().values(function(d) {
	return d.values;
});

var _svg = d3.select("body").append("svg").attr("id", "toe_svg").attr("width",
		_width + _margin.left + _margin.right).attr("height",
		_height + _margin.top + _margin.bottom).append("g").attr("transform",
		"translate(" + _margin.left + "," + _margin.top + ")");

// Build diagonal hatch from http://www.carto.net/svg/samples/pattern1.svg
_hatchG = d3.select("#toe_svg").insert("defs", "g").append("pattern").attr(
		"id", "diagonalHatch").attr("patternUnits", "userSpaceOnUse").attr("x",
		0).attr("y", 0).attr("width", 10).attr("height", 10).append("g").attr(
		"style", "fill:none; stroke:#7e7e7e; stroke-width: 2.5;");
_hatchG.append("path").attr("d", "M0,0 l10,10");
_hatchG.append("path").attr("d", "M10,0 l-10,10");

function chartImpact(contextPath, iso, date) {
	d3
			.json(
					contextPath + "/toe_impact/iso/" + iso + "/date/" + date
							+ "/json",
					function(error, data) {


						// Use only the names of fuel types with values
						var usedFuelNames = d3.entries(data.generation).map(
								function(e) {
									if (e.value.some(function(v) {
										return v.scheduledMW;
									}) > 0) {
										return e.key;
									}
								}).filter(
								function(d) {
									return d !== undefined
											&& d !== "DISPATCHABLE_LOAD";
								}).concat([ "OVERSUPPLY" ]);

						// Normalize the domain of the x- and y-axis to time and
						// demand data respectively
						_x.domain(d3.extent(data.oversupply, function(d) {
							return d.date;
						}));

						_y.domain([
								0,
								d3.sum(d3.keys(data.generation), function(d) {
									return d3.max(data.generation[d]
											.map(function(e) {
												return e.scheduledMW;
											}));
								}) + 3000 ]); // Select max hourly demand +
												// 3000MW padding

						// Create Time-of-Use rate colored background
						d3
								.entries(data.rates)
								.map(
										function(r) {
											r.value
													.forEach(function(h) {
														_svg
																.append("rect")
																.attr("height",
																		_height)
																.attr(
																		"width",
																		(_x(h[1]) - _x(h[0])))
																.attr(
																		"transform",
																		"translate("
																				+ _x(h[0])
																				+ ",0)")
																.attr(
																		"fill",
																		data.colors[r.key]);
														_svg
																.append("text")
																.attr(
																		"x",
																		(_x(h[0]) + ((_x(h[1]) - _x(h[0])) / 2)))
																.attr("y", 26)
																.style(
																		"text-anchor",
																		"middle")
																.style("fill",
																		"white")
																.style(
																		"font-size",
																		"20px")
																.text(
																		capitalize(r.key));
													});
										});

						// Generators' area
						var generators = _stack(usedFuelNames
								.map(function(name) {
									if (d3.keys(data.generation).indexOf(name) > -1) {
										return {
											name : name,
											values : data.generation[name]
													.map(function(d, i) {
														return {
															date : d.date,
															y : d.scheduledMW
														};
													})
										};
									} else if (name === "OVERSUPPLY") {
										return {
											name : name,
											values : data.oversupply
													.map(function(d) {
														return {
															date : d.date,
															y : d.excess
														};
													})
										};
									}
								}));

						var generators_shift = _stack(usedFuelNames
								.map(function(name) {
									if (d3.keys(data.generationShift).indexOf(
											name) > -1) {
										return {
											name : name,
											values : data.generationShift[name]
													.map(function(d, i) {
														return {
															date : d.date,
															y : d.scheduledMW
														};
													})
										};
									} else if (name === "OVERSUPPLY") {
										return {
											name : name,
											values : data.oversupplyShift
													.map(function(d) {
														return {
															date : d.date,
															y : d.excess
														};
													})
										};
									}
								}));

						var generator = _svg.selectAll(".generator").data(
								generators).enter().append("g").attr("class",
								"generator");

						generator.append("path").attr("class", "area").attr(
								"d", function(d) {
									return _stackArea(d.values);
								}).attr("id", function(d, i) {
							return d.name + "-path";
						}).style("fill", function(d) {
							if (d.name === "OVERSUPPLY") {
								return "url(#diagonalHatch)";
							} else {
								return data.colors[d.name];
							}
						});

						// Create legend
						var legend = _svg.selectAll(".legend").data(
								usedFuelNames.reverse());

						legend.enter().append("g").attr("class", "legend")
								.attr("transform", function(d, i) {
									return "translate(0," + i * 20 + ")";
								});

						legend.append("rect").attr("x",
								(_width + _margin.right - 18))
								.attr("width", 18).attr("height", 18).attr(
										"id", function(a, i) {
											return a + "-legend";
										}).style("fill", function(d) {
									if (d === "OVERSUPPLY") {
										return "url(#diagonalHatch)";
									} else {
										return data.colors[d];
									}
								});

						legend.append("text").attr("x",
								(_width + _margin.right - 24)).attr("y", 8)
								.attr("dy", ".35em")
								.style("text-anchor", "end").text(function(d) {
									return capitalize(d);
								}).style("font-size", "13px");

						// Ticks on x- and y-axis
						_svg.append("g").attr("class", "x axis").attr(
								"transform", "translate(0," + _height + ")")
								.call(_xAxis).append("text").attr("x",
										(_width / 2)).attr("y",
										((_margin.bottom / 1.75))).style(
										"text-anchor", "middle").style(
										"font-size", "16px")
								.text("Time of Day");

						_svg.append("g").attr("class", "y axis").call(_yAxis)
								.append("text")
								.attr("transform", "rotate(-90)").attr("y",
										(0 - (_margin.left / 1.75))).attr("x",
										(0 - (_height / 2))).style(
										"text-anchor", "middle").style(
										"font-size", "16px").text(
										"Electricity Demand (MW)");

						// Transition on-click callback
						d3.select("body").on(
								"click",
								function(d) {
									// Update the day-ahead forecast
									// with the shifted plan
									_svg.selectAll(".generator").select("path")
											.data(generators_shift)
											.transition().duration(
													_transitionDuration)
											.attr("d", function(d) {
												return _stackArea(d.values);
											});

								});
					});
}

/**
 * Convert enum strings to human-friendly text.
 * 
 * @param text
 * @returns transformed text
 */
function capitalize(text) {
	if (text.contains("PEAK")) {
		return (text.charAt(0).toUpperCase() + text.slice(1).toLowerCase())
				.replace("_", "-");
	} else {
		return (text.charAt(0).toUpperCase() + text.slice(1).toLowerCase())
				.replace("_", " ");
	}
}