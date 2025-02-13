"use client";
import { useState, useEffect, useRef } from "react";

export function SingleEventFetch(activityId) {
  const [event, setEvent] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [refreshTrigger, setRefreshTrigger] = useState(0);
  const abortControllerRef = useRef(null);

  const refresh = () => {
    setRefreshTrigger((prev) => prev + 1);
  };

  useEffect(() => {
    const fetchEvent = async () => {
      abortControllerRef.current?.abort();
      abortControllerRef.current = new AbortController();
      setIsLoading(true);
      setError(null);

      if (!activityId) {
        setError("Activity ID is required");
        setIsLoading(false);
        return;
      }

      try {
        const token = localStorage.getItem("token");
        const response = await fetch(
          `http://localhost:8080/api/activity/activityinfo/${activityId}`,
          {
            signal: abortControllerRef.current.signal,
            headers: { Authorization: `${token}` },
          }
        );

        if (response.status === 401) {
          throw new Error("Unauthorized - Please login again");
        } else if (response.status === 404) {
          throw new Error("Event not found");
        } else if (!response.ok) {
          throw new Error(`Failed to fetch event: ${response.statusText}`);
        }

        const data = await response.json();
        setEvent({
          ...data,
          participants: data.participants || [],
        });
      } catch (err) {
        if (err.name !== "AbortError") {
          setError(err.message);
          console.error("Fetch error:", err);
        }
      } finally {
        setIsLoading(false);
      }
    };

    fetchEvent();

    return () => abortControllerRef.current?.abort();
  }, [activityId, refreshTrigger]);

  return { event, isLoading, error, refresh };
}
