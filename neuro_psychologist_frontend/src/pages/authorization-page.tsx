import React from 'react';
import '../styles/Auth.css';
import Header from '../components/Header';

const AuthPage = () => {
  const handleSubmit = (e) => {
    e.preventDefault();
    // Обработка формы авторизации
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
            <div className="form-group">
              <label className="sr-only" htmlFor="email">Email</label>
              <input
                className="form-input"
                id="email"
                name="email"
                type="email"
                placeholder="Email"
                autoComplete="email"
                required
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
                required
              />
            </div>
            
            <div className="form-group">
              <button className="submit-button" type="submit">
                Войти
              </button>
            </div>
          </form>
          
          <p className="auth-footer">
            Нет аккаунта?
            <a className="register-link" href="#">
              Зарегистрироваться
            </a>
          </p>
        </div>
      </main>
    </div>
  );
};

export default AuthPage;