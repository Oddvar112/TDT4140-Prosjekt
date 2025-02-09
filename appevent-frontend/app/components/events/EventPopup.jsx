import React from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  Button,
  Typography,
  Box,
  Grid2 as Grid,
} from "@mui/material";
import CalendarMonthIcon from "@mui/icons-material/CalendarMonth";
import LocationOnIcon from "@mui/icons-material/LocationOn";
import PeopleIcon from "@mui/icons-material/People";
import {
  dialogTitleStyles,
  captionTextStyles,
  gridContainerStyles,
  iconContainerStyles,
  buttonContainerStyles,
  primaryButtonStyles,
} from "./styles/EventPopupStyles";

const CustomBox = ({ icon, title, children }) => (
  <Grid xs={12} sm={6}>
    <Box>
      <Typography variant="caption" sx={captionTextStyles}>
        {title}
      </Typography>
      <Box sx={iconContainerStyles}>
        {icon}
        <Typography sx={{ pt: 0.5 }}>{children}</Typography>
      </Box>
    </Box>
  </Grid>
);

const EventPopup = ({ open, onClose, event, formattedDate }) => {
  if (!event) return null;

  return (
    <Dialog
      open={open}
      onClose={onClose}
      fullWidth
      maxWidth="sm"
      disableEnforceFocus
    >
      <DialogTitle sx={dialogTitleStyles}>{event.title}</DialogTitle>

      <DialogContent>
        <Typography variant="caption" sx={captionTextStyles}>
          Detaljer:
        </Typography>
        <Typography variant="body1">{event.description}</Typography>

        <Grid container spacing={4} sx={gridContainerStyles}>
          <CustomBox
            title="Dato og tid:"
            icon={<CalendarMonthIcon sx={{ mr: 1 }} />}
          >
            {formattedDate}
          </CustomBox>

          <CustomBox title="Lokasjon:" icon={<LocationOnIcon sx={{ mr: 1 }} />}>
            {event.location}
          </CustomBox>
        </Grid>

        <Box mt={3}>
          <Typography variant="caption" sx={captionTextStyles}>
            Antall påmeldte:
          </Typography>
          <Box sx={iconContainerStyles}>
            <PeopleIcon sx={{ mr: 1 }} />
            <Typography sx={{ pt: 0.5 }}>
              {event.participants?.length || 0}
            </Typography>
          </Box>
        </Box>

        <Box sx={buttonContainerStyles}>
          <Button {...primaryButtonStyles}>Meld På</Button>
          <Box sx={{ flexGrow: 1 }} />
          <Button size="small" onClick={onClose} color="primary">
            Lukk
          </Button>
        </Box>
      </DialogContent>
    </Dialog>
  );
};

export default EventPopup;
