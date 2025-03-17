"use client";

import {
  Card,
  CardHeader,
  CardTitle,
  CardContent,
  CardFooter,
} from "@/components/ui/card";
import { Calendar as CalendarIcon, MapPin, Users, Trash2, Globe, Lock } from "lucide-react";
import { SearchedEventsFetch } from "../../api/events/getSearchedEvents";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { useState, useEffect } from 'react';
import { Input } from "@/components/ui/input";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Textarea } from "@/components/ui/textarea";
import { format } from "date-fns";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { Calendar } from "@/components/ui/calendar";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useToast } from "@/hooks/use-toast";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { stringify } from "postcss";

// form schema for search
const formSchema = z.object({
  date: z.date({ required_error: "Velg en dato" }),
  hour: z.string().min(1),
  minute: z.string().min(1),
  type: z.string().min(1, "Velg en type"),
  searchString: z.string(),
});

// search init
const CreateSearch = () => {
  const { fetchMatchingEvents, events, error } = SearchedEventsFetch();
  const [isLoading, setIsLoading] = useState(false);
  const [isAdmin, setIsAdmin] = useState(false);
  const { toast } = useToast();
  const router = useRouter();
  const form = useForm({
    resolver: zodResolver(formSchema),
    defaultValues: {
      date: undefined,
      hour: "00",
      minute: "00",
      type: undefined,
      searchString: "",
    },
  });

  //on submit logic
  const onSubmit = async (data) => {
    setIsLoading(true);
    try {
      const dateTime = new Date(
        `${format(data.date, "yyyy-MM-dd")}T${data.hour}:${data.minute}:00Z`
      ).toISOString();

      const searchData = {
        date: dateTime,
        type: data.type,
        searchString: data.searchString
      };

      await fetchMatchingEvents(searchData);


    } catch (error) {
      toast({
        title: "Error",
        description: `Kunne ikke finne arrangementer. Prøv igjen.`,
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        setIsAdmin(payload.isAdmin === true);
      } catch (e) {
        console.error('Error parsing JWT token:', e);
        setIsAdmin(false);
      }
    }
  }, []);

  const handleDelete = async (eventId) => {
    const token = localStorage.getItem('token');
    try {
      const response = await fetch(`http://localhost:8080/admin/event/${eventId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': token,
          'Content-Type': 'application/json'
        },
      });

      if (response.ok) {
        window.location.reload();
      } else {
        console.error('Failed to delete event');
      }
    } catch (error) {
      console.error('Error deleting event:', error);
    }
  };

  if (isLoading) return <div>Loading events...</div>;
  if (error) return <div className="error">Error: {error}</div>;

  return (
    <div className="max-w-[1200px] mx-auto px-auto animate-in">
      <div>
        <Card>
          <CardHeader>
            <CardTitle>Søk etter arrangementer</CardTitle>
          </CardHeader>
          <CardContent>
            <Form {...form}>
              <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                <div className="space-y-2">
                  <div className="flex flex-wrap gap-4">
                    <FormField
                      control={form.control}
                      name="date"
                      render={({ field }) => (
                        <FormItem className="flex flex-col">
                          <FormLabel className="text-sm">
                            Dato
                          </FormLabel>
                          <Popover>
                            <PopoverTrigger asChild>
                              <FormControl>
                                <Button
                                  variant="outline"
                                  className="w-[144px] h-10"
                                >
                                  {field.value ? (
                                    format(field.value, "PPP")
                                  ) : (
                                    <span>Velg Dato</span>
                                  )}
                                </Button>
                              </FormControl>
                            </PopoverTrigger>
                            <PopoverContent>
                              <Calendar
                                mode="single"
                                selected={field.value}
                                onSelect={field.onChange}
                                fromDate={new Date()}
                              />
                            </PopoverContent>
                          </Popover>
                          <FormMessage />
                        </FormItem>
                      )}
                    />

                    <div className="flex gap-4">
                      <FormField
                        control={form.control}
                        name="hour"
                        render={({ field }) => (
                          <FormItem className="flex flex-col">
                            <FormLabel className="text-sm">
                              Time
                            </FormLabel>
                            <Select
                              onValueChange={field.onChange}
                              defaultValue={field.value}
                            >
                              <FormControl>
                                <SelectTrigger className="w-24 h-10">
                                  <SelectValue placeholder="Time" />
                                </SelectTrigger>
                              </FormControl>
                              <SelectContent>
                                {Array.from({ length: 24 }, (_, i) => (
                                  <SelectItem
                                    key={i}
                                    value={i.toString().padStart(2, "0")}
                                  >
                                    {i.toString().padStart(2, "0")}
                                  </SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="minute"
                        render={({ field }) => (
                          <FormItem className="flex flex-col">
                            <FormLabel className="text-sm">
                              Minutt
                            </FormLabel>
                            <Select
                              onValueChange={field.onChange}
                              defaultValue={field.value}
                            >
                              <FormControl>
                                <SelectTrigger className="w-28 h-10">
                                  <SelectValue placeholder="Min" />
                                </SelectTrigger>
                              </FormControl>
                              <SelectContent>
                                {Array.from({ length: 60 }, (_, i) => (
                                  <SelectItem
                                    key={i}
                                    value={i.toString().padStart(2, "0")}
                                  >
                                    {i.toString().padStart(2, "0")}
                                  </SelectItem>
                                ))}
                              </SelectContent>
                            </Select>
                            <FormMessage />
                          </FormItem>
                        )}
                      />

                      <FormField
                        control={form.control}
                        name="type"
                        render={({ field }) => (
                          <FormItem className="flex flex-col">
                            <FormLabel className="text-sm">
                              Type
                            </FormLabel>
                            <Select
                              onValueChange={field.onChange}
                              defaultValue={field.value}
                            >
                              <FormControl>
                                <SelectTrigger className="w-28 h-10 flex items-center justify-between">
                                  <SelectValue placeholder="Velg type" />
                                </SelectTrigger>
                              </FormControl>
                              <SelectContent>
                                <SelectItem value="Konsert">Konsert</SelectItem>
                                <SelectItem value="Fest">Fest</SelectItem>
                                <SelectItem value="Markering">Markering</SelectItem>
                                <SelectItem value="Show">Show</SelectItem>
                                <SelectItem value="Musikk">Musikk</SelectItem>
                                <SelectItem value="Annet">Annet</SelectItem>
                              </SelectContent>
                            </Select>
                          </FormItem>
                        )}
                      />
                    </div>
                  </div>
                </div>

                <FormField
                  control={form.control}
                  name="searchString"
                  render={({ field }) => (
                    <FormItem>
                      <FormLabel>Søk</FormLabel>
                      <FormControl>
                        <Textarea
                          placeholder="Skriv inn søk her..."
                          {...field}
                        />
                      </FormControl>
                      <FormMessage />
                    </FormItem>
                  )}
                />

                <Button type="submit" className="w-full" disabled={isLoading}>
                  {isLoading ? "Søker..." : "Søk"}
                </Button>
                <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3 place-items-center m-8">
                  {events.map((event) => (
                    <Card
                      key={event.id}
                      className="card-hover-effect w-full border-3 border-gray300"
                    >
                      <CardHeader>
                        <CardTitle>{event.title}</CardTitle>
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
                      </CardHeader>
                      <CardContent className="space-y-2">
                        <div className="flex items-center text-sm text-muted-foreground">
                          <CalendarIcon className="mr-2 h-4 w-4" />
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
                      </CardContent>
                      <CardFooter className="flex flex-col gap-2 w-full">
                        <Link href={`/events/${event.id}`} className="w-full">
                          <Button variant="default" className="w-full">
                            Se Detaljer
                          </Button>
                        </Link>
                        {isAdmin && (
                          <Button
                            variant="destructive"
                            className="w-full"
                            onClick={() => handleDelete(event.id)}
                          >
                            <Trash2 className="mr-2 h-4 w-4" />
                            Slett Arrangement
                          </Button>
                        )}
                      </CardFooter>
                    </Card>
                  ))}
                </div>
              </form>
            </Form>
          </CardContent>
        </Card>
      </div>
    </div>
  );
};

export default CreateSearch;