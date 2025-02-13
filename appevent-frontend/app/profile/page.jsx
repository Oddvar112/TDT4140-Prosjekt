"use client";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Calendar, MapPin, Users, ChevronLeft } from "lucide-react";
import { attendingEventsFetch } from "../../api/events/getAttendingEvents";
import Link from "next/link";
import { getUsername } from "@/utils/getinfofromJWT";
import { useRouter } from "next/navigation";

export function Profile() {
  const { events, isLoading, error } = attendingEventsFetch();
  const username = getUsername();
  const router = useRouter();
  if (isLoading) return <div>Loading...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="max-w-4xl mx-auto px-6 space-y-8 animate-in my-16">
      <header className="text-center space-y-4">
        <h1 className="text-4xl font-bold header-gradient">Min profil</h1>
      </header>
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
          <p className="text-muted-foreground">Uhh noe randome text mby her</p>
        </CardContent>
      </Card>

      <Tabs defaultValue="my-events" className="space-y-4">
        <TabsList className="grid w-full grid-cols-2">
          <TabsTrigger value="my-events">Mine Arrangementer</TabsTrigger>
          <TabsTrigger value="attending">Påmeldte Arrangementer</TabsTrigger>
        </TabsList>

        <TabsContent value="my-events">
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
                    {event.participants?.length || 0}
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
                    {event.participants?.length || 0}
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
      </Tabs>
    </div>
  );
}

export default Profile;
