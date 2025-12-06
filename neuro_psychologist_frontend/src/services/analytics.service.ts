const API_BASE_URL = 'http://localhost:8080/api/analysis';

export interface MetricsData {
  id: number;
  analyzedAt: string;
  isCheckin: boolean;
  calmnessRating?: number;
  energyRating?: number;
  satisfactionRating?: number;
  connectionRating?: number;
  engagementRating?: number;
  dayRating?: number;
}

export class AnalyticsService {
  static async getMetrics(startDate?: string, endDate?: string): Promise<MetricsData[]> {
    const token = localStorage.getItem('authToken');
    
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);
    
    const url = `${API_BASE_URL}/metrics${params.toString() ? `?${params.toString()}` : ''}`;
    
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    });
    
    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || 'Failed to fetch metrics');
    }
    
    return response.json();
  }
}