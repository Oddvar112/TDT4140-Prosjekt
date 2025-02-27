"use client";

import { useState, useEffect } from "react";
import { useParams } from "next/navigation";
import { SingleEventFetch } from "../../../api/events/getEvent";
import { EventToggleButton } from "../../../components/event/EventToggleButton";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Calendar, MapPin, Users, ChevronLeft, UserPlus, Lock } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover";
import { getUserId } from "@/utils/getinfofromJWT"; 
import { useToast } from "@/hooks/use-toast";
import { Input } from "@/components/ui/input";

export default function EventDetail() {
  const params = useParams();
  const { event, isLoading, error, refresh } = SingleEventFetch(params.id);
  const router = useRouter();
  const [friends, setFriends] = useState([]);
  const [comment, setComment] = useState("");
  const userId = getUserId(); 
  const isOwner = event?.ownerId === userId;
  const { toast } = useToast();

  useEffect(() => {
    const fetchFriends = async () => {
      try {
        const response = await fetch(`http://localhost:8080/api/friends/not-invited/${params.id}`, {
          headers: {
            Authorization: localStorage.getItem("token"),
          },
        });
        if (response.ok) {
          const data = await response.json();
          setFriends(data);
        }
      } catch (error) {
        console.error("Failed to fetch friends:", error);
      }
    };

    if (isOwner && event?.isPrivate) {
      fetchFriends();
    }
  }, [isOwner, event?.isPrivate, params.id]);

  const handleInvite = async (friendId) => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/activity/${event.id}/invite/${friendId}`,
        {
          method: "POST",
          headers: {
            Authorization: localStorage.getItem("token"),
          },
        }
      );

      if (response.ok) {
        setFriends(friends.filter(friend => friend.id !== friendId));
        toast({
          title: "Invitasjon sendt",
          description: "Vennen har blitt invitert til arrangementet",
        });
      } else {
        throw new Error("Failed to send invitation");
      }
    } catch (error) {
      console.error("Failed to invite friend:", error);
      toast({
        title: "Feil",
        description: "Kunne ikke sende invitasjon",
        variant: "destructive",
      });
    }
  };

const handleCommentSubmit = async () => {
  if (!comment.trim()) return;
  try {
    const response = await fetch(`http://localhost:8080/api/activity/${event.id}/comment`, {
      method: "POST",
      headers: {
        "Authorization": localStorage.getItem("token"),
        "Content-Type": "application/json"
      },
      body: JSON.stringify({
        content: comment.trim()
      })
    });

    if (response.ok) {
      setComment("");
      refresh();
      toast({
        title: "Success",
        description: "Kommentar lagt til",
      });
    } else {
      const errorText = await response.text();
      throw new Error(errorText || "Failed to add comment");
    }
  } catch (error) {
    console.error("Failed to add comment:", error);
    toast({
      title: "Error",
      description: "Kunne ikke legge til kommentar",
      variant: "destructive",
    });
  }
};

  if (isLoading) return <div>Loading...</div>;
  if (error) return <div>Error: {error}</div>;
  if (!event) return <div>No event found</div>;

  return (
    <div className="max-w-3xl mx-auto px-6 space-y-8 animate-in my-16">
      <div className="flex items-start -mb-6">
        <Button
          variant="ghost"
          onClick={() => router.back()}
          className="hover:bg-gray-100 rounded-full p-2"
        >
          <ChevronLeft className="h-6 w-6" />
          <span className="sr-only">Back</span>
        </Button>
      </div>
      <Card>
        <CardHeader>
          <div className="flex justify-between items-center">
            <div className="space-y-1">
              <CardTitle className="text-3xl">{event.title}</CardTitle>
              {event.isPrivate && (
                <div className="flex items-center text-amber-600">
                  <Lock className="h-4 w-4 mr-1" />
                  <span className="text-sm">Privat arrangement</span>
                </div>
              )}
            </div>
            {isOwner && event.isPrivate && (
              <Popover>
                <PopoverTrigger asChild>
                  <Button variant="outline" className="ml-4">
                    <UserPlus className="h-4 w-4 mr-2" />
                    Inviter venn
                  </Button>
                </PopoverTrigger>
                <PopoverContent className="w-64">
                  <div className="space-y-2">
                    <h4 className="font-medium">Velg venn å invitere</h4>
                    {friends.length === 0 ? (
                      <p className="text-sm text-muted-foreground">
                        Du har ingen venner å invitere
                      </p>
                    ) : (
                      friends.map((friend) => (
                        <Button
                          key={friend.id}
                          variant="ghost"
                          className="w-full justify-start"
                          onClick={() => handleInvite(friend.id)}
                        >
                          {friend.brukernavn}
                        </Button>
                      ))
                    )}
                  </div>
                </PopoverContent>
              </Popover>
            )}
          </div>
          <p className="text-sm text-muted-foreground">
            Arrangert av {event.ownerUsername}
          </p>
        </CardHeader>
        <CardContent className="space-y-6">
          <div className="space-y-2">
            <div className="flex items-center text-muted-foreground">
              <Calendar className="mr-2 h-5 w-5" />
              {new Date(event.dateTime).toLocaleString("en-GB", {
                day: "2-digit",
                month: "2-digit",
                year: "numeric",
                hour: "2-digit",
                minute: "2-digit",
              })}
            </div>
            <div className="flex items-center text-muted-foreground">
              <MapPin className="mr-2 h-5 w-5" />
              {event.location}
            </div>
            <Popover>
              <PopoverTrigger asChild>
                <Button 
                  variant="outline"
                  size="sm"
                  className="bg-white hover:bg-gray-50 transition-colors"
                >
                  <div className="flex items-center">
                    <Users className="mr-2 h-4 w-4" />
                    <span className="font-medium">{event.participants.length || 0}</span>
                    <span className="ml-1 text-muted-foreground">påmeldte</span>
                  </div>
                </Button>
              </PopoverTrigger>
              <PopoverContent className="w-72 p-4">
                <div className="space-y-4">
                  <div className="flex items-center space-x-2">
                    <Users className="h-5 w-5 text-muted-foreground" />
                    <h4 className="font-semibold text-lg">Påmeldte deltakere</h4>
                  </div>
                  {event.participants.length === 0 ? (
                    <p className="text-sm text-muted-foreground text-center py-4">
                      Ingen påmeldte ennå
                    </p>
                  ) : (
                    <div className="divide-y divide-gray-100">
                      {event.participants.map((participant) => (
                        <div
                          key={participant.id}
                          className="flex items-center py-2 px-2 hover:bg-gray-50 rounded-md transition-colors cursor-default"
                        >
                          <div className="h-8 w-8 rounded-full bg-gray-100 flex items-center justify-center">
                            <span className="text-sm font-medium">
                              {participant.brukernavn.charAt(0).toUpperCase()}
                            </span>
                          </div>
                          <span className="ml-3 text-sm font-medium">
                            {participant.brukernavn}
                          </span>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </PopoverContent>
            </Popover>
          </div>

          <div className="space-y-2">
            <h3 className="font-semibold">Om arrangementet</h3>
            <p className="text-muted-foreground">{event.description}</p>
          </div>

          <EventToggleButton activityId={event.id} onToggleSuccess={refresh} />

          <div className="space-y-4">
            <h3 className="font-semibold">Kommentarer</h3>
            <div className="space-y-2">
              {event.comments?.length === 0 ? (
                <p className="text-sm text-muted-foreground">
                  Ingen kommentarer ennå. Vær den første til å kommentere!
                </p>
              ) : (
                event.comments?.map((comment) => (
                  <div key={comment.id} className="border-b pb-2 mb-2">
                    <p className="font-medium">{comment.username}</p>
                    <p className="text-sm text-muted-foreground">{comment.content}</p>
                  </div>
                ))
              )}
            </div>
            <div className="flex space-x-2">
              <Input
                placeholder="Skriv en kommentar..."
                value={comment}
                onChange={(e) => setComment(e.target.value)}
              />
              <Button onClick={handleCommentSubmit}>
                Legg til
              </Button>
            </div>
          </div>
        </CardContent>
      </Card>
    </div>
  );
}