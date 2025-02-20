import React, { useState, useEffect, useRef, useReducer, useCallback } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./ChatRoom.css";

function ChatRoom() {
    const { roomId } = useParams();
    const navigate = useNavigate();
    const [message, setMessage] = useState("");
    const email = localStorage.getItem("user") || "Guest";
    const accessToken = localStorage.getItem("AccessToken");
    const websocket = useRef(null);
    const imageInputRef = useRef(null);
    const fileInputRef = useRef(null);
    const chatContainerRef = useRef(null);

    const messagesReducer = (state, action) => {
        switch (action.type) {
            case "ADD_MESSAGE":
                return state.some(msg => msg.id === action.payload.id) ? state : [...state, action.payload];
            case "DELETE_MESSAGE":
                return state.filter(msg => msg.id !== action.payload);
            default:
                return state;
        }
    };

    const [messages, dispatchMessages] = useReducer(messagesReducer, []);

    const checkMessageDeleted = useCallback(async (messageId) => {
        try {
            const response = await axios.get(`http://localhost:8080/chat/check/${messageId}`, {
                headers: { Authorization: accessToken }
            });
            return response.data.deleted;
        } catch (error) {
            console.error("🚨 메시지 삭제 상태 확인 실패:", error);
            return false;
        }
    }, [accessToken]);

    const deleteMessage = useCallback(async (messageId) => {
        try {
            const isDeleted = await checkMessageDeleted(messageId);
            if (isDeleted) {
                dispatchMessages({ type: "DELETE_MESSAGE", payload: messageId });
            }
        } catch (error) {
            console.error("🚨 메시지 삭제 확인 실패:", error);
        }
    }, [checkMessageDeleted]);

    const sendJoinMessage = useCallback(() => {
        if (!email || !roomId || !websocket.current || websocket.current.readyState !== WebSocket.OPEN) return;

        websocket.current.send(JSON.stringify({ type: "join", username: email, roomId }));
    }, [email, roomId]);

    useEffect(() => {
        if (!email || !accessToken) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            return;
        }

        if (!websocket.current || websocket.current.readyState === WebSocket.CLOSED) {
            websocket.current = new WebSocket(`ws://localhost:8080/ws/chat/${roomId}`);

            websocket.current.onopen = sendJoinMessage;

            websocket.current.onmessage = async (event) => {
                try {
                    const data = JSON.parse(event.data);

                    if (data.type === "join") {
                        dispatchMessages({
                            type: "ADD_MESSAGE",
                            payload: { id: `join-${Date.now()}`, sender: "시스템", text: `${data.username}님이 참여했습니다.` }
                        });
                    } else if (data.type === "delete") {
                        setTimeout(() => deleteMessage(data.messageId), 2000);
                    } else if (data.sender && data.type === "text" && data.message) {
                        dispatchMessages({ type: "ADD_MESSAGE", payload: { id: data.id, sender: data.sender, text: data.message } });
                    } else if (data.sender && data.type === "image" && data.imageUrl) {
                        dispatchMessages({ type: "ADD_MESSAGE", payload: { id: data.id, sender: data.sender, imageUrl: data.imageUrl } });
                    } else if (data.sender && data.type === "file" && data.fileUrl && data.fileName) {
                        dispatchMessages({ type: "ADD_MESSAGE", payload: { id: data.id, sender: data.sender, fileUrl: data.fileUrl, fileName: data.fileName } });
                    }
                } catch (e) {
                    console.error("🚨 메시지 JSON 파싱 실패:", e);
                }
            };

            websocket.current.onclose = () => console.warn("⚠️ WebSocket 연결 종료됨.");
        }

        return () => {
            if (websocket.current && websocket.current.readyState === WebSocket.OPEN) {
                websocket.current.close();
            }
        };
    }, [roomId, accessToken, email, sendJoinMessage, deleteMessage]);

    const handleUpload = async (event, type) => {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);
        formData.append("sender", email);

        try {
            const response = await axios.post("http://localhost:8080/file/upload", formData, {
                headers: { Authorization: accessToken, "Content-Type": "multipart/form-data" }
            });

            const payload = { sender: email, type, [`${type}Url`]: response.data.fileUrl };
            if (type === "file") payload.fileName = file.name;

            websocket.current.send(JSON.stringify(payload));
            dispatchMessages({ type: "ADD_MESSAGE", payload });
        } catch (error) {
            console.error(`🚨 ${type} 업로드 실패:`, error);
        }
    };

    return (
        <div id="container">
            <main>
                <ul id="chat" ref={chatContainerRef}>
                    {messages.map((msg) => (
                        <li key={msg.id} className={msg.sender === email ? "me" : msg.sender === "시스템" ? "system" : "you"}>
                            <div className="message-container">
                                <div className="username">{msg.sender}</div>
                                {msg.imageUrl ? (
                                    <img src={msg.imageUrl} alt="업로드된 이미지" className="message-image" />
                                ) : msg.fileUrl ? (
                                    <a href={msg.fileUrl} target="_blank" rel="noopener noreferrer">{msg.fileName}</a>
                                ) : (
                                    <div className="message">{msg.text}</div>
                                )}
                            </div>
                        </li>
                    ))}
                </ul>
                <footer>
                    <input type="text" value={message} onChange={(e) => setMessage(e.target.value)} onKeyDown={(e) => e.key === "Enter" && sendMessage()} />
                    <input ref={imageInputRef} type="file" accept="image/*" hidden onChange={(e) => handleUpload(e, "image")} />
                    <input ref={fileInputRef} type="file" accept=".pdf,.doc,.docx,.zip,.txt" hidden onChange={(e) => handleUpload(e, "file")} />
                </footer>
            </main>
        </div>
    );
}

export default ChatRoom;
