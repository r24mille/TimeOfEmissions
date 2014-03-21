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

var _color = d3.scale.category10();

// SBG area
var _area = d3.svg.area().x(function(d) {
	return _x(d.date);
}).y0(_height).y1(function(d) {
	return _y(d.megawatts);
});

// Export threshold line
var _line = d3.svg.line().x(function(d) {
	return _x(d.date);
}).y(function(d) {
	return _y(d.exportThreshold);
}).interpolate("basis");

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
				// Color for each fuel type
				_color.domain(d3.keys(data.generation).concat(["OVERSUPPLY"]));

				var generators = _stack(_color.domain().map(function(name) {
					if(d3.keys(data.generation).indexOf(name) > -1) {
						return {
							name : name,
							values : data.generation[name].map(function(d) {
								return {
									date : d.date,
									y : d.megawatts
								};
							})
						};
					} else if (name === "OVERSUPPLY") {
						
						return {
							name : name,
							values : data.oversupply.map(function(d) {
								return {
									date : d.date,
									y : d.megawatts
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
						return e.megawatts;
					}));
				}) ]);

				var generator = _svg.selectAll(".generator").data(generators)
						.enter().append("g").attr("class", "generator");

				generator.append("path").attr("class", "area").attr("d",
						function(d) {
							return _stackArea(d.values);
						}).style("fill", function(d) {
					return _color(d.name);
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
				});

				// SBG area
				_svg.append("g").append("path").datum(data.oversupply)
						.attr("class", "area").attr("d", _stackArea).style(
								"fill", function(d) {
									return _color(d.name);
								});
				// _svg.append("path").datum(data.oversupply)
				// .attr("class", "area").attr("d", _area);

				// Export threshold line
				// _svg.append("path").datum(data.oversupply).attr("class",
				// "threshold").attr("d", _line);

				_svg.append("g").attr("class", "x axis").attr("transform",
						"translate(0," + _height + ")").call(_xAxis);

				_svg.append("g").attr("class", "y axis").call(_yAxis).append(
						"text").attr("transform", "rotate(-90)").attr("y", 6)
						.attr("dy", ".71em").style("text-anchor", "end").text(
								"Demand (MW)");
			});
}