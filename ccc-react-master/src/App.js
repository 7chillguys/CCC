import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Home from "./pages/Home";
import Login from "./pages/Login";
import SignUp from "./pages/SignUp";
import ChatRoom from "./pages/ChatRoom"; // ✅ ChatRoom 가져오기

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/login" element={<Login />} />
                <Route path="/signup" element={<SignUp />} />
                <Route path="/chatroom/:roomId" element={<ChatRoom />} /> {/* ✅ 동적 라우트 추가 */}
            </Routes>
        </Router>
    );
}

export default App;
