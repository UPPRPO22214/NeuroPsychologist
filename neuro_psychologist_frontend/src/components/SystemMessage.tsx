import React from 'react';

interface SystemMessageProps {
  message: string;
  systemName?: string;
  systemAvatar?: string;
}

const SystemMessage: React.FC<SystemMessageProps> = ({ 
  message, 
  systemName = "Dr. Anya Petrova", 
  systemAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuDlwFmj5Y9CeoAMU2iwNUxfZiZgCoDHpaxGmP2VETEqxANz9xd6iClo9W4NKORi7DAlpg5Iam5wY8bVYi7ZplJDVUQinQApzOv-pr9Jkl-U-CC294Zbckdjall1GByrU82rASQbehjGk3sXk4uO-HtWK6uJqF0UfaxUTFZ9ku6n7VJY2ZeiQBm3zmthuwKqpTkBI4dJgItDSXZXUjzFnD73HhVzW8oSMyeXyXWPr0ntFxH6WMfY38McHompSAZ4oTToPpmral67Bwq8" 
}) => {
  return (
    <div className="flex items-start gap-4">
      <div 
        className="bg-center bg-no-repeat aspect-square bg-cover rounded-full size-10 shrink-0" 
        style={{ backgroundImage: `url("${systemAvatar}")` }}
      ></div>
      <div className="flex flex-col gap-1.5 items-start">
        <p className="text-text-secondary text-sm font-medium">{systemName}</p>
        <div className="bg-surface-secondary text-text-primary rounded-xl rounded-tl-none px-6 py-4 max-w-md">
          <p className="text-base font-normal leading-relaxed">{message}</p>
        </div>
      </div>
    </div>
  );
};

export default SystemMessage;