import {
  Box,
  Typography,
  Paper,
  Stack,
} from "@mui/material";
import SmartToyOutlinedIcon from "@mui/icons-material/SmartToyOutlined";
import AssistantIcon from "@mui/icons-material/Assistant";

const lifecycleStages = [
  "Requirements",
  "HLD",
  "LLD",
  "Code",
  "Tests",
  "Release",
];

const AuthLayout = ({ children }) => {
  return (
    <Box
      sx={{
        width: "100%",
        minHeight: "100svh",
        display: "grid",
        gridTemplateColumns: { xs: "1fr", md: "1fr 1fr" },
        background:
          "radial-gradient(circle at top left, #f3edff, transparent 34%), radial-gradient(circle at bottom right, #e8f7ff, transparent 28%), #fff",
      }}
    >
      <Box
        sx={{
          display: { xs: "none", md: "flex" },
          flexDirection: "column",
          alignItems: "center",
          justifyContent: "center",
          textAlign: "center",
          px: { md: 7, lg: 10 },
          py: 8,
        }}
      >
        <Box
          sx={{
            width: 72,
            height: 72,
            borderRadius: 4,
            bgcolor: "#efe7ff",
            color: "#6d28d9",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            mb: 3,
          }}
        >
          <SmartToyOutlinedIcon sx={{ fontSize: 42 }} />
        </Box>

        <Typography variant="h4" fontWeight={800} maxWidth={380}>
          AI Software Engineering Assistant
        </Typography>

        <Typography color="text.secondary" mt={2} maxWidth={470} lineHeight={1.7}>
          Your AI companion for the entire software development lifecycle,
          from requirements to HLD, LLD, code, tests, and release readiness.
        </Typography>

        <Box
          sx={{
            mt: 4,
            display: "flex",
            flexWrap: "wrap",
            justifyContent: "center",
            gap: 1.2,
            maxWidth: 440,
          }}
        >
          {lifecycleStages.map((stage, index) => (
            <Box
              key={stage}
              component="span"
              sx={{
                px: 1.6,
                py: 0.75,
                borderRadius: 999,
                bgcolor: index % 2 === 0 ? "#eef6ff" : "#f2edff",
                color: index % 2 === 0 ? "#075985" : "#5b21b6",
                fontSize: 13,
                fontWeight: 800,
                lineHeight: 1,
              }}
            >
              {stage}
            </Box>
          ))}
        </Box>

        <Paper
          elevation={0}
          sx={{
            mt: 7,
            width: 300,
            height: 170,
            borderRadius: 4,
            bgcolor: "#f1ecff",
            position: "relative",
            opacity: 0.8,
            p: 3,
          }}
        >
          <Stack direction="row" spacing={0.7} mb={3}>
            {[1, 2, 3].map((i) => (
              <Box
                key={i}
                sx={{
                  width: 7,
                  height: 7,
                  borderRadius: "50%",
                  bgcolor: "#c4b5fd",
                }}
              />
            ))}
          </Stack>

          <Box sx={{ height: 12, width: 170, bgcolor: "#fff", borderRadius: 2, mb: 1.5 }} />
          <Box sx={{ height: 12, width: 130, bgcolor: "#fff", borderRadius: 2, mb: 1.5 }} />
          <Box sx={{ height: 12, width: 90, bgcolor: "#fff", borderRadius: 2 }} />

          <Typography
            sx={{
              mt: 3,
              color: "#6d28d9",
              fontSize: 13,
              fontWeight: 800,
              textAlign: "left",
            }}
          >
            Plan. Design. Build. Ship.
          </Typography>

          <Box
            sx={{
              position: "absolute",
              right: -20,
              bottom: 28,
              width: 70,
              height: 54,
              borderRadius: 3,
              background: "linear-gradient(135deg, #8b5cf6, #6d28d9)",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              color: "#fff",
              boxShadow: "0 14px 30px rgba(109, 40, 217, 0.25)",
            }}
          >
            <AssistantIcon />
          </Box>
        </Paper>
      </Box>

      <Box
        sx={{
          minHeight: "100svh",
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          p: { xs: 2.5, sm: 4, lg: 6 },
        }}
      >
        {children}
      </Box>
    </Box>
  );
};

export default AuthLayout;
