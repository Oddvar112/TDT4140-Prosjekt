import { useState, useEffect, useCallback, useRef, use } from "react";

export function SearchedEventsFetch() {
  const [events, setEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const abortControllerRef = useRef(null);

  const fetchMatchingEvents = useCallback(async (searchData) => {
    if (!searchData) return;

    abortControllerRef.current?.abort();
    abortControllerRef.current = new AbortController();
    setIsLoading(true);

    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No authentication token found");

      const response = await fetch(
        "http://localhost:8080/api/activity/search",
        {
          method: "POST",
          signal: abortControllerRef.current.signal,
          headers: { 
            "Content-Type": "application/json",
            Authorization: token,
          },
          body: JSON.stringify(
            {
              date: searchData.date,
              type: searchData.type,
              searchString: searchData.searchString,
            }         
          ),
        });

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
  }, []);

  return { events, isLoading, error, fetchMatchingEvents };
}
