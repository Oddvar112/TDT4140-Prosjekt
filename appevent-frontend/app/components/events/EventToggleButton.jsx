import React, { useState, useEffect } from "react";
import { Button } from "@mui/material";

const EventToggleButton = ({ event, onEventUpdate }) => {
  const [isLoading, setIsLoading] = useState(true);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const checkParticipation = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/activity/isParticipating",
          {
            method: "POST",
            headers: {
              "Content-Type": "application/json",
              Authorization: token,
            },
            body: JSON.stringify(event.id),
          }
        );

        if (!response.ok) throw new Error("Failed to fetch status");

        const participationStatus = await response.json();
        onEventUpdate({
          ...event,
          isParticipating: participationStatus,
        });
      } catch (error) {
        console.error("Participation check error:", error);
      } finally {
        setIsLoading(false);
      }
    };

    checkParticipation();
  }, [event.id, token]);

  const handleClick = async () => {
    setIsLoading(true);
    try {
      const response = await fetch(
        "http://localhost:8080/api/activity/toggleParticipation",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: token,
          },
          body: JSON.stringify(event.id),
        }
      );

      if (!response.ok) throw new Error("Toggle failed");

      // Handle empty response
      let updatedEvent;
      try {
        updatedEvent = await response.json();
      } catch {
        // If no JSON body, calculate new state locally
        updatedEvent = {
          ...event,
          isParticipating: !event.isParticipating,
          participants: event.isParticipating
            ? event.participants.slice(0, -1)
            : [...event.participants, "current-user-id"],
        };
      }

      onEventUpdate(updatedEvent);
    } catch (error) {
      console.error("Toggle error:", error.message);
      // Revert to original state
      onEventUpdate(event);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Button
      size="small"
      variant="contained"
      color={
        isLoading ? "inherit" : event.isParticipating ? "error" : "primary"
      }
      onClick={handleClick}
      disabled={isLoading}
      sx={{ minWidth: 120 }}
    >
      {isLoading ? "Laster..." : event.isParticipating ? "Meld Av" : "Meld På"}
    </Button>
  );
};

export default EventToggleButton;
