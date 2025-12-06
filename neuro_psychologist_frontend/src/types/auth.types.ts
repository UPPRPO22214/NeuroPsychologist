export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface AuthResponse {
  token: string;
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
}

export interface ErrorResponse {
  error: string;
}

export interface UserProfile {
  id: number;
  email: string;
  firstName: string;
}

export interface UpdateProfileRequest {
  firstName: string;
  newPassword?: string;
  currentPassword: string;
}