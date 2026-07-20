import { Box, Paper, Typography } from "@mui/material";
import ConstructionOutlinedIcon from "@mui/icons-material/ConstructionOutlined";

const ComingSoon = ({ title }) => {
  return (
    <Box>
      <Typography variant="h4" fontWeight={800} mb={3}>
        {title}
      </Typography>

      <Paper
        elevation={0}
        sx={{
          p: { xs: 4, md: 6 },
          borderRadius: 3,
          border: "1px solid #ececf1",
          display: "flex",
          flexDirection: "column",
          alignItems: "center",
          textAlign: "center",
          gap: 1.5,
        }}
      >
        <Box
          sx={{
            width: 72,
            height: 72,
            borderRadius: "50%",
            bgcolor: "#efe7ff",
            color: "#7c3aed",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
          }}
        >
          <ConstructionOutlinedIcon sx={{ fontSize: 34 }} />
        </Box>
        <Typography fontWeight={700}>{title} is coming soon.</Typography>
        <Typography variant="body2" color="text.secondary">
          This feature is under construction.
        </Typography>
      </Paper>
    </Box>
  );
};

export default ComingSoon;
