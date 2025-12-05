import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import { chatService } from '../services/chat.service';
import type { ChatHistoryItem } from '../types/chat-history.types';
import '../styles/ChatHistory.css';

const ChatHistoryPage: React.FC = () => {
  const navigate = useNavigate();
  const [history, setHistory] = useState<ChatHistoryItem[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [expandedId, setExpandedId] = useState<number | null>(null);

  useEffect(() => {
    loadHistory();
  }, [currentPage]);

  const loadHistory = async () => {
    try {
      setLoading(true);
      setError(null);
      const response = await chatService.getChatHistory({ page: currentPage, size: 10 });
      setHistory(response.content);
      setTotalPages(response.totalPages);
    } catch (err) {
      setError('Не удалось загрузить историю чатов');
      console.error('Error loading chat history:', err);
    } finally {
      setLoading(false);
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return new Intl.DateTimeFormat('ru-RU', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    }).format(date);
  };

  const getRatingColor = (rating: number) => {
    if (rating >= 4) return '#4ade80';
    if (rating >= 3) return '#fbbf24';
    return '#f87171';
  };

  const toggleExpand = (id: number) => {
    setExpandedId(expandedId === id ? null : id);
  };

  return (
    <div className="relative flex h-auto min-h-screen w-full flex-col bg-background-primary" style={{ fontFamily: 'Inter, "Noto Sans", sans-serif' }}>
      <div className="flex h-full grow flex-col">
        <Header />
        
        <main className="flex-1 flex flex-col items-center p-4 sm:p-6 lg:p-8" style={{ paddingTop: 'calc(3rem + 60px)' }}>
          <div className="w-full max-w-6xl">
            <div className="mb-6 flex items-center justify-between">
              <div>
                <h1 className="text-text-primary text-3xl font-bold leading-tight">История чатов</h1>
              </div>
            </div>

            {loading ? (
              <div className="flex justify-center items-center py-20">
                <div className="loading-spinner"></div>
              </div>
            ) : error ? (
              <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center">
                <p className="text-red-600 font-medium">{error}</p>
                <button
                  onClick={loadHistory}
                  className="mt-4 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition-colors"
                >
                  Попробовать снова
                </button>
              </div>
            ) : history.length === 0 ? (
              <div className="bg-background-secondary rounded-2xl shadow-sm p-12 text-center">
                <div className="text-6xl mb-4">📝</div>
                <h2 className="text-text-primary text-xl font-bold mb-2">История пуста</h2>
                <p className="text-text-secondary mb-6">Вы ещё не проходили чекап дня</p>
                <button
                  onClick={() => navigate('/chat')}
                  className="px-6 py-3 bg-brand-primary text-white rounded-lg hover:bg-brand-primary-darker transition-colors font-medium"
                >
                  Пройти первый чекап
                </button>
              </div>
            ) : (
              <>
                <div className="space-y-4">
                  {history.map((item) => (
                    <div
                      key={item.id}
                      className="bg-background-secondary rounded-2xl shadow-sm overflow-hidden transition-all hover:shadow-md"
                    >
                      <div
                        className="p-6 cursor-pointer"
                        onClick={() => toggleExpand(item.id)}
                      >
                        <div className="flex items-start justify-between mb-4">
                          <div className="flex-1">
                            <div className="flex items-center gap-3 mb-2">
                              <h3 className="text-text-primary text-lg font-bold">
                                Чекап от {formatDate(item.createdAt)}
                              </h3>
                              {item.dayRating && (
                                <div
                                  className="px-3 py-1 rounded-full text-sm font-medium text-white"
                                  style={{ backgroundColor: getRatingColor(item.dayRating) }}
                                >
                                  {item.dayRating}/5
                                </div>
                              )}
                            </div>
                          </div>
                          <button
                            className="text-text-secondary hover:text-brand-primary transition-all"
                            style={{
                              transform: expandedId === item.id ? 'rotate(180deg)' : 'rotate(0deg)',
                              transition: 'transform 0.3s ease'
                            }}
                          >
                            <svg
                              width="24"
                              height="24"
                              viewBox="0 0 24 24"
                              fill="none"
                              stroke="currentColor"
                              strokeWidth="2"
                              strokeLinecap="round"
                              strokeLinejoin="round"
                            >
                              <polyline points="6 9 12 15 18 9"></polyline>
                            </svg>
                          </button>
                        </div>

                        <div className="grid grid-cols-5 gap-3">
                          <div className="text-center">
                            <div className="text-text-secondary text-xs mb-1">Спокойствие</div>
                            <div className="flex justify-center">
                              {'⭐'.repeat(item.calmnessRating)}
                            </div>
                          </div>
                          <div className="text-center">
                            <div className="text-text-secondary text-xs mb-1">Энергия</div>
                            <div className="flex justify-center">
                              {'⭐'.repeat(item.energyRating)}
                            </div>
                          </div>
                          <div className="text-center">
                            <div className="text-text-secondary text-xs mb-1">Удовлетворение</div>
                            <div className="flex justify-center">
                              {'⭐'.repeat(item.satisfactionRating)}
                            </div>
                          </div>
                          <div className="text-center">
                            <div className="text-text-secondary text-xs mb-1">Связь</div>
                            <div className="flex justify-center">
                              {'⭐'.repeat(item.connectionRating)}
                            </div>
                          </div>
                          <div className="text-center">
                            <div className="text-text-secondary text-xs mb-1">Вовлечённость</div>
                            <div className="flex justify-center">
                              {'⭐'.repeat(item.engagementRating)}
                            </div>
                          </div>
                        </div>
                      </div>

                      {expandedId === item.id && (
                        <div className="border-t border-surface-primary p-6 bg-surface-secondary">
                          <div className="space-y-4">
                            <div>
                              <h4 className="text-text-primary font-semibold mb-2">Текущее состояние:</h4>
                              <p className="text-text-secondary">{item.currentStateText}</p>
                            </div>
                            <div>
                              <h4 className="text-text-primary font-semibold mb-2">Моменты энергии:</h4>
                              <p className="text-text-secondary">{item.energyMomentsText}</p>
                            </div>
                            <div>
                              <h4 className="text-text-primary font-semibold mb-2">Чего не хватает:</h4>
                              <p className="text-text-secondary">{item.missingElementText}</p>
                            </div>
                            
                            {item.analysisText && (
                              <div className="mt-6 pt-6 border-t border-surface-primary">
                                <h4 className="text-text-primary font-semibold mb-3 flex items-center gap-2">
                                  <span>✨</span>
                                  <span>Анализ:</span>
                                </h4>
                                <p className="text-text-secondary whitespace-pre-line">{item.analysisText}</p>
                              </div>
                            )}
                            
                            {item.recommendations && item.recommendations.length > 0 && (
                              <div className="mt-4">
                                <h4 className="text-text-primary font-semibold mb-3">Рекомендации:</h4>
                                <ul className="space-y-2">
                                  {item.recommendations.map((rec, index) => (
                                    <li key={index} className="flex items-start gap-2 text-text-secondary">
                                      <span className="text-brand-primary font-bold">{index + 1}.</span>
                                      <span>{rec}</span>
                                    </li>
                                  ))}
                                </ul>
                              </div>
                            )}
                          </div>
                        </div>
                      )}
                    </div>
                  ))}
                </div>

                {totalPages > 1 && (
                  <div className="mt-8 flex justify-center items-center gap-2">
                    <button
                      onClick={() => setCurrentPage(prev => Math.max(0, prev - 1))}
                      disabled={currentPage === 0}
                      className="px-4 py-2 rounded-lg bg-background-secondary text-text-primary font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-surface-secondary transition-colors"
                    >
                      ← Назад
                    </button>
                    <span className="px-4 py-2 text-text-secondary">
                      Страница {currentPage + 1} из {totalPages}
                    </span>
                    <button
                      onClick={() => setCurrentPage(prev => Math.min(totalPages - 1, prev + 1))}
                      disabled={currentPage >= totalPages - 1}
                      className="px-4 py-2 rounded-lg bg-background-secondary text-text-primary font-medium disabled:opacity-50 disabled:cursor-not-allowed hover:bg-surface-secondary transition-colors"
                    >
                      Вперёд →
                    </button>
                  </div>
                )}
              </>
            )}
          </div>
        </main>
      </div>
    </div>
  );
};

export default ChatHistoryPage;