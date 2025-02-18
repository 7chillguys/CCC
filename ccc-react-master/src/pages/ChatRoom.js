import React, { useState, useEffect, useRef, useCallback } from "react";
import axios from "axios";
import "./ChatRoom.css"; // ✅ 스타일 적용

function ChatRoom() {
    const [messages, setMessages] = useState([]);
    const [message, setMessage] = useState("");
    const email = localStorage.getItem("user") || "Guest";
    const accessToken = localStorage.getItem("AccessToken");
    const websocket = useRef(null);

    // ✅ `sendJoinMessage`를 useCallback으로 감싸서 의존성 문제 해결
    const sendJoinMessage = useCallback(() => {
        if (!email || !websocket.current || websocket.current.readyState !== WebSocket.OPEN) return;

        const payload = JSON.stringify({
            type: "join",
            username: email
        });

        console.log(`📤 채팅방 입장 메시지 전송: ${payload}`);
        websocket.current.send(payload);
    }, [email]);

    useEffect(() => {
        if (!email || !accessToken) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            return;
        }

        if (!websocket.current || websocket.current.readyState === WebSocket.CLOSED) {
            websocket.current = new WebSocket(process.env.REACT_APP_WEBSOCKET_URL);

            websocket.current.onopen = () => {
                console.log("✅ WebSocket 연결 성공!");
                sendJoinMessage(); // ✅ useCallback을 사용했기 때문에 안전
            };

            websocket.current.onmessage = (event) => {
                console.log("📩 새 메시지:", event.data);
                try {
                    const data = JSON.parse(event.data);

                    if (data.sender === "시스템") {
                        setMessages((prev) => [...prev, { sender: "시스템", text: data.message }]);
                    } else if (data.sender && data.message) {
                        displayMessage(data.sender, data.message);
                    }
                } catch (e) {
                    console.error("🚨 메시지 JSON 파싱 실패:", e);
                }
            };

            websocket.current.onclose = () => {
                console.warn("⚠️ WebSocket 연결 종료됨.");
            };
        }

        return () => {
            if (websocket.current && websocket.current.readyState === WebSocket.OPEN) {
                console.warn("🔌 컴포넌트 언마운트 시 WebSocket 닫기");
                websocket.current.close();
            }
        };
    }, [accessToken, email, sendJoinMessage]); // ✅ useCallback을 의존성 배열에 추가하여 무한 렌더링 방지

    const sendMessage = async () => {
        if (!message.trim()) return;

        try {
            await axios.post(
                "/chat/send",
                { sender: email, message },
                { headers: { Authorization: accessToken, "Content-Type": "application/json" } }
            );

            displayMessage(email, message);
            setMessage("");
        } catch (error) {
            console.error("🚨 메시지 전송 실패:", error);
            alert("메시지 전송에 실패했습니다.");
        }
    };

    const displayMessage = (sender, text) => {
        setMessages((prevMessages) => {
            if (prevMessages.some((msg) => msg.sender === sender && msg.text === text)) {
                return prevMessages;
            }
            return [...prevMessages, { sender, text }];
        });
    };

    return (
        <div id="container">
            <main>
                <ul id="chat">
                    {messages.length === 0 ? (
                        <li className="system">{email}님이 입장했습니다.</li>
                    ) : (
                        messages.map((msg, index) => (
                            <li key={index}
                                className={msg.sender === email ? "me" : msg.sender === "시스템" ? "system" : "you"}>
                                <div className="message-container">
                                    <div className="username">{msg.sender}</div>
                                    <div className="message">{msg.text}</div>
                                </div>
                            </li>
                        ))
                    )}
                </ul>
                <footer>
                    <input
                        id="messageInput"
                        type="text"
                        placeholder="메시지를 입력하세요"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                    />
                    <input id="fileInput" type="file" style={{ display: "none" }} accept="image/*, .pdf, .doc, .docx" />
                    <img id="uploadImage" src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/1940306/ico_picture.png"
                         alt="이미지 업로드" />
                    <img id="uploadFile" src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/1940306/ico_file.png"
                         alt="파일 업로드" />
                </footer>
            </main>
        </div>
    );
}

export default ChatRoom;
