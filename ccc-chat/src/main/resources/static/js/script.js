let websocket;

function connectWebSocket() {
    if (!username) {
        console.error("🚨 사용자 이름이 없습니다. WebSocket 연결 불가!");
        return;
    }

    websocket = new WebSocket("ws://localhost:8080/ws/chat");

    websocket.onopen = function () {
        console.log("✅ WebSocket 연결 성공!");
        sendJoinMessage();
    };

    websocket.onmessage = function (msg) {
        console.log("📩 수신된 메시지:", msg.data);

        let data;
        try {
            data = JSON.parse(msg.data);
        } catch (e) {
            console.error("🚨 메시지 JSON 파싱 실패:", e);
            return;
        }

        if (data.type === "join") {
            displayMessage("시스템", `${data.username}님이 입장하셨습니다.`);
        } else if (data.sender && data.message) {
            displayMessage(data.sender, data.message);
        } else {
            console.warn("🚨 알 수 없는 메시지 형식:", data);
        }
    };

    websocket.onclose = function () {
        console.warn("⚠️ WebSocket 연결 종료됨. 3초 후 재연결 시도...");
        setTimeout(connectWebSocket, 3000);
    };
}

// ✅ 채팅방 입장 메시지 전송
function sendJoinMessage() {
    if (!username) {
        console.warn("🚨 사용자 이름이 없습니다.");
        return;
    }

    let payload = JSON.stringify({
        type: "join",
        username: username
    });

    console.log(`📤 채팅방 입장 메시지 전송: ${payload}`);
    websocket.send(payload);
}

// ✅ 메시지 전송 함수
function sendMessage() {
    let messageInput = document.getElementById("messageInput");
    if (!messageInput) {
        console.error("🚨 메시지 입력 필드가 존재하지 않습니다!");
        return;
    }

    let message = messageInput.value.trim();
    if (!message) return;

    if (websocket && websocket.readyState === WebSocket.OPEN) {
        let payload = JSON.stringify({
            sender: username,
            message: message
        });

        console.log(`📤 전송된 메시지: ${payload}`);
        websocket.send(payload);
    } else {
        console.warn("🚨 WebSocket이 닫혀 있어 메시지를 전송할 수 없습니다.");
    }

    messageInput.value = ""; // 입력창 초기화
}

// ✅ Enter 키 입력 시 메시지 전송
function enterkey(event) {
    if (event.key === "Enter") {
        sendMessage();
    }
}

// ✅ 채팅 메시지 화면에 표시
function displayMessage(sender, message) {
    let chat = document.getElementById("chat");
    let messageItem = document.createElement("li");
    let messageContainer = document.createElement("div");
    let usernameLabel = document.createElement("div");
    let messageContent = document.createElement("div");

    messageContainer.classList.add("message-container");

    if (!sender || !message) {
        console.error("🚨 메시지 데이터가 올바르지 않습니다.", sender, message);
        return;
    }

    if (sender === username) {
        messageItem.className = "me";
    } else if (sender === "시스템") {
        messageItem.className = "system";
        messageContainer.textContent = message;
        messageContainer.classList.add("system-message");
        messageItem.appendChild(messageContainer);
        chat.appendChild(messageItem);
        chat.scrollTop = chat.scrollHeight;
        return;
    } else {
        messageItem.className = "you";
    }

    usernameLabel.className = "username";
    usernameLabel.textContent = sender;
    messageContent.className = "message";
    messageContent.textContent = message;

    messageContainer.appendChild(usernameLabel);
    messageContainer.appendChild(messageContent);
    messageItem.appendChild(messageContainer);

    chat.appendChild(messageItem);
    chat.scrollTop = chat.scrollHeight;
}

// ✅ 페이지 로드 시 WebSocket 연결
document.addEventListener("DOMContentLoaded", function () {
    connectWebSocket();
});
