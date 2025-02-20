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
    }, [accessToken]); // ✅ 의존성 배열 추가

    const deleteMessage = useCallback(async (messageId) => {
        try {
            console.log("🗑 삭제 요청 메시지 ID 확인:", messageId);

            const isDeleted = await checkMessageDeleted(messageId);
            if (isDeleted) {
                dispatchMessages({ type: "DELETE_MESSAGE", payload: messageId });
                console.log("✅ 메시지 삭제 완료:", messageId);
            }
        } catch (error) {
            console.error("🚨 메시지 삭제 확인 실패:", error);
        }
    }, [checkMessageDeleted]); // ✅ checkMessageDeleted 추가

    const sendJoinMessage = useCallback(() => {
        if (!email || !roomId || !websocket.current || websocket.current.readyState !== WebSocket.OPEN) return;

        const payload = JSON.stringify({
            type: "join",
            username: email,
            roomId: roomId
        });

        websocket.current.send(payload);
    }, [email, roomId]);

    const receiveMessage = useCallback((id, sender, text) => {
        dispatchMessages({
            type: "ADD_MESSAGE",
            payload: { id, sender, text }
        });

        setTimeout(() => {
            deleteMessage(id);
        }, 20000);
    }, [deleteMessage]); // ✅ deleteMessage 추가

    useEffect(() => {
        if (!email || !accessToken) {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
            return;
        }

        if (!websocket.current || websocket.current.readyState === WebSocket.CLOSED) {
            websocket.current = new WebSocket(`ws://localhost:8080/ws/chat/${roomId}`);

            websocket.current.onopen = () => {
                sendJoinMessage();
            };

            websocket.current.onmessage = async (event) => {
                try {
                    const data = JSON.parse(event.data);
                    console.log("📩 WebSocket으로 받은 메시지:", data);

                    if (data.type === "join") {
                        dispatchMessages({
                            type: "ADD_MESSAGE",
                            payload: { id: `join-${Date.now()}`, sender: "시스템", text: `${data.username}님이 참여했습니다.` }
                        });
                    }
                    else if (data.type === "delete") {
                        setTimeout(() => {
                            deleteMessage(data.messageId);
                        }, 2000);
                    }
                    else if (data.sender && data.type === "text" && data.message) {
                        displayMessage(data.sender, data.message);
                    }
                    else if (data.sender && data.type === "image" && data.imageUrl) {
                        console.log("🔍 이미지 메시지 수신:", data.imageUrl);
                        displayMessage(data.sender, null, data.imageUrl, undefined, undefined);
                    }
                    else if (data.sender && data.type === "file" && data.fileUrl && data.fileName) {
                        console.log("📂 파일 메시지 수신:", data.fileUrl, data.fileName);
                        displayMessage(data.sender, null, undefined, data.fileUrl, data.fileName);
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
                websocket.current.close();
            }
        };
    }, [roomId, accessToken, email, sendJoinMessage, deleteMessage, receiveMessage]);

    const sendMessage = async () => {
        if (!message.trim()) return;

        try {
            const response = await axios.post(
                "http://localhost:8080/chat/send",
                { sender: email, message, roomId },
                { headers: { Authorization: accessToken, "Content-Type": "application/json" } }
            );

            if (response.data.id) {
                dispatchMessages({
                    type: "ADD_MESSAGE",
                    payload: { id: response.data.id, sender: email, text: message }
                });

                setTimeout(() => {
                    deleteMessage(response.data.id);
                }, 20000);
            }
            setMessage("");
        } catch (error) {
            console.error("🚨 메시지 전송 실패:", error);
            alert("메시지 전송에 실패했습니다.");
        }
    };


    const handleImageUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);
        formData.append("sender", email);

        try {
            const response = await axios.post("http://localhost:8080/file/upload", formData, {
                headers: { Authorization: accessToken, "Content-Type": "multipart/form-data" }
            });

            const imageUrl = response.data.fileUrl;
            console.log("✅ 이미지 업로드 성공:", imageUrl);


            if (websocket.current && websocket.current.readyState === WebSocket.OPEN) {
                websocket.current.send(JSON.stringify({
                    sender: email,
                    imageUrl: imageUrl,
                    type: "image"
                }));
            }

            displayMessage(email, null, imageUrl, undefined, undefined );
        } catch (error) {
            console.error("🚨 이미지 업로드 실패:", error);
            alert("이미지 업로드에 실패했습니다.");
        }
    };

    const handleFileUpload = async (event) => {
        const file = event.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);
        formData.append("sender", email);

        try {
            const response = await axios.post("http://localhost:8080/file/upload", formData, {
                headers: { Authorization: accessToken, "Content-Type": "multipart/form-data" }
            });

            const fileUrl = response.data.fileUrl;
            const fileName = file.name;
            console.log("✅ 파일 업로드 성공:", fileUrl, fileName);

            if (websocket.current && websocket.current.readyState === WebSocket.OPEN) {
                websocket.current.send(JSON.stringify({
                    sender: email,
                    fileUrl: fileUrl,
                    fileName: fileName,
                    type: "file"
                }));
            }


            displayMessage(email, null, undefined, fileUrl, fileName);
        } catch (error) {
            console.error("🚨 파일 업로드 실패:", error);
            alert("파일 업로드에 실패했습니다.");
        }
    };
    const inviteUser = async () => {
        const inviteEmail = prompt("초대할 이메일을 입력하세요:");
        if (!inviteEmail) return;

        try {
            await axios.post(
                "http://localhost:8080/chat/room/invite",
                { roomId, email: inviteEmail },
                { headers: { Authorization: accessToken } }
            );
            alert("사용자를 초대했습니다!");
        } catch (error) {
            console.error("🚨 초대 실패:", error);
            alert("초대에 실패했습니다.");
        }
    };
    const displayMessage = (
        sender,
        text = null,
        imageUrl = undefined,
        fileUrl = undefined,
        fileName = undefined
    ) => {
        // 메시지를 하나 추가
        dispatchMessages({
            type: "ADD_MESSAGE",
            payload: {
                id: Date.now(),
                sender,
                text,
                imageUrl,
                fileUrl,
                fileName
            }
        });

        // 메시지 렌더링 후 자동 스크롤
        setTimeout(() => {
            if (chatContainerRef.current) {
                chatContainerRef.current.scrollTop = chatContainerRef.current.scrollHeight;
            }
        }, 100);
    };
    const leaveChatRoom = async () => {
        if (!window.confirm("정말로 채팅방을 나가시겠습니까?")) return;

        try {
            await axios.delete(`http://localhost:8080/chat/room/leave/${roomId}`, {
                headers: { Authorization: accessToken },
                data: { email }
            });

            alert("채팅방을 성공적으로 나갔습니다.");
            navigate("/");
        } catch (error) {
            console.error("🚨 채팅방 나가기 실패:", error);
            alert("채팅방 나가기에 실패했습니다.");
        }
    };

    return (
        <div id="container">
            <main>
                <ul id="chat" >
                    {messages.map((msg) => (
                        <li key={msg.id} className={msg.sender === email ? "me" : msg.sender === "시스템" ? "system" : "you"}>
                            <div className="message-container">
                                <div className="username">{msg.sender}</div>
                                {msg.imageUrl ? (
                                    <div className="message image-message">
                                        <img src={msg.imageUrl} alt="업로드된 이미지" />
                                    </div>
                                ) : msg.fileUrl ? (
                                    <div className="message file-message">
                                        <a href={msg.fileUrl} target="_blank" rel="noopener noreferrer">
                                            <img src="https://cdn-icons-png.flaticon.com/512/337/337946.png" alt="파일 아이콘" />
                                            <span className="file-name">{msg.fileName}</span>
                                        </a>
                                    </div>
                                ) : (
                                    <div className="message">{msg.text}</div>
                                )}
                            </div>
                        </li>
                    ))}
                </ul>

                <footer>
                    <input
                        id="messageInput"
                        type="text"
                        placeholder="입력하세요"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        onKeyDown={(e) => e.key === "Enter" && sendMessage()}
                    />
                    <input ref={imageInputRef} type="file" accept="image/*" style={{ display: "none" }} onChange={handleImageUpload} />
                    <img id="uploadImage" src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/1940306/ico_picture.png"
                         alt="이미지 업로드" onClick={() => imageInputRef.current.click()} />
                    <input ref={fileInputRef} type="file" accept=".pdf,.doc,.docx,.zip,.txt" style={{ display: "none" }} onChange={handleFileUpload} />
                    <img id="uploadFile" src="https://s3-us-west-2.amazonaws.com/s.cdpn.io/1940306/ico_file.png"
                         alt="파일 업로드" onClick={() => fileInputRef.current.click()} />
                </footer>

                <div className="button-container">
                    <button className="invite-button" onClick={inviteUser}>초대</button>
                    <button className="leave-button" onClick={leaveChatRoom}>나가기</button>
                </div>
            </main>
        </div>
    );
}

export default ChatRoom;
