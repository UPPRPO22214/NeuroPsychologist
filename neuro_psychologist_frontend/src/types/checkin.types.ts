export interface RatingQuestion {
  id: number;
  question: string;
  rating: number | null;
}

export interface OpenQuestion {
  id: number;
  question: string;
  answer: string;
}

export interface CheckInData {
  ratingQuestions: {
    calmness: number;
    energy: number;
    satisfaction: number;
    connection: number;
    engagement: number;
  };
  openQuestions: {
    currentState: string;
    energyMoments: string;
    missingElement: string;
  };
}

export interface CheckInResponse {
  success: boolean;
  message?: string;
  error?: string;
}