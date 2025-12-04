import React, { useState, useRef, useEffect } from 'react';
import '../styles/Chat.css';
import UserMessage from '../components/UserMessage';
import SystemMessage from '../components/SystemMessage';
import LoadingAnimation from '../components/LoadingAnimation';
import Header from '../components/Header';
import { chatService, type Message } from '../services/chat.service';

const ChatPage = () => {
  const [messages, setMessages] = useState<Message[]>([
    {
      id: '1',
      text: 'Привет! Я здесь, чтобы поддержать вас. Расскажите, как прошёл ваш день? Что вас порадовало или, может быть, что-то вас беспокоит?',
      isUser: false,
      timestamp: new Date()
    }
  ]);
  const [inputValue, setInputValue] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages, isLoading]);

  const handleSendMessage = async () => {
    if (!inputValue.trim() || isLoading) return;

    const userMessage: Message = {
      id: Date.now().toString(),
      text: inputValue,
      isUser: true,
      timestamp: new Date()
    };

    setMessages(prev => [...prev, userMessage]);
    setInputValue('');
    setIsLoading(true);

    try {
      // Simulate longer wait time for LLM response
      await new Promise(resolve => setTimeout(resolve, 2000));
      
      const response = await chatService.sendMessage(userMessage.text);

      if (response.success && response.analysisText) {
        const systemMessage: Message = {
          id: (Date.now() + 1).toString(),
          text: formatResponse(response),
          isUser: false,
          timestamp: new Date()
        };
        setMessages(prev => [...prev, systemMessage]);
      } else {
        const errorMessage: Message = {
          id: (Date.now() + 1).toString(),
          text: response.error || 'К сожалению, не удалось получить анализ. Возможно, проблема с подключением к AI сервису. Пожалуйста, попробуйте позже.',
          isUser: false,
          timestamp: new Date()
        };
        setMessages(prev => [...prev, errorMessage]);
      }
    } catch (error) {
      const errorMessage: Message = {
        id: (Date.now() + 1).toString(),
        text: 'Не удалось отправить сообщение. Проверьте подключение к интернету и попробуйте снова.',
        isUser: false,
        timestamp: new Date()
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const formatResponse = (response: any): string => {
    let formattedText = '';
    
    if (response.dayRating !== null && response.dayRating !== undefined) {
      formattedText += `Оценка вашего дня: ${response.dayRating}/10\n\n`;
    }
    
    if (response.analysisText) {
      formattedText += response.analysisText;
    }
    
    return formattedText.trim();
  };

  const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSendMessage();
    }
  };

  return (
    <div className="relative flex h-auto min-h-screen w-full flex-col bg-background-primary group/design-root overflow-x-hidden" style={{ fontFamily: 'Inter, "Noto Sans", sans-serif' }}>
      <div className="flex h-full grow flex-col">
        <Header isAuthenticated={true} />
        
        <main className="flex-1 flex flex-col justify-center items-center p-4 sm:p-6 lg:p-8">
          <div className="w-full max-w-6xl flex flex-col h-[95vh] bg-background-secondary rounded-2xl shadow-sm">
            <div className="p-6 border-b border-surface-primary">
              <h1 className="text-text-primary text-xl font-bold leading-tight">Dr. Anya Petrova</h1>
              <p className="text-text-secondary text-sm font-normal leading-normal mt-1">
                Добро пожаловать! Я здесь, чтобы поддержать вас. Поделитесь тем, что у вас на душе, и мы вместе разберёмся.
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
              
              {isLoading && <LoadingAnimation />}
              
              <div ref={messagesEndRef} />
            </div>
            
            {/* Input area */}
            <div className="px-6 py-4 border-t border-surface-primary bg-background-secondary rounded-b-2xl">
              <div className="flex items-center gap-3">
                <input 
                  className="form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-text-primary focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-transparent border-surface-primary bg-surface-secondary h-12 placeholder:text-text-secondary px-4 text-base font-normal leading-normal" 
                  placeholder="Напишите ваше сообщение..." 
                  value={inputValue}
                  onChange={(e) => setInputValue(e.target.value)}
                  onKeyPress={handleKeyPress}
                  disabled={isLoading}
                />
                <button 
                  onClick={handleSendMessage}
                  disabled={isLoading || !inputValue.trim()}
                  className="flex items-center justify-center shrink-0 cursor-pointer rounded-lg h-12 w-12 bg-brand-primary text-white hover:bg-brand-primary-darker transition-colors disabled:bg-brand-primary-lighter disabled:cursor-not-allowed"
                >
                  <span className="material-symbols-outlined text-2xl">
                    send
                  </span>
                </button>
              </div>
            </div>
          </div>
        </main>
      </div>
    </div>
  );
};

export default ChatPage;