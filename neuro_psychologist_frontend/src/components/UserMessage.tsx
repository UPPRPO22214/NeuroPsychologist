import React from 'react';

interface UserMessageProps {
  message: string;
  userName?: string;
  userAvatar?: string;
}

const UserMessage: React.FC<UserMessageProps> = ({ 
  message, 
  userName = "You", 
  userAvatar = "https://lh3.googleusercontent.com/aida-public/AB6AXuBqrfwi9q6Iayjjsvn5PWAwCtZYsGM89VaK122_iqTEJt1AoPsLnDA3mG5_uWzTPihknzLp0SimuIj_oKJdS3FzfAIHLpHASBOKt5vjw8UryX3x66KK2sZg0sMvU5wW3YNuLCiqXY5Rs5KKVI2GwAKV8tyt8Tz05oIqZatnVDRZCy8Sy071oT-ayMB20F-a3IEwMXYGDjjDb4tOViN_MN8uk5_1vs6w0CoxSwSg7NhBh167x8wP0_c1am8gK46Hsem7gxZD7sgSaq9-" 
}) => {
  return (
    <div className="flex items-start gap-4 justify-end">
      <div className="flex flex-col gap-1.5 items-end">
        <p className="text-text-secondary text-sm font-medium">{userName}</p>
        <div className="bg-brand-primary text-white rounded-xl rounded-tr-none px-6 py-4 max-w-md">
          <p className="text-base font-normal leading-relaxed">{message}</p>
        </div>
      </div>
      <div 
        className="bg-center bg-no-repeat aspect-square bg-cover rounded-full w-10 shrink-0" 
        style={{ backgroundImage: `url("${userAvatar}")` }}
      ></div>
    </div>
  );
};

export default UserMessage;