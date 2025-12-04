const API_URL = 'http://localhost:8080/api';

export interface Message {
  id: string;
  text: string;
  isUser: boolean;
  timestamp: Date;
}

export interface AnalysisResponse {
  id?: number;
  dayRating?: number;
  recommendations?: string[];
  analyzedAt?: string;
  success: boolean;
  error?: string;
}

export const chatService = {
  async sendMessage(message: string): Promise<AnalysisResponse> {
    const token = localStorage.getItem('authToken');
    
    if (!token) {
      throw new Error('No authentication token found');
    }

    const response = await fetch(`${API_URL}/analysis/analyze`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ userText: message })
    });

    if (!response.ok) {
      throw new Error('Failed to send message');
    }

    return response.json();
  }
};