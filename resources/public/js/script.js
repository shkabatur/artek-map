
var canvas = document.getElementById("canvas")
var ctx = canvas.getContext("2d");
ctx.font = "italic 10pt Arial";

var map = new Image();
map.src = "img/map-night.jpg";

var green = new Image();
green.src = "img/green.png";

var red = new Image();
red.src = "img/red.png";

var nodes;

function draw_map() {
	ctx.drawImage(map,0,0,1910,859);
}

function draw_green(node) {
	ctx.drawImage(green,node.x,node.y,10,10);
	ctx.fillStyle = "#53DE0D";
	ctx.fillText(node.name, node.x, node.y);
}

function draw_red(node) {
	ctx.drawImage(red,node.x,node.y,10,10);
	ctx.fillStyle = "red";
	ctx.fillText(node.name, node.x, node.y);
}


map.onload = function() {
	draw_map()

	fetch('./nodes')
		.then(response => {
			return response.json()
		})
		.then(data => {
			draw_map();
			nodes = data;
			nodes.forEach( node => {
				if (node.result == true) {
					draw_green(node)
				} else {
					draw_red(node)
				}
			});
		});

	setInterval(function() {
		fetch('./nodes')
		.then(response => {
			return response.json()
		})
		.then(data => {
			draw_map();
			nodes = data;
			nodes.forEach( node => {
				if (node.result == true) {
					draw_green(node)
				} else {
					draw_red(node)
				}
			});
		});
	}, 5000);


}

