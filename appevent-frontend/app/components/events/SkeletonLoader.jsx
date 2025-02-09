import React from "react";
import { Skeleton, Box, Grid2 as Grid } from "@mui/material";
import { grey } from "@mui/material/colors";

const skeletons = [1, 2, 3, 4, 5, 6, 7, 8, 9];
export default function SkeletonLoader() {
  return (
    <Box>
      <Grid
        container
        spacing={4}
        justifyContent="center"
        sx={{
          width: "100%",
          maxWidth: "1200px",
          margin: "0 auto",
          padding: "0 24px",
        }}
      >
        {skeletons.map((_, index) => (
          <Grid xs={12} sm={6} md={4} key={index}>
            {/* Skeleton representing an event card */}
            <Skeleton
              variant="rectangular"
              width={345}
              height={200}
              sx={{ bgcolor: grey[400], mt: "5rem" }}
            />
            <Skeleton
              variant="text"
              width={100}
              height={30}
              sx={{ bgcolor: grey[400] }}
            />
            <Skeleton
              variant="text"
              width={200}
              height={20}
              sx={{ bgcolor: grey[400] }}
            />
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}
