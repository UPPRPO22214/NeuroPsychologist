import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/WelcomePage.css';
import Header from '../components/Header';

const WelcomePage: React.FC = () => {
    const navigate = useNavigate();
    return (
        <div className="app">
            <div className="layout-container">
                <Header />

                <main className="main">
                    <div className="container">
                        <div className="content-wrapper">
                            <section className="hero-section">
                                <div className="hero-content">
                                    <h1 className="hero-title">Ваш личный помощник в мире психологии</h1>
                                    <h2 className="hero-subtitle">Mindful - это инновационный чат-бот, созданный для поддержки вашего эмоционального благополучия. Получите доступ к профессиональным советам и техникам самопомощи в любое время и в любом месте.</h2>
                                    <div className="hero-button-container">
                                        <button className="btn btn-hero" onClick={() => navigate('/auth')}>
                                            <span className="btn-text">Начать бесплатно</span>
                                        </button>
                                    </div>
                                </div>
                            </section>

                            <section className="features-section">
                                <div className="features-header">
                                    <h2 className="features-title">Почему выбирают Mindful?</h2>
                                    <p className="features-description">Mindful предлагает уникальный подход к психологической поддержке, сочетая передовые технологии и экспертные знания.</p>
                                </div>
                                <div className="features-grid">
                                    <div className="feature-card">
                                        <div className="feature-icon">
                                            <span className="material-symbols-outlined">chat_bubble</span>
                                        </div>
                                        <div className="feature-content">
                                            <h3 className="feature-title">Круглосуточная поддержка</h3>
                                            <p className="feature-text">Получите помощь в любое время суток, когда вам это необходимо.</p>
                                        </div>
                                    </div>
                                    <div className="feature-card">
                                        <div className="feature-icon">
                                            <span className="material-symbols-outlined">favorite</span>
                                        </div>
                                        <div className="feature-content">
                                            <h3 className="feature-title">Индивидуальный подход</h3>
                                            <p className="feature-text">Чат-бот адаптируется к вашим потребностям и предлагает персонализированные решения.</p>
                                        </div>
                                    </div>
                                    <div className="feature-card">
                                        <div className="feature-icon">
                                            <span className="material-symbols-outlined">lock</span>
                                        </div>
                                        <div className="feature-content">
                                            <h3 className="feature-title">Конфиденциальность</h3>
                                            <p className="feature-text">Ваши данные надежно защищены, и общение остается анонимным.</p>
                                        </div>
                                    </div>
                                </div>
                            </section>

                            <section className="capabilities-section">
                                <div className="capabilities-header">
                                    <div className="capabilities-text">
                                        <h2 className="capabilities-title">Что вы можете сделать с Mindful?</h2>
                                        <p className="capabilities-description">Mindful предоставляет широкий спектр инструментов и техник для улучшения вашего психического здоровья.</p>
                                    </div>
                                    <button className="btn btn-primary">
                                        <span className="btn-text">Начать</span>
                                    </button>
                                </div>
                                <div className="capabilities-grid">
                                    <div className="capability-item">
                                        <div className="capability-image" style={{backgroundImage: 'url("https://lh3.googleusercontent.com/aida-public/AB6AXuC6miZY-NY0e5bRWng0GWi-1rX6cgYquz2W0Bfs0R9T_BNCWMT6Grs1GxZJublxkdkCxZo5Nf2g6tRZTRCXsO8LN8AyGAXb7VYfi7ovLRWs_bbFJBeHSsNXfxFb7IKyg0VUuGWhoBlIxSntZgRtLSVeYE9uY5OTpFUuIMw351eDiYIstCb2eYbzENLzSi2OE-RyYfwo6p5r_xD80T--ByJL-Did7SwEscBTHS1jwknVijASd5ob0UFqPQr56nG_mPdniJFpJPlFokgi")'}}></div>
                                        <div>
                                            <h3 className="capability-item-title">Медитации и релаксация</h3>
                                            <p className="capability-item-text">Практикуйте осознанность и снижайте уровень стресса с помощью наших медитативных техник.</p>
                                        </div>
                                    </div>
                                    <div className="capability-item">
                                        <div className="capability-image" style={{backgroundImage: 'url("https://lh3.googleusercontent.com/aida-public/AB6AXuAttohPiqJg4T2zmL2meW_BdKTXq20FuXCvQ0em1PjCUCcxDJnzHPFBMhiaiiH-nPltHvJqpTPh31Rlt2OlFeFzjTWNZhdIxQd8yCc4Wyygx0ViU1KaSrVglsRjjeAHrjlQNBpqJdIdfOnlHyOomuOl-zgHe5jM2XI-sKbmzxmknAZ4gDURmfzwEThcW7oz1CSK6hIz0wBBjX0Svq8CvQimquG4rPUjAc_LzWzeY4nuxbRbY3nJ0UnNVfoHjJ-_xY5MjqVikjnGpM6V")'}}></div>
                                        <div>
                                            <h3 className="capability-item-title">Разговорная терапия</h3>
                                            <p className="capability-item-text">Выражайте свои чувства и получайте поддержку в безопасной и конфиденциальной обстановке.</p>
                                        </div>
                                    </div>
                                    <div className="capability-item">
                                        <div className="capability-image" style={{backgroundImage: 'url("https://lh3.googleusercontent.com/aida-public/AB6AXuAWyyu951iW9p3NhIiqkq7-UNE8SEzc_z20Fsv5vv7EGQoy-0dr2FqZb9t8hrNs4830KMba9cfUGSWkyqcKPnKxdq02WXjdaphR97gyooYyowU2B271_eLGLRDDlrAX9CRZKJzwtxcUGyvE1z9VIPRSchd4rRwdIgxx-HH9AuCxM1uZD0T7noAz8pMu6kSGP1norKKqyY70qWiBPEc1o9oY0CYPBrTSJenf50mYrW_lsnvcrVq1ixka6E8HfNtfVMpvW7rDbA8CXDK8")'}}></div>
                                        <div>
                                            <h3 className="capability-item-title">Позитивное мышление</h3>
                                            <p className="capability-item-text">Развивайте оптимизм и уверенность в себе с помощью наших упражнений на позитивное мышление.</p>
                                        </div>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                </main>

                <footer className="footer">
                    <div className="footer-container">
                        <div className="footer-content">
                            <div className="footer-links">
                                <a className="footer-link" href="#">Политика конфиденциальности</a>
                                <a className="footer-link" href="#">Условия использования</a>
                                <a className="footer-link" href="#">Связаться с нами</a>
                            </div>
                            <div className="footer-social">
                                <a className="social-link" href="#">
                                    <svg className="social-icon" fill="currentColor" viewBox="0 0 256 256" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M247.39,68.94A8,8,0,0,0,240,64H209.57A48.66,48.66,0,0,0,168.1,40a46.91,46.91,0,0,0-33.75,13.7A47.9,47.9,0,0,0,120,88v6.09C79.74,83.47,46.81,50.72,46.46,50.37a8,8,0,0,0-13.65,4.92c-4.31,47.79,9.57,79.77,22,98.18a110.93,110.93,0,0,0,21.88,24.2c-15.23,17.53-39.21,26.74-39.47,26.84a8,8,0,0,0-3.85,11.93c.75,1.12,3.75,5.05,11.08,8.72C53.51,229.7,65.48,232,80,232c70.67,0,129.72-54.42,135.75-124.44l29.91-29.9A8,8,0,0,0,247.39,68.94Zm-45,29.41a8,8,0,0,0-2.32,5.14C196,166.58,143.28,216,80,216c-10.56,0-18-1.4-23.22-3.08,11.51-6.25,27.56-17,37.88-32.48A8,8,0,0,0,92,169.08c-.47-.27-43.91-26.34-44-96,16,13,45.25,33.17,78.67,38.79A8,8,0,0,0,136,104V88a32,32,0,0,1,9.6-22.92A30.94,30.94,0,0,1,167.9,56c12.66.16,24.49,7.88,29.44,19.21A8,8,0,0,0,204.67,80h16Z"></path>
                                    </svg>
                                </a>
                                <a className="social-link" href="#">
                                    <svg className="social-icon" fill="currentColor" viewBox="0 0 256 256" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M128,80a48,48,0,1,0,48,48A48.05,48.05,0,0,0,128,80Zm0,80a32,32,0,1,1,32-32A32,32,0,0,1,128,160ZM176,24H80A56.06,56.06,0,0,0,24,80v96a56.06,56.06,0,0,0,56,56h96a56.06,56.06,0,0,0,56-56V80A56.06,56.06,0,0,0,176,24Zm40,152a40,40,0,0,1-40,40H80a40,40,0,0,1-40-40V80A40,40,0,0,1,80,40h96a40,40,0,0,1,40,40ZM192,76a12,12,0,1,1-12-12A12,12,0,0,1,192,76Z"></path>
                                    </svg>
                                </a>
                                <a className="social-link" href="#">
                                    <svg className="social-icon" fill="currentColor" viewBox="0 0 256 256" xmlns="http://www.w3.org/2000/svg">
                                        <path d="M128,24A104,104,0,1,0,232,128,104.11,104.11,0,0,0,128,24Zm8,191.63V152h24a8,8,0,0,0,0-16H136V112a16,16,0,0,1,16-16h16a8,8,0,0,0,0-16H152a32,32,0,0,0-32,32v24H96a8,8,0,0,0,0,16h24v63.63a88,88,0,1,1,16,0Z"></path>
                                    </svg>
                                </a>
                            </div>
                        </div>
                        <div className="footer-bottom">
                            2025 Mindful
                        </div>
                    </div>
                </footer>
            </div>
        </div>
    );
};

export default WelcomePage;
