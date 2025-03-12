import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Calendar, MapPin, Users, Lock, Globe } from "lucide-react";
import Link from "next/link";

export const EventCard = ({ event }) => {
  return (
    <Card>
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="text-xl">{event.title}</CardTitle>
          <div className="flex items-center gap-2">
            {event.isPrivate ? (
              <div className="flex items-center text-amber-600">
                <Lock className="h-4 w-4 mr-1" />
                <span className="text-sm font-medium">Privat</span>
              </div>
            ) : (
              <div className="flex items-center text-green-600">
                <Globe className="h-4 w-4 mr-1" />
                <span className="text-sm font-medium">Offentlig</span>
              </div>
            )}
          </div>
        </div>
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
  );
};

export default EventCard;