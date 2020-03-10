
canvas = document.getElementById("canvas");
ctx = canvas.getContext("2d");
ctx.font = "italic 10pt Arial";
var c_x = 10;
var c_y = 10;
var file;
map = new Image();
map.src = "img/map.jpg";
green = new Image();
green.src = "img/green.png";
red = new Image();
red.src = "img/red.png";


var nodes = {
	nodes_list : null,
	current_node_id: null,

	nodes_list_init : function() {
		let id = 0;
		this.nodes_list.forEach( (node) => {
			node.is_visible = true;
			node.id = id; id += 1;
		});
	},
	change_node_name: function(new_name) {
		this.nodes_list[this.current_node_id].name = new_name;
	},
	change_node_ip: function(new_ip) {
		this.nodes_list[this.current_node_id].ip = new_ip;
	},
	move_from_to: {
		from: null,
		to: null,
		node_id: null
	},
	is_click_on_node: function(x,y) {
		let x1 = x - 5;
		let x2 = x + 5;
		let y1 = y - 5;
		let y2 = y + 5;
		let id = -1;

		this.nodes_list.forEach( (node) => {
			if ( (node.x >= x1) && (node.y >= y1) && (node.x <= x2) && (node.y <= y2)) {
				//alert(node.name);
				id = node.id;
			}
		});
		return id;
	},
	move_to: function(newX, newY) {
		this.nodes_list[this.moving.node_id].x = newX;
		this.nodes_list[this.moving.node_id].y = newY;
	},
	moving: {
		is_moving: false,
		from: null,
		node_id: null
	},
	show_nodes: function() {
		this.nodes_list.map( (node) => {
			if (node.is_visible) {
				ctx.drawImage(green,node.x, node.y, c_x, c_y);
				ctx.fillStyle = "#53DE0D";
				ctx.fillText(node.name, node.x + 10, node.y + 10);
			}
		})
	},


};


load_nodes.onclick= function() {
	fetch('./nodes')
		.then(response => {
			return response.json()
		})
		.then(data => {
			nodes.nodes_list = data;

			nodes.nodes_list_init()
			nodes.show_nodes()

			setInterval(function(){
				ctx.drawImage(map,0,0,1910,859);
				nodes.show_nodes();
			}, 50);
		});
}

canvas.onclick = (e) => {
	if (nodes.moving.is_moving) {
		nodes.move_to(e.layerX-c_x/2, e.layerY-c_y/2);
		nodes.moving = {
			is_moving: false,
			from: null,
			node_id: null
		}
	} else {
		node_id = nodes.is_click_on_node(e.layerX-c_x/2, e.layerY-c_y/2);
		if (node_id >= 0) {
			nodes.moving.is_moving = true;
			nodes.moving.from = { x: e.layerX, y: e.layerY};
			nodes.moving.node_id = node_id;
		}
	}
}
canvas.ondblclick = (e) => {
	node_id = nodes.is_click_on_node(e.layerX-c_x/2, e.layerY-c_y/2);
	if (node_id >= 0) {
		if (e.altKey) { // Если нажат альт, то удаляем узел
			nodes.nodes_list.splice(node_id, 1)
		} else {
			new_node_name.value = nodes.nodes_list[node_id].name;
			new_node_ip.value =  nodes.nodes_list[node_id].ip;
			nodes.current_node_id = node_id;
		}
	} else {
		node = {
			x : e.layerX-c_x/2,
			y : e.layerY-c_y/2,
			is_visible: true,
			id: nodes.nodes_list.length,
			name: "test",
      ip: "127.0.0.1",
		};
		nodes.nodes_list.push(node);
	}
}

canvas.onmousemove = (e) => {
	if ( nodes.moving.is_moving ) {
		ctx.beginPath();
		ctx.moveTo(nodes.moving.from.x, nodes.moving.from.y);
		ctx.lineTo(e.layerX, e.layerY);
		ctx.stroke();
	}
}


map.onload = function() {
	ctx.drawImage(map,0,0,1910,859);

}


change_name.onclick = (e) => {
	nodes.change_node_name(new_node_name.value);
}

change_ip.onclick = (e) => {
	nodes.change_node_ip(new_node_ip.value);
}

save_button.onclick = (e) => {
	fetch('./change-nodes', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json',
		},
		body: JSON.stringify(nodes.nodes_list)
	})
}
