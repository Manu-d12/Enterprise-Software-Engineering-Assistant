import { useNavigate } from "react-router-dom";
import { Box, Button, Paper, Stack, Typography } from "@mui/material";
import FolderOpenOutlinedIcon from "@mui/icons-material/FolderOpenOutlined";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import AutoAwesomeOutlinedIcon from "@mui/icons-material/AutoAwesomeOutlined";
import CodeOutlinedIcon from "@mui/icons-material/CodeOutlined";
import InboxOutlinedIcon from "@mui/icons-material/InboxOutlined";
import { useAuth } from "../context/useAuth";

const stats = [
  {
    label: "Total Projects",
    value: 0,
    icon: <FolderOpenOutlinedIcon />,
    bg: "#efe7ff",
    color: "#7c3aed",
  },
  {
    label: "Documents Uploaded",
    value: 0,
    icon: <DescriptionOutlinedIcon />,
    bg: "#e6f7ee",
    color: "#16a34a",
  },
  {
    label: "AI Requests",
    value: 0,
    icon: <AutoAwesomeOutlinedIcon />,
    bg: "#e6f0ff",
    color: "#2563eb",
  },
  {
    label: "Code Generated",
    value: 0,
    icon: <CodeOutlinedIcon />,
    bg: "#ffeede",
    color: "#ea580c",
  },
];

const StatCard = ({ stat }) => (
  <Paper
    elevation={0}
    sx={{
      p: 3,
      borderRadius: 3,
      border: "1px solid #ececf1",
      display: "flex",
      alignItems: "center",
      gap: 2,
    }}
  >
    <Box
      sx={{
        width: 52,
        height: 52,
        borderRadius: 2.5,
        bgcolor: stat.bg,
        color: stat.color,
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
        flexShrink: 0,
      }}
    >
      {stat.icon}
    </Box>
    <Box>
      <Typography variant="body2" color="text.secondary" fontWeight={600}>
        {stat.label}
      </Typography>
      <Typography variant="h5" fontWeight={800}>
        {stat.value}
      </Typography>
    </Box>
  </Paper>
);

const Dashboard = () => {
  const navigate = useNavigate();
  const { userInfo } = useAuth();
  const displayName = userInfo?.username || userInfo?.name || "Admin";

  return (
    <Box>
      <Typography variant="h4" fontWeight={800}>
        Dashboard
      </Typography>
      <Typography variant="h6" fontWeight={700} mt={1.5}>
        Welcome back, {displayName} 👋
      </Typography>
      <Typography color="text.secondary" mb={3.5}>
        Here&apos;s what&apos;s happening with your projects.
      </Typography>

      <Box
        sx={{
          display: "grid",
          gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" },
          gap: 2.5,
        }}
      >
        {stats.map((stat) => (
          <StatCard key={stat.label} stat={stat} />
        ))}
      </Box>

      <Paper
        elevation={0}
        sx={{
          mt: 3.5,
          p: { xs: 3, md: 4 },
          borderRadius: 3,
          border: "1px solid #ececf1",
        }}
      >
        <Typography variant="h6" fontWeight={800} mb={3}>
          Recent Activity
        </Typography>

        <Stack alignItems="center" textAlign="center" spacing={1.5} py={4}>
          <Box
            sx={{
              width: 72,
              height: 72,
              borderRadius: "50%",
              bgcolor: "#f3f4f6",
              color: "#9ca3af",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <InboxOutlinedIcon sx={{ fontSize: 34 }} />
          </Box>
          <Typography color="text.secondary" fontWeight={600}>
            No activity yet.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Create a project and start using AI features.
          </Typography>
          <Button
            variant="contained"
            onClick={() => navigate("/dashboard/projects")}
            sx={{
              mt: 1,
              px: 3,
              py: 1.1,
              borderRadius: 2,
              textTransform: "none",
              fontWeight: 700,
              background: "linear-gradient(135deg, #7c3aed, #5b21b6)",
            }}
          >
            Create Your First Project
          </Button>
        </Stack>
      </Paper>
    </Box>
  );
};

export default Dashboard;
