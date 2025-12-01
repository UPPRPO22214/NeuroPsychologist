import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import WelcomePage from '../pages/welcome-page';
import AuthPage from '../pages/authorization-page';
import RegistrationPage from '../pages/registration-page';
import ChatPage from '../pages/chat-page';

const AppRouter: React.FC = () => {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<WelcomePage />} />
        <Route path="/auth" element={<AuthPage />} />
        <Route path="/register" element={<RegistrationPage />} />
        <Route path="/chat" element={<ChatPage />} />
      </Routes>
    </Router>
  );
};

export default AppRouter;