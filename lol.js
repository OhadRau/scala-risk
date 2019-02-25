let myToken = undefined;
let ws = undefined;
let rooms = undefined;
let clients = undefined;
let chatHistory = {};
let currentlyTalkingTo = "";

$(document).ready(() => {
    $("#join").on('click', () => {
        const name = $('#name').val();
        joinGame(name)
    });
    $("#create-room").on('click', () => {
        console.log("Create room clicked!");
        $("#create-room-modal").modal()
    });
    $("#create-room-ok").on('click', () => {
        const roomName = $('#roomNameInput').val()
        createRoom(roomName);
    })

    ws = new WebSocket("ws://localhost:9000/ws");
    ws.onmessage = event => {
        let msg = JSON.parse(event.data);
        console.log(msg);
        switch (msg._type) {
            case "actors.Token":
                if (!myToken) {
                    myToken = msg.token
                }
                listRooms();
                break;
            case "actors.Ping":
                const pong = {
                    _type: "actors.Pong",
                    token: myToken
                };
                ws.send(JSON.stringify(pong));
                break;
            case "actors.NotifyClientsChanged":
                const players = msg.strings;
                $("#clients").empty();
                clients = players;
                players.forEach(player => {
                    $("#clients").append(`<a class='list-group-item list-group-item-action clients' clientName="${player.name}" publicToken="${player.publicToken}"> ${player.name} </a>`)
                });
                $("#create-room").removeClass("disabled");
                $(".clients").on("click", function() {
                    console.log("clients clicked");
                    const publicToken = $(this).attr("publicToken");
                    const name = $(this).attr("clientName");
                    openChatBox(name, publicToken);
                })
                break;
            case "actors.NotifyRoomsChanged":
                rooms = msg.rooms;
                updateRooms();
                break;
            case "actors.UserMessage":
                const message = msg.message;
                const senderName = msg.senderName;
                const publicToken = msg.publicToken;
                const timestamp = msg.timestamp;
                if (!chatHistory.hasOwnProperty(publicToken)) {
                    chatHistory[publicToken] = ""
                }
                if (currentlyTalkingTo == "") {
                    chatHistory[publicToken] += `\n${senderName}: ${message}`;
                    openChatBox(senderName, publicToken);
                } else {
                    chatHistory[currentlyTalkingTo] += `\n${timestamp} ${senderName}: ${message}`;
                    $("#chatModalText").text(chatHistory[currentlyTalkingTo]);
                }
                break;
        }
    }
});

function joinGame(name) {
    console.log("Joining game with name: " + name);
    let msg = {
        _type: "actors.AssignName",
        name: name,
        token: myToken
    };
    ws.send(JSON.stringify(msg))
}

function updateRooms() {
    $("#players-table").empty();
    rooms.forEach((room, index) => {
        $("#players-table").append(`
<tr class="row clickable-row" roomid="${room.roomId}">
    <th class="col-sm-1" scope="row">${index}</th>
    <td class="col-sm-4">${room.name}</td>
    <td class="col-sm-4">${room.hostName}</td>
    <td class="col-sm-3">${room.numClients} / 6</td>
</tr>`)
    })
    $(".clickable-row").on('click', function() {
        console.log("Clicked!");
        console.log($(this));
        const roomId = $(this).attr("roomid");
        console.log("roomId:");
        console.log(roomId);
        joinRoom(roomId);
    })
}

function createRoom(roomName) {
    console.log("Creating room with name: " + roomName);
    let msg = {
        _type: "actors.CreateRoom",
        roomName: roomName,
        token: myToken
    };
    console.log(JSON.stringify(msg));
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

function joinRoom(roomId) {
    let msg = {
        _type: "actors.JoinRoom",
        token: myToken,
        roomId: roomId
    };
    console.log(JSON.stringify(msg));
    ws.send(JSON.stringify(msg))
}

function listRooms() {
    let msg = {
        _type: "actors.ListRoom",
        token: myToken
    };
    ws.send(JSON.stringify(msg))
}

function openChatBox(name, publicToken) {
    console.log("public token is: " + publicToken);
    if (!chatHistory.hasOwnProperty(publicToken)) {
        chatHistory[publicToken] = "";
    }
    console.log(chatHistory[publicToken]);
    currentlyTalkingTo=publicToken;
    $("#chatBoxModal").on('hide.bs.modal', () => {
        currentlyTalkingTo = "";
    })
    $("#chatBoxModal").modal();
    $("#chatModalTitle").text(name);
    $("#chatModalText").text(chatHistory[publicToken]);
    $("#chatModalSend").attr("publicToken", publicToken);
    $("#chatModalSend").on("click", function(e) {
        e.preventDefault();
        const msg = $("#chatInput").val();
        const publicToken = $(this).attr("publicToken");
        sendMessage(msg, publicToken);
        $("#chatInput").val('');
    })
}

function sendMessage(message, publicToken) {
    let msg = {
        _type: "actors.MessageToUser",
        token: myToken,
        recipientPublic: publicToken,
        message: message
    };
    ws.send(JSON.stringify(msg))
}
