import React from 'react';

const LoadingAnimation = () => {
  return (
    <div className="flex items-start gap-3">
      <div className="flex size-10 shrink-0 items-center justify-center rounded-full bg-brand-secondary">
        <span className="material-symbols-outlined text-brand-primary text-2xl">
          psychology
        </span>
      </div>
      <div className="flex flex-col gap-1.5 rounded-xl rounded-tl-none bg-surface-secondary px-4 py-3 max-w-md">
        <div className="flex gap-1">
          <div className="w-2 h-2 bg-brand-primary rounded-full animate-bounce" style={{ animationDelay: '0ms' }}></div>
          <div className="w-2 h-2 bg-brand-primary rounded-full animate-bounce" style={{ animationDelay: '150ms' }}></div>
          <div className="w-2 h-2 bg-brand-primary rounded-full animate-bounce" style={{ animationDelay: '300ms' }}></div>
        </div>
      </div>
    </div>
  );
};

export default LoadingAnimation;