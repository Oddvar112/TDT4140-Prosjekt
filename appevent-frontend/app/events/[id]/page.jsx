"use client";
import { useParams } from "next/navigation";
import { SingleEventFetch } from "../../../api/events/getEvent";
import { EventToggleButton } from "../../../components/event/EventToggleButton";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Calendar, MapPin, Users, ChevronLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";

export default function EventDetail() {
  const params = useParams();
  const { event, isLoading, error, refresh } = SingleEventFetch(params.id);
  const router = useRouter();

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
          <CardTitle className="text-3xl">{event.title}</CardTitle>
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
            <div className="flex items-center text-muted-foreground">
              <Users className="mr-2 h-5 w-5" />
              {event.participants.length || 0} påmeldte
            </div>
          </div>

          <div className="space-y-2">
            <h3 className="font-semibold">Om arrangementet</h3>
            <p className="text-muted-foreground">{event.description}</p>
          </div>

          <EventToggleButton activityId={event.id} onToggleSuccess={refresh} />
        </CardContent>
      </Card>
    </div>
  );
}
