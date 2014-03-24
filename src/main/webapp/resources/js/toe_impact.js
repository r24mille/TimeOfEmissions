var _margin = {
	top : 20,
	right : 20,
	bottom : 30,
	left : 50
};
var _width = 960 - _margin.left - _margin.right;
var _height = 500 - _margin.top - _margin.bottom;

var _x = d3.time.scale().range([ 0, _width ]);
var _y = d3.scale.linear().range([ _height, 0 ]);
var _xAxis = d3.svg.axis().scale(_x).orient("bottom");
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

var _svg = d3.select("body").append("svg").attr("width",
		_width + _margin.left + _margin.right).attr("height",
		_height + _margin.top + _margin.bottom).append("g").attr("transform",
		"translate(" + _margin.left + "," + _margin.top + ")");

function chartImpact(contextPath, iso, date) {
	d3.json(contextPath + "/toe_impact/iso/" + iso + "/date/" + date + "/json",
			function(error, data) {
				var hours = [];
				for (var i = 0; i <= 23; i++) {
					hours.push(i);
				}

				// Use only the names of fuel types with values
				var usedFuelNames = d3.entries(data.generation).map(
						function(e) {
							if (e.value.some(function(v) {
								return v.scheduledMW;
							}) > 0) {
								return e.key;
							}
						}).filter(function(d) {
					return d !== undefined && d !== "DISPATCHABLE_LOAD";
				}).concat([ "OVERSUPPLY" ]);

				var generators = _stack(usedFuelNames.map(function(name) {
					if (d3.keys(data.generation).indexOf(name) > -1) {
						return {
							name : name,
							values : data.generation[name].map(function(d, i) {
								return {
									date : d.date,
									y : d.scheduledMW
								};
							})
						};
					} else if (name === "OVERSUPPLY") {
						return {
							name : name,
							values : data.oversupply.map(function(d) {
								return {
									date : d.date,
									y : d.excess
								};
							})
						};
					}
				}));

				var generators_shift = _stack(usedFuelNames.map(function(name) {
					if (d3.keys(data.generationShift).indexOf(name) > -1) {
						return {
							name : name,
							values : data.generationShift[name].map(function(d,
									i) {
								return {
									date : d.date,
									y : d.scheduledMW
								};
							})
						};
					} else if (name === "OVERSUPPLY") {
						return {
							name : name,
							values : data.oversupplyShift.map(function(d) {
								return {
									date : d.date,
									y : d.excess
								};
							})
						};
					}
				}));

				_x.domain(d3.extent(data.oversupply, function(d) {
					return d.date;
				}));

				_y.domain([ 0, d3.sum(d3.keys(data.generation), function(d) {
					return d3.max(data.generation[d].map(function(e) {
						return e.scheduledMW;
					}));
				}) ]);

				var generator = _svg.selectAll(".generator").data(generators)
						.enter().append("g").attr("class", "generator");

				generator.append("path").attr("class", "area").attr("d",
						function(d) {
							return _stackArea(d.values);
						}).attr("id", function(d, i) {
					return d.name + "-path";
				}).style("fill", function(d) {
					return data.colors[d.name];
				});

				generator.append("text").datum(function(d) {
					return {
						name : d.name,
						value : d.values[d.values.length - 1]
					};
				}).attr(
						"transform",
						function(d) {
							return "translate(" + _x(d.value.date) + ","
									+ _y(d.value.y0 + d.value.y / 2) + ")";
						}).attr("x", -75).attr("dy", ".35em").text(function(d) {
					return d.name;
				}).attr("id", function(d, i) {
					return d.name + "-text";
				});

				// Generators' area
				_svg.append("g").attr("class", "x axis").attr("transform",
						"translate(0," + _height + ")").call(_xAxis);

				_svg.append("g").attr("class", "y axis").call(_yAxis).append(
						"text").attr("transform", "rotate(-90)").attr("y", 6)
						.attr("dy", ".71em").style("text-anchor", "end").text(
								"Demand (MW)");

				// Transition on-click callback
				d3.select("body").on(
						"click",
						function(d) {
							_svg.selectAll(".generator").select("path").data(
									generators_shift).transition().duration(
									_transitionDuration).attr("d", function(d) {
								return _stackArea(d.values);
							});

							generators_shift.map(function(d) {
								if (d.name === "OVERSUPPLY") {
									if (d3.sum(d.values) == 0) {
										d3.select("#OVERSUPPLY-text")
												.transition().duration(
														_transitionDuration)
												.style("opacity", 0);
									}
								}
							})
						});
			});
}