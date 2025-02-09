"use client";
import React, { useState, useEffect, useRef } from "react";
import SkeletonLoader from "./SkeletonLoader";
import EventCard from "./EventCard";
import { Box, Typography } from "@mui/material";
import Grid from "@mui/material/Grid2";

export default function EventFetch() {
  const [events, setEvents] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const abortControllerRef = useRef(null);

  useEffect(() => {
    const fetchEvents = async () => {
      abortControllerRef.current?.abort();
      abortControllerRef.current = new AbortController();
      setIsLoading(true);

      try {
        // Get JWT token from localStorage
        const token = localStorage.getItem("token");
        if (!token) {
          throw new Error("No authentication token found");
        }

        const response = await fetch(
          `http://localhost:8080/api/activity/upcoming`,
          {
            signal: abortControllerRef.current.signal,
            headers: {
              Authorization: `${token}`,
            },
          }
        );

        if (response.status === 401) {
          throw new Error("Unauthorized - Please login again");
        }

        if (!response.ok) {
          throw new Error(`Failed to fetch events: ${response.statusText}`);
        }

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
  }, []);

  if (isLoading) return <SkeletonLoader />;
  if (error) return <div>Something went wrong: {error}</div>;

  return (
    <Box>
      <Typography
        align="center"
        variant="h4"
        gutterBottom
        sx={{ padding: "2rem" }}
      >
        Upcoming Events
      </Typography>
      <Grid
        container
        spacing={4}
        justifyContent="center"
        sx={{
          width: "100%",
          maxWidth: "1200px",
          margin: "0 auto",
          padding: "0 24px",
        }}
      >
        {events.map((event, index) => (
          <Grid xs={12} sm={6} md={4} key={event.id || index}>
            <EventCard event={event} />
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}
