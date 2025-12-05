export interface ChatHistoryItem {
  id: number;
  userId: number;
  calmnessRating: number;
  energyRating: number;
  satisfactionRating: number;
  connectionRating: number;
  engagementRating: number;
  currentStateText: string;
  energyMomentsText: string;
  missingElementText: string;
  dayRating: number | null;
  recommendations: string[] | null;
  analysisText: string | null;
  analyzedAt: string;
  createdAt: string;
}

export interface ChatHistoryResponse {
  content: ChatHistoryItem[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}

export interface ChatHistoryParams {
  page?: number;
  size?: number;
}