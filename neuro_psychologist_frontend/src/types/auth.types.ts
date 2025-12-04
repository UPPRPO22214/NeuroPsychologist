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