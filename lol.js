let myToken = undefined;
let ws = undefined
$( document ).ready( () => {
	console.log("ok?")
	$("#join").on('click', () => {
		console.log("Clicked!")
		const name = $('#name').val()
		joinGame(name)
	})

	ws = new WebSocket("ws://localhost:9000/ws");
	ws.onmessage = event => {
		let msg = JSON.parse(event.data)
		console.log(msg)
		switch(msg._type) {
			case "actors.Ok":
				if (!myToken) {
					myToken = msg.msg
				}
				break;
			case "actors.Ping":
				const pong = {
					_type: "actors.Pong",
					token: myToken
				}
				ws.send(JSON.stringify(pong))
				break;
			case "actors.NotifyClientsChanged":
				const players = msg.strings
				$("#clients").empty()
				players.forEach(player => {
					$("#clients").append("<li class='list-group-item'>" + player + "</li>")
				})
				break;
		}
	}
})

function joinGame(name) {
	console.log("Joining game with name: " + name)
	let msg = {
		_type: "actors.AssignName",
		name: name,
		token: myToken
	}
	ws.send(JSON.stringify(msg))
}

function startGame() {
	var ws = new WebSocket("ws://localhost:9000/ws");
	ws.onopen = _evt => {
		console.log("trying to start");
		ws.onmessage = evt => {
			console.log(evt.data);
			const msg = JSON.parse(evt.data);
			switch (msg._type) {
				case "actors.Ok":
				myToken = msg.msg;
				ws.send('{"_type": "actors.Ready", "token": "' + myToken + '"}');
				break;
			}
		}

	};
}
