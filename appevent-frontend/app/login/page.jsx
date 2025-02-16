"use client";

import React, { useState } from 'react';
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Lock, User } from "lucide-react";

const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [formData, setFormData] = useState({
    username: '',
    password: '',
    confirmPassword: ''
  });
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError('');

    if (!isLogin && formData.password !== formData.confirmPassword) {
      setError('Passordene matcher ikke');
      setIsLoading(false);
      return;
    }

    try {
      const endpoint = isLogin ? 'http://localhost:8080/api/auth/login' : 'http://localhost:8080/api/auth/register';
      const body = isLogin 
        ? { username: formData.username, password: formData.password }
        : { 
            username: formData.username, 
            password: formData.password,
            confirmPassword: formData.confirmPassword
          };

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      });

      const data = await response.text();
      
      if (!response.ok) {
        setError(data);
        return;
      }

      localStorage.setItem('token', data);
      window.location.href = '/upcoming-events';
    } catch (err) {
      setError(err.message || 'En feil oppstod');
    } finally {
      setIsLoading(false);
    }
  };

  const handleInputChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const toggleMode = () => {
    setIsLogin(!isLogin);
    setError('');
    setFormData({
      username: '',
      password: '',
      confirmPassword: ''
    });
  };

  return (
    <div className="min-h-screen flex items-center justify-center p-4">
      <div className="w-full max-w-6xl">
        <header className="text-center space-y-4 mb-8">
          <h1 className="text-4xl font-bold header-gradient">
            {isLogin ? 'Logg inn på AppEvent' : 'Registrer deg på AppEvent'}
          </h1>
          <p className="text-muted-foreground">
            {isLogin 
              ? 'Logg inn for å delta eller opprette arrangementer' 
              : 'Opprett en konto for å delta på arrangementer'}
          </p>
        </header>

        <div className="flex justify-center">
          <Card className="w-full max-w-md">
            <CardHeader>
              <CardTitle className="text-2xl text-center">
                {isLogin ? 'Login' : 'Registrer deg'}
              </CardTitle>
            </CardHeader>
            <form onSubmit={handleSubmit}>
              <CardContent className="space-y-4">
                {error && (
                  <div className="bg-red-50 text-red-600 p-4 rounded-lg">
                    {error}
                  </div>
                )}
                
                <div className="space-y-4">
                  <div className="flex items-center space-x-2">
                    <User className="w-4 h-4 text-muted-foreground" />
                    <Input
                      type="text"
                      name="username"
                      placeholder="Brukernavn"
                      value={formData.username}
                      onChange={handleInputChange}
                      className="flex-1"
                      required
                    />
                  </div>

                  <div className="flex items-center space-x-2">
                    <Lock className="w-4 h-4 text-muted-foreground" />
                    <Input
                      type="password"
                      name="password"
                      placeholder="Passord"
                      value={formData.password}
                      onChange={handleInputChange}
                      className="flex-1"
                      required
                    />
                  </div>

                  {!isLogin && (
                    <div className="flex items-center space-x-2">
                      <Lock className="w-4 h-4 text-muted-foreground" />
                      <Input
                        type="password"
                        name="confirmPassword"
                        placeholder="Bekreft passord"
                        value={formData.confirmPassword}
                        onChange={handleInputChange}
                        className="flex-1"
                        required
                      />
                    </div>
                  )}
                </div>
              </CardContent>

              <CardFooter className="flex flex-col space-y-4">
                <Button 
                  type="submit" 
                  className="w-full"
                  disabled={isLoading}
                >
                  {isLoading 
                    ? (isLogin ? 'Logger inn...' : 'Registrerer...') 
                    : (isLogin ? 'Logg inn' : 'Registrer deg')}
                </Button>
                
                <div className="text-sm text-center text-muted-foreground">
                  {isLogin ? (
                    <>
                      Har du ikke en konto?{' '}
                      <button 
                        type="button"
                        onClick={toggleMode}
                        className="text-primary hover:underline"
                      >
                        Registrer deg her
                      </button>
                    </>
                  ) : (
                    <>
                      Har du allerede en konto?{' '}
                      <button
                        type="button"
                        onClick={toggleMode}
                        className="text-primary hover:underline"
                      >
                        Logg inn her
                      </button>
                    </>
                  )}
                </div>
              </CardFooter>
            </form>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default AuthPage;