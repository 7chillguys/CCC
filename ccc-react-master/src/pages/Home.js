import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";


function Home() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [chatRooms, setChatRooms] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const accessToken = localStorage.getItem("AccessToken");
        const user = localStorage.getItem("user");
        if (accessToken && user) {
            setIsLoggedIn(true);
            fetchChatRooms(user);
        }

        const handleStorageChange = () => {
            const accessToken = localStorage.getItem("AccessToken");
            const user = localStorage.getItem("user");
            if (accessToken && user) {
                setIsLoggedIn(true);
                fetchChatRooms(user);
            } else {
                setIsLoggedIn(false);
            }
        };

        window.addEventListener("storage", handleStorageChange);

        return () => {
            window.removeEventListener("storage", handleStorageChange);
        };
    }, []);

    const fetchChatRooms = async (email) => {
        try {
            const response = await axios.get("${process.env.REACT_APP_API_URL}/chat/room/list", {
                params: { email },
            });
            setChatRooms(response.data);
        } catch (error) {
            console.error("채팅방 목록 불러오기 실패:", error);
        }
    };

    const handleCreateChatRoom = async () => {
        const roomName = prompt("새 채팅방 이름을 입력하세요:");
        if (!roomName) return;

        const userEmail = localStorage.getItem("user"); // ✅ 현재 로그인된 사용자 이메일

        try {
            const response = await axios.post("${process.env.REACT_APP_API_URL}/chat/room/create", {
                name: roomName,
                email: userEmail,
            });

            console.log("채팅방 생성 성공:", response.data);
            setChatRooms([...chatRooms, response.data]);
        } catch (error) {
            console.error("채팅방 생성 실패:", error);
            alert("채팅방 생성에 실패했습니다.");
        }
    };

    const enterChatRoom = (roomId) => {
        window.location.href = `/chatroom/${roomId}`;
    };

    const handleLogout = async () => {
        try {
            const accessToken = localStorage.getItem("AccessToken");
            const user = localStorage.getItem("user");

            if (!accessToken || !user) {
                console.error("로그인 정보가 없습니다.");
                return;
            }

            const response = await axios.post(
                "/auth/logout",
                {},
                {
                    headers: {
                        "X-Auth-User": user,
                        Authorization: accessToken,
                    },
                }
            );

            console.log("로그아웃 성공:", response.data);

            localStorage.removeItem("AccessToken");
            localStorage.removeItem("RefreshToken");
            localStorage.removeItem("user");

            setIsLoggedIn(false);
        } catch (error) {
            console.error("로그아웃 오류:", error);
            alert("로그아웃에 실패했습니다.");
        }
    };

    const handleLogin = () => {
        navigate("/login");
    };

    const handleSignup = () => {
        navigate("/signup");
    };

    return (
        <div>
            <h2>메인 페이지</h2>
            {isLoggedIn ? (
                <>
                    <p>로그인 상태: {localStorage.getItem("user")}</p>
                    <button onClick={handleLogout}>로그아웃</button>
                    <button onClick={handleCreateChatRoom}>채팅방 생성</button> {/* ✅ 채팅방 생성 버튼 추가 */}

                    <h3>채팅방 목록</h3>
                    {chatRooms.length > 0 ? (
                        <ul>
                            {chatRooms.map((room) => (
                                <li key={room.id} onClick={() => enterChatRoom(room.id)} style={{ cursor: "pointer", textDecoration: "underline" }}>
                                    {room.name}
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <p>참여 중인 채팅방이 없습니다.</p>
                    )}
                </>
            ) : (
                <>
                    <button onClick={handleLogin}>로그인</button>
                    <button onClick={handleSignup}>회원가입</button>
                </>
            )}
        </div>
    );
}

export default Home;
