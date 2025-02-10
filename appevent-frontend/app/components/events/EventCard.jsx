import { React, useState } from "react";
import {
  Card,
  CardActionArea,
  CardActions,
  CardContent,
  Typography,
  Box,
} from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import EventPopup from "./EventPopup";
import {
  cardStyles,
  cardActionStyles,
  iconBoxStyles,
  titleStyles,
  descriptionStyles,
} from "./styles/EventCardStyles";
import { formatDateTime, truncateText } from "./utils/helpers";
import EventToggleButton from "./EventToggleButton";

export default function EventCard({ event, onEventUpdate }) {
  const maxLength = 200;
  const { truncatedText, isTruncated } = truncateText(
    event.description,
    maxLength
  );
  const formattedDate = formatDateTime(event.dateTime);
  const [open, setOpen] = useState(false);

  return (
    <>
      <Card sx={cardStyles}>
        <CardActionArea onClick={() => setOpen(true)} sx={cardActionStyles}>
          <CardContent>
            <Typography variant="h5" sx={titleStyles}>
              {event.title}
            </Typography>
            <Box sx={iconBoxStyles}>
              <CalendarMonthIcon sx={{ mr: 1 }} />
              <Typography color="text.secondary" pt={0.5}>
                {formattedDate}
              </Typography>
            </Box>
            <Box sx={{ ...iconBoxStyles, mt: 1 }}>
              <LocationOnIcon sx={{ mr: 1 }} />
              <Typography color="text.secondary" pt={0.5}>
                {event.location}
              </Typography>
            </Box>
            <Typography variant="body2" sx={descriptionStyles}>
              {truncatedText}
              {isTruncated && <strong>...Read more</strong>}
            </Typography>
          </CardContent>
        </CardActionArea>

        <CardActions sx={{ marginTop: "auto" }}>
          <EventToggleButton event={event} onEventUpdate={onEventUpdate} />
        </CardActions>
      </Card>

      <EventPopup
        open={open}
        onClose={() => setOpen(false)}
        event={event}
        formattedDate={formattedDate}
        onEventUpdate={onEventUpdate}
      />
    </>
  );
}
