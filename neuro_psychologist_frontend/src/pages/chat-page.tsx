import React from 'react';
import '../styles/Chat.css';
import UserMessage from '../components/UserMessage';
import SystemMessage from '../components/SystemMessage';
import Header from '../components/Header';

const ChatPage = () => {
  return (
    <div className="relative flex h-auto min-h-screen w-full flex-col bg-background-primary group/design-root overflow-x-hidden" style={{ fontFamily: 'Inter, "Noto Sans", sans-serif' }}>
      <div className="flex h-full grow flex-col">
        <Header isAuthenticated={true} />
        
        <main className="flex-1 flex flex-col justify-center items-center p-4 sm:p-6 lg:p-8">
          <div className="w-full max-w-6xl flex flex-col h-[95vh] bg-background-secondary rounded-2xl shadow-sm">
            <div className="p-6 border-b border-surface-primary">
              <h1 className="text-text-primary text-xl font-bold leading-tight">Dr. Anya Petrova</h1>
              <p className="text-text-secondary text-sm font-normal leading-normal mt-1">
                Welcome! I'm here to support you. Feel free to share what's on your mind, and we'll work through it together.
              </p>
            </div>
            
            <div className="flex-1 p-6 space-y-6 overflow-y-auto">
              <SystemMessage message="Hi there! How can I assist you today?" />
              
              <UserMessage message="I've been feeling overwhelmed lately with work and personal life. It's hard to balance everything." />
              
              <SystemMessage message="I understand. It's common to feel overwhelmed when juggling multiple responsibilities. Let's explore some strategies to help you manage your stress and find a better balance." />
            </div>
            
            {/* Input area */}
            <div className="px-6 py-4 border-t border-surface-primary bg-background-secondary rounded-b-2xl">
              <div className="flex items-center gap-3">
                <input 
                  className="form-input flex w-full min-w-0 flex-1 resize-none overflow-hidden rounded-lg text-text-primary focus:outline-none focus:ring-2 focus:ring-brand-primary focus:border-transparent border-surface-primary bg-surface-secondary h-12 placeholder:text-text-secondary px-4 text-base font-normal leading-normal" 
                  placeholder="Type your message..." 
                  value=""
                  onChange={() => {}} 
                />
                <button className="flex items-center justify-center shrink-0 cursor-pointer rounded-lg h-12 w-12 bg-brand-primary text-white hover:bg-brand-primary-darker transition-colors disabled:bg-brand-primary-lighter">
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