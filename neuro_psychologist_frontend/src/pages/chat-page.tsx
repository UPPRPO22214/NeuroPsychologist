import React, { useState, useRef, useEffect } from 'react';
import '../styles/Chat.css';
import UserMessage from '../components/UserMessage';
import SystemMessage from '../components/SystemMessage';
import Header from '../components/Header';
import { chatService } from '../services/chat.service';

interface Message {
  id: string;
  text: string;
  isUser: boolean;
  timestamp: Date;
}

type CheckInStep = 'welcome' | 'rating' | 'open' | 'submitting' | 'complete';

interface RatingAnswers {
  calmness: number | null;
  energy: number | null;
  satisfaction: number | null;
  connection: number | null;
  engagement: number | null;
}

interface OpenAnswers {
  currentState: string;
  energyMoments: string;
  missingElement: string;
}

const RATING_QUESTIONS = [
  { key: 'calmness' as keyof RatingAnswers, text: 'Насколько я чувствую себя спокойно и в гармонии с собой?' },
  { key: 'energy' as keyof RatingAnswers, text: 'Насколько сегодняшний день был для меня наполнен энергией, а не истощением?' },
  { key: 'satisfaction' as keyof RatingAnswers, text: 'Насколько я удовлетворён(а) тем, как прошёл мой день?' },
  { key: 'connection' as keyof RatingAnswers, text: 'Насколько близкими и тёплыми были мои контакты с людьми сегодня?' },
  { key: 'engagement' as keyof RatingAnswers, text: 'Насколько я испытывал(а) сегодня интерес, радость или чувство вовлечённости?' }
];

const OPEN_QUESTIONS = [
  { key: 'currentState' as keyof OpenAnswers, text: 'Какое слово, образ или метафора лучше всего описывает моё состояние прямо сейчас?' },
  { key: 'energyMoments' as keyof OpenAnswers, text: 'Какой момент сегодня отнял у меня больше всего энергии, а какой — добавил?' },
  { key: 'missingElement' as keyof OpenAnswers, text: 'Чего мне прямо сейчас не хватает для полного покоя или удовлетворения?' }
];

