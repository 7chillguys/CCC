import React, { useState } from 'react';
import axios from 'axios';
import {useNavigate} from "react-router-dom";
axios.defaults.baseURL = process.env.REACT_APP_API_URL;

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [message, setMessage] = useState('');
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
            // 로그인 요청
            const response = await axios.post('/auth/login', {
                email,
                password,
            }, {
                withCredentials: true,
            });
            console.log('Response Headers:', response.headers);
            // 응답에서 헤더 가져오기
            const accessToken = response.headers['accesstoken'];
            const refreshToken = response.headers['refreshtoken'];
            const user = response.headers['x-auth-user'];

            // 토큰과 사용자 정보를 상태에 저장
            localStorage.setItem('AccessToken', accessToken);
            localStorage.setItem('RefreshToken', refreshToken);
            localStorage.setItem('user', user);

            // 토큰을 콘솔에 출력
            console.log('AccessToken:', accessToken);
            console.log('RefreshToken:', refreshToken);
            console.log('User:', user);

            setMessage('로그인 성공!');
            setIsLoggedIn(true);

            // 홈으로 이동
            navigate('/');
        } catch (error) {
            if (error.response) {
                setMessage('로그인 실패: ' + error.response.data);
            } else {
                setMessage('Error: ' + error.message);
            }
        }
    };

    return (
        <div>
            <h2>로그인</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>이메일</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>비밀번호</label>
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">로그인</button>
            </form>
            {message && <p>{message}</p>}
            {isLoggedIn && <p>로그인 상태: {localStorage.getItem('user')}</p>}
        </div>
    );
}

export default Login;
