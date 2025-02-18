import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
axios.defaults.baseURL = process.env.REACT_APP_API_URL;

function Home() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const accessToken = localStorage.getItem('AccessToken');
        const user = localStorage.getItem('user');
        if (accessToken && user) {
            setIsLoggedIn(true);
        }

        const handleStorageChange = () => {
            const accessToken = localStorage.getItem('AccessToken');
            const user = localStorage.getItem('user');
            if (accessToken && user) {
                setIsLoggedIn(true);
            } else {
                setIsLoggedIn(false);
            }
        };

        window.addEventListener('storage', handleStorageChange);

        return () => {
            window.removeEventListener('storage', handleStorageChange);
        };
    }, []);

    const handleLogout = async () => {
        try {
            const accessToken = localStorage.getItem('AccessToken');
            const user = localStorage.getItem('user');

            if (!accessToken || !user) {
                console.error('로그인 정보가 없습니다.');
                return;
            }

            const response = await axios.post(
                '/auth/logout',
                {},
                {
                    headers: {
                        'X-Auth-User': user,
                        'Authorization': accessToken,
                    },
                }
            );

            console.log('로그아웃 성공:', response.data);

            localStorage.removeItem('AccessToken');
            localStorage.removeItem('RefreshToken');
            localStorage.removeItem('user');

            setIsLoggedIn(false);

        } catch (error) {
            console.error('로그아웃 오류:', error);
            alert('로그아웃에 실패했습니다.');
        }
    };

    const handleLogin = () => {
        navigate('/login');
    };

    const handleSignup = () => {
        navigate('/signup');
    };

    const enterChatRoom = () => {
        const email = localStorage.getItem("user");

        if (!email) {
            alert("로그인이 필요합니다.");
            return;
        }

        window.location.href = "/chatroom"; // ✅ React에서 ChatRoom.js 페이지로 이동
    };



    return (
        <div>
            <h2>메인 페이지</h2>
            {isLoggedIn ? (
                <>
                    <p>로그인 상태: {localStorage.getItem('user')}</p>
                    <button onClick={handleLogout}>로그아웃</button>
                    <button onClick={enterChatRoom}>채팅방 입장</button> {/* ✅ 채팅방 입장 버튼 추가 */}
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
