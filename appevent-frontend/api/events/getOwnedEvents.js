"use client";

import { useState, useEffect } from 'react';

export const ownedEventsFetch = () => {
  const [events, setEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/activity/owned", {
          headers: {
            Authorization: localStorage.getItem("token"),
          },
        });
        if (response.ok) {
          const data = await response.json();
          setEvents(data);
        } else {
          throw new Error("Failed to fetch events");
        }
      } catch (err) {
        setError(err.message);
      } finally {
        setIsLoading(false);
      }
    };

    fetchEvents();
  }, []);

  return { events, isLoading, error };
};