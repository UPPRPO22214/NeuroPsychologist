import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthService } from '../services/auth.service';
import type { UserProfile, UpdateProfileRequest } from '../types/auth.types';
import Header from '../components/Header';
import '../styles/Profile.css';

const ProfilePage: React.FC = () => {
  const navigate = useNavigate();
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string>('');
  const [success, setSuccess] = useState<string>('');
  const [formData, setFormData] = useState({
    firstName: '',
    newPassword: '',
    confirmPassword: '',
    currentPassword: '',
  });

  useEffect(() => {
    loadProfile();
  }, []);

  const loadProfile = async () => {
    try {
      setLoading(true);
      const data = await AuthService.getProfile();
      setProfile(data);
      setFormData(prev => ({
        ...prev,
        firstName: data.firstName || '',
      }));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось загрузить профиль');
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value,
    }));
    setError('');
    setSuccess('');
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccess('');

    // Validation
    if (!formData.firstName.trim()) {
      setError('Имя обязательно для заполнения');
      return;
    }

    if (!formData.currentPassword) {
      setError('Текущий пароль обязателен для сохранения изменений');
      return;
    }

    if (formData.newPassword && formData.newPassword !== formData.confirmPassword) {
      setError('Новые пароли не совпадают');
      return;
    }

    if (formData.newPassword && formData.newPassword.length < 6) {
      setError('Новый пароль должен содержать минимум 6 символов');
      return;
    }

    try {
      const updateData: UpdateProfileRequest = {
        firstName: formData.firstName.trim(),
        currentPassword: formData.currentPassword,
      };

      if (formData.newPassword) {
        updateData.newPassword = formData.newPassword;
      }

      const updatedProfile = await AuthService.updateProfile(updateData);
      setProfile(updatedProfile);
      setSuccess('Профиль успешно обновлен!');
      
      // Clear password fields
      setFormData(prev => ({
        ...prev,
        newPassword: '',
        confirmPassword: '',
        currentPassword: '',
      }));
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Не удалось обновить профиль');
    }
  };

  if (loading) {
    return (
      <div className="profile-page">
        <Header />
        <div className="profile-container">
          <div className="loading">Загрузка профиля...</div>
        </div>
      </div>
    );
  }

  return (
    <div className="profile-page">
      <Header />
      <div className="profile-container">
        <div className="profile-card">
          <h1>Редактирование профиля</h1>
          
          {error && <div className="error-message">{error}</div>}
          {success && <div className="success-message">{success}</div>}

          <form onSubmit={handleSubmit} className="profile-form">
            <div className="form-group">
              <label htmlFor="email">Email</label>
              <input
                type="email"
                id="email"
                value={profile?.email || ''}
                disabled
                className="input-disabled"
              />
              <small className="form-hint">Email нельзя изменить</small>
            </div>

            <div className="form-group">
              <label htmlFor="firstName">Имя *</label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                value={formData.firstName}
                onChange={handleInputChange}
                placeholder="Введите ваше имя"
                required
              />
            </div>

            <div className="form-divider">
              <span>Изменить пароль </span>
            </div>

            <div className="form-group">
              <label htmlFor="newPassword">Новый пароль</label>
              <input
                type="password"
                id="newPassword"
                name="newPassword"
                value={formData.newPassword}
                onChange={handleInputChange}
                placeholder="Оставьте пустым, чтобы сохранить текущий пароль"
                minLength={6}
              />
              {formData.newPassword && (
                <small className="form-hint">Минимум 6 символов</small>
              )}
            </div>

            <div className="form-group">
              <label htmlFor="confirmPassword">Подтвердите новый пароль</label>
              <input
                type="password"
                id="confirmPassword"
                name="confirmPassword"
                value={formData.confirmPassword}
                onChange={handleInputChange}
                placeholder="Подтвердите новый пароль"
                disabled={!formData.newPassword}
              />
            </div>

            <div className="form-divider">
              <span>Подтверждение личности</span>
            </div>

            <div className="form-group">
              <label htmlFor="currentPassword">Текущий пароль *</label>
              <input
                type="password"
                id="currentPassword"
                name="currentPassword"
                value={formData.currentPassword}
                onChange={handleInputChange}
                placeholder="Введите текущий пароль"
                required
              />
              <small className="form-hint">Обязателен для сохранения любых изменений</small>
            </div>

            <div className="form-actions">
              <button type="button" onClick={() => navigate('/chat')} className="btn-secondary">
                Отмена
              </button>
              <button type="submit" className="btn-primary">
                Сохранить изменения
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;