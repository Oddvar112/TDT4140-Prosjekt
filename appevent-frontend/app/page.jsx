"use client";
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';

export default function Home() {
  const router = useRouter();

  useEffect(() => {
    const token = localStorage.getItem('jwt_token');
    if (token) {
      router.push('/events');
    } else {
      router.push('/login');
    }
  }, [router]); 

  return (
    <div className="min-h-screen flex items-center justify-center">
      <div className="text-muted-foreground">Laster...</div>
    </div>
  );
}