import { Box, Paper, Stack, Typography } from "@mui/material";
import ImageOutlinedIcon from "@mui/icons-material/ImageOutlined";
import { formatDate } from "../../utils/format";

const SummaryItem = ({ label, value }) => (
  <Box>
    <Typography variant="caption" color="text.secondary" fontWeight={600}>
      {label}
    </Typography>
    <Typography fontWeight={600} mt={0.5} sx={{ whiteSpace: "pre-line" }}>
      {value}
    </Typography>
  </Box>
);

const ProjectOverview = ({ project, documentCount = 0, aiOutputs = 0 }) => {
  return (
    <Stack spacing={3}>
      <Paper
        elevation={0}
        sx={{ p: { xs: 3, md: 4 }, borderRadius: 3, border: "1px solid #ececf1" }}
      >
        <Typography variant="h6" fontWeight={800} mb={3}>
          Project Summary
        </Typography>

        <Box
          sx={{
            display: "grid",
            gridTemplateColumns: { xs: "1fr", sm: "2fr 1fr" },
            rowGap: 3,
            columnGap: 4,
          }}
        >
          <SummaryItem label="Project Name" value={project?.name || "—"} />
          <SummaryItem label="Total Documents" value={String(documentCount)} />
          <SummaryItem label="Description" value={project?.description || "—"} />
          <SummaryItem label="AI Outputs" value={String(aiOutputs)} />
          <SummaryItem label="Created On" value={formatDate(project?.createdDate)} />
        </Box>
      </Paper>

      <Paper
        elevation={0}
        sx={{ p: { xs: 3, md: 4 }, borderRadius: 3, border: "1px solid #ececf1" }}
      >
        <Typography variant="h6" fontWeight={800} mb={3}>
          Recent Activity
        </Typography>
        <Stack alignItems="center" textAlign="center" spacing={1.5} py={4}>
          <Box
            sx={{
              width: 64,
              height: 64,
              borderRadius: "50%",
              bgcolor: "#f3f4f6",
              color: "#9ca3af",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
            }}
          >
            <ImageOutlinedIcon sx={{ fontSize: 30 }} />
          </Box>
          <Typography color="text.secondary" fontWeight={600}>
            No activity yet.
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Upload documents or start using AI features.
          </Typography>
        </Stack>
      </Paper>
    </Stack>
  );
};

export default ProjectOverview;
