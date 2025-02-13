"use client";
import { Button } from "@/components/ui/button";
import { useState, useEffect } from "react";

export const EventToggleButton = ({ activityId, onToggleSuccess }) => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const [isParticipating, setIsParticipating] = useState(false);
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
            body: JSON.stringify(activityId),
          }
        );

        if (!response.ok) throw new Error("Failed to check participation");
        setIsParticipating(await response.json());
      } catch (err) {
        setError(err.message);
      }
    };

    checkParticipation();
  }, [activityId, token]);

  const handleClick = async () => {
    setIsLoading(true);
    setError(null);

    try {
      const response = await fetch(
        "http://localhost:8080/api/activity/toggleParticipation",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: token,
          },
          body: JSON.stringify(activityId),
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Failed to toggle participation");
      }

      setIsParticipating((prev) => !prev);
      onToggleSuccess();
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  if (error) return <div className="text-red-500">{error}</div>;

  return (
    <Button
      variant={isParticipating ? "destructive" : "default"}
      onClick={handleClick}
      disabled={isLoading}
      className="w-full"
    >
      {isLoading ? "Laster..." : isParticipating ? "Meld Av" : "Meld På"}
    </Button>
  );
};
