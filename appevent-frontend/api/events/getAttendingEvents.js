"use client";
import { useState, useEffect, useCallback, useRef } from "react";

export function attendingEventsFetch() {
  const [events, setEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const abortControllerRef = useRef(null);

  const handleEventUpdate = useCallback((updatedEvent) => {
    setEvents((prevEvents) =>
      prevEvents.map((event) =>
        event.id === updatedEvent.id ? updatedEvent : event
      )
    );
  }, []);

  useEffect(() => {
    const fetchEvents = async () => {
      abortControllerRef.current?.abort();
      abortControllerRef.current = new AbortController();
      setIsLoading(true);

      try {
        const token = localStorage.getItem("token");
        if (!token) throw new Error("No authentication token found");

        const response = await fetch(
          "http://localhost:8080/api/activity/myactivities",
          {
            signal: abortControllerRef.current.signal,
            headers: { Authorization: `${token}` },
          }
        );

        if (response.status === 401)
          throw new Error("Unauthorized - Please login again");
        if (!response.ok)
          throw new Error(`Failed to fetch events: ${response.statusText}`);

        const data = await response.json();
        setEvents(data);
      } catch (err) {
        if (err.name !== "AbortError") {
          setError(err.message);
          console.error("Fetch error:", err);
        }
      }
      setIsLoading(false);
    };

    fetchEvents();

    return () => abortControllerRef.current?.abort();
  }, []);

  return { events, isLoading, error, handleEventUpdate };
}
