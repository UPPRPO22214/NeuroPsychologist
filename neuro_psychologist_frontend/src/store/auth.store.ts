import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface User {
  id: number;
  username: string;
  email: string;
  firstName?: string;
}

interface AuthState {
  isAuthenticated: boolean;
  user: User | null;
  token: string | null;
  
  // Actions
  login: (token: string, user: User) => void;
  logout: () => void;
  setUser: (user: User) => void;
  checkAuth: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      isAuthenticated: false,
      user: null,
      token: null,

      login: (token: string, user: User) => {
        localStorage.setItem('authToken', token);
        set({ 
          isAuthenticated: true, 
          user, 
          token 
        });
      },

      logout: () => {
        localStorage.removeItem('authToken');
        set({ 
          isAuthenticated: false, 
          user: null, 
          token: null 
        });
      },

      setUser: (user: User) => {
        set({ user });
      },

      checkAuth: () => {
        const token = localStorage.getItem('authToken');
        if (token) {
          set({ 
            isAuthenticated: true, 
            token 
          });
        } else {
          set({ 
            isAuthenticated: false, 
            user: null, 
            token: null 
          });
        }
      },
    }),
    {
      name: 'auth-storage',
      partialize: (state) => ({
        isAuthenticated: state.isAuthenticated,
        user: state.user,
        token: state.token,
      }),
    }
  )
);