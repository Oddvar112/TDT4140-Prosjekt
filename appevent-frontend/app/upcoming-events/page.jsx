"use client";
import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Calendar, MapPin, Users } from "lucide-react";
import { UpcomingEventsFetch } from "../../api/events/getUpcomingEvents";
import Link from "next/link";
import { Button } from "@/components/ui/button";

export function UpcomingEventPage() {
  const { events, isLoading, error } = UpcomingEventsFetch();

  if (isLoading) return <div>Loading events...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="max-w-[1200px] mx-auto px-auto animate-in">
      <header className="text-center space-y-4 my-14">
        <h1 className="text-4xl font-bold header-gradient">
          Utforsk unike arrangementer
        </h1>
        <p className="text-muted-foreground">
          Meld på arrangmenter eller opprett din egen
        </p>
      </header>

      <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 place-items-center m-8">
        {events.map((event) => (
          <Card
            key={event.id}
            className="card-hover-effect w-full border-3 border-gray300"
          >
            <CardHeader>
              <CardTitle>{event.title}</CardTitle>
            </CardHeader>
            <CardContent className="space-y-2">
              <div className="flex items-center text-sm text-muted-foreground">
                <Calendar className="mr-2 h-4 w-4" />
                {new Date(event.dateTime).toLocaleString()}
              </div>
              <div className="flex items-center text-sm text-muted-foreground">
                <MapPin className="mr-2 h-4 w-4" />
                {event.location}
              </div>
              <div className="flex items-center text-sm text-muted-foreground">
                <Users className="mr-2 h-4 w-4" />
                {event.participants?.length || 0}
              </div>
            </CardContent>
            <CardFooter>
              <Link href={`/events/${event.id}`} className="w-full">
                <Button variant="default" className="w-full">
                  Se Detaljer
                </Button>
              </Link>
            </CardFooter>
          </Card>
        ))}
      </div>
    </div>
  );
}

export default UpcomingEventPage;
