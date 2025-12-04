import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthService } from '../services/auth.service';
import { useAuthStore } from '../store/auth.store';
import '../styles/Auth.css';
import Header from '../components/Header';

const AuthPage: React.FC = () => {
  const navigate = useNavigate();
  const login = useAuthStore((state) => state.login);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const response = await AuthService.login({ email, password });
      
      // Update auth store with token and user data
      login(response.token, {
        id: response.id,
        username: response.username,
        email: response.email
      });
      
      // Redirect to chat page after successful login
      navigate('/chat');
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Ошибка входа');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <Header />
      
      <main className="auth-main">
        <div className="auth-form-container">
          <div className="auth-header-text">
            <h1 className="auth-title">Добро пожаловать в Mindful</h1>
            <p className="auth-subtitle">Ваш личный помощник для душевного равновесия.</p>
          </div>
          
          <form className="auth-form" onSubmit={handleSubmit}>
            {error && (
              <div style={{
                color: '#ef4444',
                backgroundColor: '#fee2e2',
                padding: '12px',
                borderRadius: '8px',
                marginBottom: '16px',
                fontSize: '14px'
              }}>
                {error}
              </div>
            )}
            
            <div className="form-group">
              <label className="sr-only" htmlFor="email">Email</label>
              <input
                className="form-input"
                id="email"
                name="email"
                type="email"
                placeholder="Email"
                autoComplete="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
                disabled={loading}
              />
            </div>
            
            <div className="form-group">
              <label className="sr-only" htmlFor="password">Пароль</label>
              <input
                className="form-input"
                id="password"
                name="password"
                type="password"
                placeholder="Пароль"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                disabled={loading}
              />
            </div>
            
            <div className="form-group">
              <button className="submit-button" type="submit" disabled={loading}>
                {loading ? 'Вход...' : 'Войти'}
              </button>
            </div>
          </form>
          
          <p className="auth-footer">
            Нет аккаунта?
            <a
              className="register-link"
              href="#"
              onClick={(e) => {
                e.preventDefault();
                navigate('/register');
              }}
            >
              Зарегистрироваться
            </a>
          </p>
        </div>
      </main>
    </div>
  );
};

export default AuthPage;