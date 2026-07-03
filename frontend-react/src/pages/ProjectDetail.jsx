import { useCallback, useEffect, useState } from "react";
import { Link as RouterLink, useLocation, useParams } from "react-router-dom";
import {
  Alert,
  Box,
  Breadcrumbs,
  Button,
  CircularProgress,
  IconButton,
  Link,
  Menu,
  MenuItem,
  Paper,
  Stack,
  Tab,
  Tabs,
  Typography,
} from "@mui/material";
import NavigateNextRoundedIcon from "@mui/icons-material/NavigateNextRounded";
import EditOutlinedIcon from "@mui/icons-material/EditOutlined";
import MoreVertRoundedIcon from "@mui/icons-material/MoreVert";
import { getProjectById } from "../api/project";
import { getDocuments } from "../api/document";
import { avatarColor } from "../utils/format";
import ProjectOverview from "../components/project/ProjectOverview";
import ProjectDocuments from "../components/project/ProjectDocuments";
import ProjectChat from "../components/project/ProjectChat";

const ProjectDetail = () => {
  const { projectId } = useParams();
  const location = useLocation();

  const [project, setProject] = useState(location.state?.project || null);
  const [loading, setLoading] = useState(!location.state?.project);
  const [error, setError] = useState("");
  const [tab, setTab] = useState(0);
  const [menuAnchor, setMenuAnchor] = useState(null);

  const [documents, setDocuments] = useState([]);
  const [docsLoading, setDocsLoading] = useState(true);

  const loadProject = useCallback(() => {
    getProjectById(projectId)
      .then((response) => {
        setProject(response.data);
      })
      .catch((err) => {
        setError(err.response?.data?.message || "Failed to load project.");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [projectId]);

  const loadDocuments = useCallback(() => {
    getDocuments(projectId)
      .then((response) => {
        setDocuments(Array.isArray(response.data) ? response.data : []);
      })
      .catch(() => {
        setDocuments([]);
      })
      .finally(() => {
        setDocsLoading(false);
      });
  }, [projectId]);

  useEffect(() => {
    loadProject();
    loadDocuments();
  }, [loadProject, loadDocuments]);

  if (loading) {
    return (
      <Stack alignItems="center" py={10}>
        <CircularProgress sx={{ color: "#7c3aed" }} />
      </Stack>
    );
  }

  if (error) {
    return <Alert severity="error">{error}</Alert>;
  }

  const initial = (project?.name || "?").charAt(0).toUpperCase();
  const { bg, color } = avatarColor(project?.id);

  return (
    <Box>
      <Breadcrumbs
        separator={<NavigateNextRoundedIcon fontSize="small" />}
        sx={{ mb: 2, fontSize: 14 }}
      >
        <Link
          component={RouterLink}
          to="/dashboard/projects"
          underline="hover"
          sx={{ color: "#7c3aed", fontWeight: 600 }}
        >
          Projects
        </Link>
        <Typography color="text.secondary" fontWeight={600}>
          {project?.name}
        </Typography>
      </Breadcrumbs>

      <Paper
        elevation={0}
        sx={{
          p: { xs: 2.5, md: 3 },
          borderRadius: 3,
          border: "1px solid #ececf1",
          mb: 3,
        }}
      >
        <Stack
          direction={{ xs: "column", sm: "row" }}
          justifyContent="space-between"
          alignItems={{ xs: "flex-start", sm: "center" }}
          spacing={2}
        >
          <Stack direction="row" spacing={2} alignItems="center" sx={{ minWidth: 0 }}>
            <Box
              sx={{
                width: 52,
                height: 52,
                borderRadius: 2.5,
                bgcolor: bg,
                color,
                fontWeight: 800,
                fontSize: 22,
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
                flexShrink: 0,
              }}
            >
              {initial}
            </Box>
            <Box sx={{ minWidth: 0 }}>
              <Typography variant="h5" fontWeight={800} noWrap>
                {project?.name}
              </Typography>
              <Typography
                variant="body2"
                color="text.secondary"
                sx={{
                  display: "-webkit-box",
                  WebkitLineClamp: 1,
                  WebkitBoxOrient: "vertical",
                  overflow: "hidden",
                }}
              >
                {project?.description}
              </Typography>
            </Box>
          </Stack>

          <Stack direction="row" spacing={1} alignItems="center" flexShrink={0}>
            <Button
              variant="outlined"
              startIcon={<EditOutlinedIcon />}
              sx={{
                borderRadius: 2,
                textTransform: "none",
                fontWeight: 700,
                borderColor: "#ddd6fe",
                color: "#6d28d9",
                "&:hover": { borderColor: "#c4b5fd", bgcolor: "#f5f3ff" },
              }}
            >
              Edit Project
            </Button>
            <IconButton onClick={(e) => setMenuAnchor(e.currentTarget)}>
              <MoreVertRoundedIcon />
            </IconButton>
            <Menu
              anchorEl={menuAnchor}
              open={Boolean(menuAnchor)}
              onClose={() => setMenuAnchor(null)}
            >
              <MenuItem onClick={() => setMenuAnchor(null)} sx={{ fontSize: 14 }}>
                Edit Project
              </MenuItem>
            </Menu>
          </Stack>
        </Stack>

        <Tabs
          value={tab}
          onChange={(_, value) => setTab(value)}
          sx={{
            mt: 2.5,
            minHeight: 0,
            "& .MuiTab-root": {
              textTransform: "none",
              fontWeight: 700,
              fontSize: 14,
              minHeight: 40,
              color: "#6b7280",
            },
            "& .Mui-selected": { color: "#6d28d9 !important" },
            "& .MuiTabs-indicator": { backgroundColor: "#6d28d9" },
          }}
        >
          <Tab label="Overview" />
          <Tab label="Documents" />
          <Tab label="AI Chat" />
        </Tabs>
      </Paper>

      {/* Keep every panel mounted and toggle visibility so in-tab state
          (e.g. the AI chat history) survives sub-tab navigation. */}
      <Box sx={{ display: tab === 0 ? "block" : "none" }}>
        <ProjectOverview project={project} documentCount={documents.length} />
      </Box>
      <Box sx={{ display: tab === 1 ? "block" : "none" }}>
        <ProjectDocuments
          projectId={projectId}
          documents={documents}
          loading={docsLoading}
          onUploaded={loadDocuments}
        />
      </Box>
      <Box sx={{ display: tab === 2 ? "block" : "none" }}>
        <ProjectChat projectId={projectId} />
      </Box>
    </Box>
  );
};

export default ProjectDetail;
