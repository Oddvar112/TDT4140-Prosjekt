import { useState, useEffect } from "react";

export const useEventParticipation = (activityId) => {
  const [isParticipating, setIsParticipating] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const token = localStorage.getItem("token");

  useEffect(() => {
    const checkParticipation = async () => {
      if (!activityId || !token) {
        setIsLoading(false);
        return;
      }

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

        if (!response.ok) throw new Error("Participation check failed");
        setIsParticipating(await response.json());
      } catch (err) {
        setError(err.message);
      } finally {
        setIsLoading(false);
      }
    };

    checkParticipation();
  }, [activityId, token]);

  const toggleParticipation = async () => {
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
          body: JSON.stringify(activityId),
        }
      );

      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "Toggle failed");
      }

      setIsParticipating((prev) => !prev);
    } catch (err) {
      setError(err.message);
      throw err;
    } finally {
      setIsLoading(false);
    }
  };

  return { isParticipating, isLoading, error, toggleParticipation };
};
