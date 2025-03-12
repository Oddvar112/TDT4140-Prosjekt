"use client";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
  FormDescription,
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
import { ChevronLeft } from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { useToast } from "@/hooks/use-toast";
import { useState } from "react";
import { postCreateEvent } from "@/api/events/createEvent";
import { useRouter } from "next/navigation";

// form schema
const formSchema = z.object({
  title: z.string().min(1, "Arrangement tittel må fylles ut"),
  date: z.date({ required_error: "Velg en dato" }),
  hour: z.string().min(1),
  minute: z.string().min(1),
  location: z.string().min(1, "Lokasjon må fylles ut"),
  description: z.string().min(1, "Beskrivelse må fylles ut"),
  isPrivate: z.boolean().default(false),
});

// event init
const CreateEvent = () => {
  const { createEvent } = postCreateEvent();
  const [isLoading, setIsLoading] = useState(false);
  const { toast } = useToast();
  const router = useRouter();
  const form = useForm({
    resolver: zodResolver(formSchema),
    defaultValues: {
      title: "",
      date: undefined,
      hour: "00",
      minute: "00",
      location: "",
      description: "",
      isPrivate: false,
    },
  });

  //on submit logic
  const onSubmit = async (data) => {
    setIsLoading(true);
    try {
      const dateTime = new Date(
        `${format(data.date, "yyyy-MM-dd")}T${data.hour}:${data.minute}:00Z`
      ).toISOString();

      const eventData = {
        title: data.title,
        dateTime: dateTime,
        location: data.location,
        description: data.description,
        isPrivate: data.isPrivate,
      };

      const success = await createEvent(eventData);

      if (success) {
        toast({
          title: "Success",
          description: "Arrangmentet har blitt opprettet",
        });
        form.reset();
      } else {
        toast({
          title: "Error",
          description: "Kunne ikke opprettet arrangmentet. Prøv igjen.",
          variant: "destructive",
        });
      }
    } catch (error) {
      toast({
        title: "Error",
        description: "Kunne ikke opprettet arrangmentet. Prøv igjen.",
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="px-6 max-w-2xl mx-auto space-y-8 animate-in">
      <header className="text-center space-y-4 my-14">
        <h1 className="text-4xl font-bold header-gradient">
          Opprett Arrangement
        </h1>
        <p className="text-muted-foreground">Del dine arrangmenter med andre</p>
      </header>
      <Button
        variant="ghost"
        onClick={() => router.back()}
        className="hover:bg-gray-100 rounded-full p-2"
      >
        <ChevronLeft className="h-6 w-6" />
        <span className="sr-only">Back</span>
      </Button>

      {/* Card form */}
      <Card>
        <CardHeader>
          <CardTitle>Arrangement Detaljer</CardTitle>
        </CardHeader>
        <CardContent>
          <Form {...form}>
            <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
              {/* Title field */}
              <FormField
                control={form.control}
                name="title"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Arrangment Tittel</FormLabel>
                    <FormControl>
                      <Input placeholder="Skriv inn tittel her..." {...field} />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Date and Time fields */}
              <div className="space-y-2">
                <FormLabel>Dato & Tid</FormLabel>
                <div className="flex flex-wrap gap-4">
                  <FormField
                    control={form.control}
                    name="date"
                    render={({ field }) => (
                      <FormItem className="flex flex-col">
                        <FormLabel className="text-sm text-muted-foreground">
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
                          <FormLabel className="text-sm text-muted-foreground">
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
                          <FormLabel className="text-sm text-muted-foreground">
                            Minutt
                          </FormLabel>
                          <Select
                            onValueChange={field.onChange}
                            defaultValue={field.value}
                          >
                            <FormControl>
                              <SelectTrigger className="w-24 h-10">
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
                  </div>
                </div>
              </div>

              {/* Location field */}
              <FormField
                control={form.control}
                name="location"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Lokasjon</FormLabel>
                    <FormControl>
                      <Input
                        placeholder="Skriv inn lokasjon her..."
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Description field */}
              <FormField
                control={form.control}
                name="description"
                render={({ field }) => (
                  <FormItem>
                    <FormLabel>Beskrivelse</FormLabel>
                    <FormControl>
                      <Textarea
                        placeholder="Skriv inn beskrivelse her..."
                        {...field}
                      />
                    </FormControl>
                    <FormMessage />
                  </FormItem>
                )}
              />

              {/* Private event checkbox */}
              <FormField
                control={form.control}
                name="isPrivate"
                render={({ field }) => (
                  <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md border p-4">
                    <FormControl>
                      <input
                        type="checkbox"
                        checked={field.value}
                        onChange={field.onChange}
                        className="h-4 w-4 mt-1"
                      />
                    </FormControl>
                    <div className="space-y-1 leading-none">
                      <FormLabel>Privat Arrangement</FormLabel>
                      <FormDescription>
                        Gjør arrangementet privat for å kontrollere hvem som kan delta
                      </FormDescription>
                    </div>
                  </FormItem>
                )}
              />

              {/* Submit button */}
              <Button type="submit" className="w-full" disabled={isLoading}>
                {isLoading ? "Oppretter..." : "Opprett Arrangment"}
              </Button>
            </form>
          </Form>
        </CardContent>
      </Card>
    </div>
  );
};

export default CreateEvent;