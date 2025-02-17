import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import SignUp from './pages/SignUp';  // 회원가입 페이지
import Home from './pages/Home';      // 홈 페이지
import Login from './pages/Login';    // 로그인 페이지
import ChatRoom from "./pages/ChatRoom";

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/signup" element={<SignUp />} />
                <Route path="/login" element={<Login />} />
                <Route path="/chatroom" element={<ChatRoom />} />
            </Routes>
        </Router>
    );
}

export default App;
