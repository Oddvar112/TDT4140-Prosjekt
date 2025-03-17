import { useState } from "react";

export const postCreateEvent = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const createEvent = async (eventData) => {
    setIsLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem("token");
      const response = await fetch(
        "http://localhost:8080/api/activity/add", 
        {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: token,
        },
        body: JSON.stringify({
          title: eventData.title,
          dateTime: eventData.dateTime,
          location: eventData.location,
          description: eventData.description,
          isPrivate: Boolean(eventData.isPrivate), 
          participants: [],
          type: eventData.type,
        }),
      });

      if (!response.ok) {
        console.error("API Error:", error);
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      setIsLoading(false);
      return true;
    } catch (err) {
      console.error("Request Error:", err);
      setError(err.message);
      setIsLoading(false);
      return false;
    }
  };

  return { createEvent, isLoading, error };
};