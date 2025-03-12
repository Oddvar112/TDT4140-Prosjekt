"use client";

import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
import { Calendar, MapPin, Users, ChevronLeft, UserPlus, UserMinus, Search, Check, X, Bell } from "lucide-react";
import { attendingEventsFetch } from "../../api/events/getAttendingEvents";
import Link from "next/link";
import { getUsername } from "@/utils/getinfofromJWT";
import { useRouter } from "next/navigation";
import { ownedEventsFetch } from "../../api/events/getOwnedEvents";
import { pastEventsFetch } from "../../api/events/getPastEvents";

export function Profile() {
  const { events, isLoading, error } = attendingEventsFetch();
  const { events: ownedEvents, isLoading: ownedLoading, error: ownedError } = ownedEventsFetch();
  const { events: pastEvents, isLoading: pastLoading, error: pastError } = pastEventsFetch();
  const username = getUsername();
  const router = useRouter();
  const [friends, setFriends] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [searchResults, setSearchResults] = useState([]);
  const [pendingRequests, setPendingRequests] = useState([]);
  const [alert, setAlert] = useState({ show: false, message: "", type: "success" });

  useEffect(() => {
    fetchFriends();
    fetchPendingRequests();
  }, []);

  const fetchFriends = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/friends", {
        headers: {
          Authorization: `${localStorage.getItem("token")}`,
        },
      });
      if (response.ok) {
        const data = await response.json();
        setFriends(data);
      }
    } catch (error) {
      console.error("Failed to fetch friends:", error);
      setAlert({
        show: true,
        message: "Kunne ikke hente vennelisten",
        type: "error"
      });
    }
  };

  const fetchPendingRequests = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/friends/requests/pending", {
        headers: {
          Authorization: localStorage.getItem("token"),
        },
      });
      if (response.ok) {
        const data = await response.json();
        setPendingRequests(data);
      }
    } catch (error) {
      console.error("Failed to fetch friend requests:", error);
    }
  };

  const handleSearch = async () => {
    if (!searchTerm.trim()) return;
    try {
      const response = await fetch(`http://localhost:8080/api/users/search?term=${searchTerm}`, {
        headers: {
          Authorization: `${localStorage.getItem("token")}`,
        },
      });
      if (response.ok) {
        const data = await response.json();
        setSearchResults(data);
      }
    } catch (error) {
      setAlert({ show: true, message: "Søket feilet", type: "error" });
    }
  };

  const sendFriendRequest = async (userId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/friends/request/${userId}`, {
        method: "POST",
        headers: {
          Authorization: localStorage.getItem("token"),
        },
      });
      if (response.ok) {
        setAlert({ show: true, message: "Venneforespørsel sendt!", type: "success" });
        setSearchResults(searchResults.filter(user => user.id !== userId));
        setTimeout(() => setAlert({ show: false, message: "", type: "success" }), 3000);
      }
    } catch (error) {
      setAlert({ show: true, message: "Kunne ikke sende forespørsel", type: "error" });
    }
  };

  const acceptFriendRequest = async (requestId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/friends/accept/${requestId}`, {
        method: "POST",
        headers: {
          Authorization: localStorage.getItem("token"),
        },
      });
      if (response.ok) {
        setAlert({ show: true, message: "Venneforespørsel godtatt!", type: "success" });
        fetchPendingRequests();
        fetchFriends();
        setTimeout(() => setAlert({ show: false, message: "", type: "success" }), 3000);
      }
    } catch (error) {
      setAlert({ show: true, message: "Kunne ikke godta forespørselen", type: "error" });
    }
  };

  const rejectFriendRequest = async (requestId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/friends/reject/${requestId}`, {
        method: "POST",
        headers: {
          Authorization: localStorage.getItem("token"),
        },
      });
      if (response.ok) {
        setAlert({ show: true, message: "Venneforespørsel avvist", type: "success" });
        fetchPendingRequests();
        setTimeout(() => setAlert({ show: false, message: "", type: "success" }), 3000);
      }
    } catch (error) {
      setAlert({ show: true, message: "Kunne ikke avvise forespørselen", type: "error" });
    }
  };

const removeFriend = async (friendId) => {
  try {
    
    const response = await fetch(`http://localhost:8080/api/friends/remove/${friendId}`, {
      method: "DELETE",
      headers: {
        Authorization: localStorage.getItem("token"),
      },
    });
    
    
    if (response.ok) {
      setAlert({ show: true, message: "Venn fjernet", type: "success" });
      fetchFriends(); 
    } else {
      const errorText = await response.text();
      setAlert({ 
        show: true, 
        message: "Kunne ikke fjerne venn: " + (errorText || "Ukjent feil"), 
        type: "error" 
      });
    }
  } catch (error) {
    setAlert({ 
      show: true, 
      message: "Nettverksfeil ved fjerning av venn", 
      type: "error" 
    });
  }
};

  if (isLoading || pastLoading) return <div>Loading...</div>;
  if (error || pastError) return <div className="error">Error: {error || pastError}</div>;

  return (
    <div className="max-w-4xl mx-auto px-6 space-y-8 my-16">
      <header className="text-center space-y-4">
        <h1 className="text-4xl font-bold header-gradient">Min profil</h1>
      </header>
      
      {alert.show && (
        <div 
          className={`p-4 rounded-lg ${
            alert.type === "error" 
              ? "bg-red-100 text-red-800" 
              : "bg-green-100 text-green-800"
          }`}
        >
          {alert.message}
        </div>
      )}

      <Button
        variant="ghost"
        onClick={() => router.back()}
        className="hover:bg-gray-100 rounded-full p-2"
      >
        <ChevronLeft className="h-6 w-6" />
        <span className="sr-only">Back</span>
      </Button>

      <Card>
        <CardHeader>
          <CardTitle>{username}</CardTitle>
        </CardHeader>
        <CardContent>
          <p className="text-muted-foreground">
            Velkommen til din profilside. Her kan du se en oversikt over arrangementer og håndtere dine venner.
            Du kan under tidligere arrangementer se en oversikt over tidligere arrangementer du har deltatt på og 
            laste opp bilde fra arrangementet.
          </p>
        </CardContent>
      </Card>

      <Tabs defaultValue="friends" className="space-y-4">
        <TabsList className="grid w-full grid-cols-4">
          <TabsTrigger value="my-events">Mine Arrangementer</TabsTrigger>
          <TabsTrigger value="attending">Påmeldte Arrangementer</TabsTrigger>
          <TabsTrigger value="past-events">Tidligere Arrangementer</TabsTrigger>
          <TabsTrigger value="friends">Venner</TabsTrigger>
        </TabsList>

      <TabsContent value="my-events">
      <div className="space-y-4">
        {ownedEvents.map((event) => (
          <Card key={event.id}>
            <CardHeader>
              <CardTitle className="text-xl">{event.title}</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex items-center text-sm text-muted-foreground">
                <Calendar className="mr-2 h-4 w-4" />
                {new Date(event.dateTime).toLocaleString("en-GB", {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                  hour: "2-digit",
                  minute: "2-digit",
                })}
              </div>
              <div className="flex items-center text-sm text-muted-foreground">
                <MapPin className="mr-2 h-4 w-4" />
                {event.location}
              </div>
              <div className="flex items-center text-sm text-muted-foreground">
                <Users className="mr-2 h-4 w-4" />
                {event.participants?.length || 0} påmeldte
              </div>
              <div className="mt-4">
                <Link href={`/events/${event.id}`} className="w-full">
                  <Button variant="default" className="w-full">
                    Se Detaljer
                  </Button>
                </Link>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </TabsContent>

        <TabsContent value="attending">
          <div className="space-y-4">
            {events.map((event) => (
              <Card key={event.id}>
                <CardHeader>
                  <CardTitle className="text-xl">{event.title}</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                  <div className="flex items-center text-sm text-muted-foreground">
                    <Calendar className="mr-2 h-4 w-4" />
                    {new Date(event.dateTime).toLocaleString("en-GB", {
                      day: "2-digit",
                      month: "2-digit",
                      year: "numeric",
                      hour: "2-digit",
                      minute: "2-digit",
                    })}
                  </div>
                  <div className="flex items-center text-sm text-muted-foreground">
                    <MapPin className="mr-2 h-4 w-4" />
                    {event.location}
                  </div>
                  <div className="flex items-center text-sm text-muted-foreground">
                    <Users className="mr-2 h-4 w-4" />
                    {event.participants?.length || 0} påmeldte
                  </div>
                  <div className="mt-4">
                    <Link href={`/events/${event.id}`} className="w-full">
                      <Button variant="default" className="w-full">
                        Se Detaljer
                      </Button>
                    </Link>
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </TabsContent>

        <TabsContent value="past-events">
          <div className="space-y-4">
            {pastEvents.length === 0 ? (
              <Card>
                <CardContent className="text-center p-6">
                  <p className="text-muted-foreground">Du har ingen tidligere arrangementer</p>
                </CardContent>
              </Card>
            ) : (
              pastEvents.map((event) => (
                <Card key={event.id}>
                  <CardHeader>
                    <CardTitle className="text-xl">{event.title}</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-2">
                    <div className="flex items-center text-sm text-muted-foreground">
                      <Calendar className="mr-2 h-4 w-4" />
                      {new Date(event.dateTime).toLocaleString("en-GB", {
                        day: "2-digit",
                        month: "2-digit",
                        year: "numeric",
                        hour: "2-digit",
                        minute: "2-digit",
                      })}
                    </div>
                    <div className="flex items-center text-sm text-muted-foreground">
                      <MapPin className="mr-2 h-4 w-4" />
                      {event.location}
                    </div>
                    <div className="flex items-center text-sm text-muted-foreground">
                      <Users className="mr-2 h-4 w-4" />
                      {event.participants?.length || 0} påmeldte
                    </div>
                    <div className="mt-4">
                      <Link href={`/events/${event.id}`} className="w-full">
                        <Button variant="default" className="w-full">
                          Se Detaljer
                        </Button>
                      </Link>
                    </div>
                  </CardContent>
                </Card>
              ))
            )}
          </div>
        </TabsContent>

        <TabsContent value="friends" className="space-y-4">
          {pendingRequests.length > 0 && (
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Bell className="h-5 w-5" />
                  Venneanmodninger ({pendingRequests.length})
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-2">
                {pendingRequests.map((request) => (
                  <Card key={request.id}>
                    <CardContent className="flex items-center justify-between p-4">
                      <div>
                        <p className="font-medium">{request.sender.brukernavn}</p>
                        <p className="text-sm text-muted-foreground">
                          Sendt {new Date(request.sentAt).toLocaleDateString()}
                        </p>
                      </div>
                      <div className="flex gap-2">
                        <Button
                          onClick={() => acceptFriendRequest(request.id)}
                          variant="default"
                          size="sm"
                          className="bg-green-500 hover:bg-green-600 text-white"
                        >
                          <Check className="h-4 w-4 mr-1" />
                          Godta
                        </Button>
                        <Button
                          onClick={() => rejectFriendRequest(request.id)}
                          variant="destructive"
                          size="sm"
                        >
                          <X className="h-4 w-4 mr-1" />
                          Avvis
                        </Button>
                      </div>
                    </CardContent>
                  </Card>
                ))}
              </CardContent>
            </Card>
          )}

          {/* Søk etter nye venner */}
          <Card>
            <CardHeader>
              <CardTitle>Finn nye venner</CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="flex space-x-2">
                <Input
                  placeholder="Søk etter brukere..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
                />
                <Button onClick={handleSearch}>
                  <Search className="h-4 w-4 mr-2" />
                  Søk
                </Button>
              </div>
              
              {searchResults.length > 0 && (
                <div className="space-y-2">
                  {searchResults.map((user) => (
                    <Card key={user.id}>
                      <CardContent className="flex items-center justify-between p-4">
                        <span>{user.brukernavn}</span>
                        <Button 
                          onClick={() => sendFriendRequest(user.id)}
                          variant="default"
                          size="sm"
                          className="bg-blue-500 hover:bg-blue-600 text-white"
                        >
                          <UserPlus className="h-4 w-4 mr-2" />
                          Send forespørsel
                        </Button>
                      </CardContent>
                    </Card>
                  ))}
                </div>
              )}
            </CardContent>
          </Card>

          {/* Eksisterende venner */}
          <Card>
            <CardHeader>
              <CardTitle>Mine venner ({friends.length})</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              {friends.length === 0 ? (
                <p className="text-muted-foreground">
                  Du har ingen venner enda. Søk etter brukere for å legge til venner!
                </p>
              ) : (
                friends.map((friend) => (
                  <Card key={friend.id}>
                    <CardContent className="flex items-center justify-between p-4">
                    <span>{friend.brukernavn}</span>
                    <Button 
                      onClick={() => removeFriend(friend.id)}
                      variant="destructive"
                      size="sm"
                    >
                      <UserMinus className="h-4 w-4 mr-2" />
                      Fjern venn
                    </Button>
                  </CardContent>
                </Card>
              ))
            )}
          </CardContent>
        </Card>
      </TabsContent>
    </Tabs>
  </div>
);
}

export default Profile;