const ChatPage = () => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      text: 'Добро пожаловать! 👋\n\nЯ рада видеть вас здесь. Давайте проведём небольшой чекап дня — это поможет вам лучше понять своё состояние и то, что происходит внутри.\n\nЯ задам вам несколько вопросов о вашем дне и самочувствии. Отвечайте честно и спокойно — здесь нет правильных или неправильных ответов.\n\nГотовы начать?',
      isUser: false,
      timestamp: new Date()
    }
  ]);
  const [step, setStep] = useState<CheckInStep>('welcome');
  const [currentRatingIndex, setCurrentRatingIndex] = useState(0);
  const [currentOpenIndex, setCurrentOpenIndex] = useState(0);
  const [ratingAnswers, setRatingAnswers] = useState<RatingAnswers>({
    calmness: null,
    energy: null,
    satisfaction: null,
    connection: null,
    engagement: null
  });
  const [openAnswers, setOpenAnswers] = useState<OpenAnswers>({
    currentState: '',
    energyMoments: '',
    missingElement: ''
  });
  const [inputValue, setInputValue] = useState('');
  const [showRatingButtons, setShowRatingButtons] = useState(false);
  const [hoveredRating, setHoveredRating] = useState<number | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, showRatingButtons]);

  const addMessage = (text: string, isUser: boolean) => {
    const newMessage: Message = {
      id: Date.now().toString(),
      text,
      isUser,
      timestamp: new Date()
    };
    setMessages(prev => [...prev, newMessage]);
  };

  const handleStartCheckIn = () => {
    addMessage('Да, готов(а) начать!', true);
    setTimeout(() => {
      addMessage(RATING_QUESTIONS[0].text, false);
      setStep('rating');
      setShowRatingButtons(true);
    }, 500);
  };

  const handleRatingSelect = (rating: number) => {
    const currentQuestion = RATING_QUESTIONS[currentRatingIndex];
    setRatingAnswers(prev => ({
      ...prev,
      [currentQuestion.key]: rating
    }));

    // Отображаем выбранный рейтинг звёздочками
    const stars = '⭐'.repeat(rating);
    addMessage(stars, true);
    setShowRatingButtons(false);

    setTimeout(() => {
      if (currentRatingIndex < RATING_QUESTIONS.length - 1) {
        const nextIndex = currentRatingIndex + 1;
        addMessage(RATING_QUESTIONS[nextIndex].text, false);
        setCurrentRatingIndex(nextIndex);
        setShowRatingButtons(true);
      } else {
        addMessage('Спасибо за ваши оценки! Теперь давайте перейдём к более открытым вопросам.\n\n' + OPEN_QUESTIONS[0].text, false);
        setStep('open');
      }
    }, 500);
  };

  const handleOpenAnswerSubmit = () => {
    if (!inputValue.trim()) return;

    const currentQuestion = OPEN_QUESTIONS[currentOpenIndex];
    setOpenAnswers(prev => ({
      ...prev,
      [currentQuestion.key]: inputValue
    }));

    addMessage(inputValue, true);
    setInputValue('');

    setTimeout(() => {
      if (currentOpenIndex < OPEN_QUESTIONS.length - 1) {
        const nextIndex = currentOpenIndex + 1;
        addMessage(OPEN_QUESTIONS[nextIndex].text, false);
        setCurrentOpenIndex(nextIndex);
      } else {
        submitCheckIn();
      }
    }, 500);
  };

  const submitCheckIn = async () => {
    setStep('submitting');
    addMessage('Обрабатываю ваши ответы...', false);

    try {
      // Формируем структурированные данные для отправки
      const checkInData = {
        calmnessRating: ratingAnswers.calmness!,
        energyRating: ratingAnswers.energy!,
        satisfactionRating: ratingAnswers.satisfaction!,
        connectionRating: ratingAnswers.connection!,
        engagementRating: ratingAnswers.engagement!,
        currentStateText: openAnswers.currentState,
        energyMomentsText: openAnswers.energyMoments,
        missingElementText: inputValue
      };

      const response = await chatService.submitCheckIn(checkInData);
      
      setTimeout(() => {
        if (response.success) {
          // Формируем итоговое сообщение с обзором и рекомендациями
          let finalMessage = '✨ **Обзор вашего дня**\n\n';
          
          if (response.analysisText) {
            finalMessage += response.analysisText + '\n\n';
          }
          
          if (response.recommendations && response.recommendations.length > 0) {
            finalMessage += '**Рекомендации:**\n';
            response.recommendations.forEach((rec, index) => {
              finalMessage += `${index + 1}. ${rec}\n`;
            });
          }
          
          addMessage(finalMessage.trim(), false);
        } else {
          addMessage('✨ Спасибо за ваши ответы!\n\nВаш чекап дня успешно сохранён. Эта информация поможет лучше понять ваше состояние и отследить динамику вашего самочувствия.', false);
        }
        setStep('complete');
      }, 1000);
    } catch (error) {
      addMessage('Не удалось отправить данные. Попробуйте снова.', false);
      setStep('open');
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      if (step === 'welcome') {
        handleStartCheckIn();
      } else if (step === 'open') {
        handleOpenAnswerSubmit();
      }
    }
  };

  const handleSendClick = () => {
    if (step === 'welcome') {
      handleStartCheckIn();
    } else if (step === 'open') {
      handleOpenAnswerSubmit();
    }
  };

  return (
    <div className="relative flex h-auto min-h-screen w-full flex-col bg-background-primary group/design-root overflow-x-hidden" style={{ fontFamily: 'Inter, "Noto Sans", sans-serif' }}>
      <div className="flex h-full grow flex-col">
        <Header isAuthenticated={true} />
        
        <main className="flex-1 flex flex-col justify-center items-center p-4 sm:p-6 lg:p-8">
          <div className="w-full max-w-6xl flex flex-col h-[95vh] bg-background-secondary rounded-2xl shadow-sm">
            <div className="p-6 border-b border-surface-primary">
              <h1 className="text-text-primary text-xl font-bold leading-tight">Чекап дня</h1>
              <p className="text-text-secondary text-sm font-normal leading-normal mt-1">
                Давайте вместе разберёмся, как прошёл ваш день
              </p>
            </div>
            
            <div className="flex-1 p-6 space-y-6 overflow-y-auto">
              {messages.map((message) => (
                message.isUser ? (
                  <UserMessage key={message.id} message={message.text} />
                ) : (
                  <SystemMessage key={message.id} message={message.text} />
                )
              ))}

              {showRatingButtons && step === 'rating' && (
                <div className="flex justify-end">
                  <div className="flex flex-col items-end space-y-3 max-w-md">
                    <div className="flex gap-1">
                      {[1, 2, 3, 4, 5].map((rating) => (
                        <button
                          key={rating}
                          onClick={() => handleRatingSelect(rating)}
                          onMouseEnter={() => setHoveredRating(rating)}
                          onMouseLeave={() => setHoveredRating(null)}
                          className="w-10 h-10 flex items-center justify-center transition-all cursor-pointer"
                          title={`${rating} ${rating === 1 ? 'звезда' : rating < 5 ? 'звезды' : 'звёзд'}`}
                          style={{ background: 'none', border: 'none', padding: 0 }}
                        >
                          <svg
                            width="32"
                            height="32"
                            viewBox="0 0 24 24"
                            fill={hoveredRating && rating <= hoveredRating ? '#C9A989' : 'none'}
                            stroke="#C9A989"
                            strokeWidth="2"
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            style={{ transition: 'fill 0.2s' }}
                          >
                            <polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2" />
                          </svg>
                        </button>
                      ))}
                    </div>
                    <span className="text-sm text-text-secondary">Выберите от 1 до 5 звёзд</span>
                  </div>
                </div>
              )}
              
              <div ref={messagesEndRef} />
            </div>
            
            {/* Input area */}
            {(step === 'welcome' || step === 'open') && (
              <div className="px-6 py-4 border-t border-surface-primary bg-background-secondary rounded-b-2xl">
                <div className="flex items-center gap-3">
                  <input
                    className="form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-text-primary focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-transparent border-surface-primary bg-surface-secondary h-12 placeholder:text-text-secondary px-4 text-base font-normal leading-normal"
                    placeholder={step === 'welcome' ? 'Напишите "Да" чтобы начать...' : 'Напишите ваш ответ...'}
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    onKeyPress={handleKeyPress}
                  />
                  <button
                    onClick={handleSendClick}
                    disabled={step === 'open' && !inputValue.trim()}
                    className="flex items-center justify-center shrink-0 cursor-pointer rounded-lg h-12 w-12 bg-brand-primary text-white hover:bg-brand-primary-darker transition-colors disabled:bg-brand-primary-lighter disabled:cursor-not-allowed"
                  >
                    <span className="material-symbols-outlined text-2xl">
                      send
                    </span>
                  </button>
                </div>
              </div>
            )}
          </div>
        </main>
      </div>
    </div>
  );
};

export default ChatPage;