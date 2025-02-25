import React, { useState } from 'react';
import axios from 'axios';
import {useNavigate} from "react-router-dom";

function SignUp() {
    const [empId, setEmpId] = useState('');
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [department, setDepartment] = useState('');
    const [position, setPosition] = useState('');
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();

        try {
             await axios.post(`${process.env.REACT_APP_API_URL}/user/signup`, {
                empId,
                name,
                password,
                department,
                position,
                email,
            });

            setMessage('회원가입 성공!');
            alert('회원가입 성공! 이메일 인증을 진행해주세요.');
            //홈으로 이동
            navigate('/');
        } catch (error) {
            if (error.response) {
                setMessage('회원가입 실패: ' + error.response.data);
            } else {
                setMessage('Error: ' + error.message);
            }
        }
    };

    return (
        <div>
            <h2>회원가입</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label>사번</label>
                    <input
                        type="text"
                        value={empId}
                        onChange={(e) => setEmpId(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>이름</label>
                    <input
                        type="text"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
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
                <div>
                    <label>부서</label>
                    <input
                        type="text"
                        value={department}
                        onChange={(e) => setDepartment(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>직급</label>
                    <input
                        type="text"
                        value={position}
                        onChange={(e) => setPosition(e.target.value)}
                        required
                    />
                </div>
                <div>
                    <label>이메일</label>
                    <input
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <button type="submit">회원가입</button>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
}

export default SignUp;
