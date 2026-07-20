import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Menu,
  MenuItem,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import AddRoundedIcon from "@mui/icons-material/AddRounded";
import MoreVertRoundedIcon from "@mui/icons-material/MoreVert";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import AutoAwesomeOutlinedIcon from "@mui/icons-material/AutoAwesomeOutlined";
import FolderOffOutlinedIcon from "@mui/icons-material/FolderOffOutlined";
import { getProjects, createProject } from "../api/project";
import { avatarColor, formatDate } from "../utils/format";

const gradientBtn = {
  borderRadius: 2,
  textTransform: "none",
  fontWeight: 700,
  background: "linear-gradient(135deg, #7c3aed, #5b21b6)",
};

const ProjectCard = ({ project, onOpen }) => {
  const [anchorEl, setAnchorEl] = useState(null);
  const initial = (project.name || "?").charAt(0).toUpperCase();
  const { bg, color } = avatarColor(project.id);

  return (
    <Paper
      elevation={0}
      sx={{
        p: 2.5,
        borderRadius: 3,
        border: "1px solid #ececf1",
        display: "flex",
        flexDirection: "column",
        height: "100%",
        transition: "box-shadow .2s, border-color .2s",
        "&:hover": {
          boxShadow: "0 12px 30px rgba(17,24,39,0.08)",
          borderColor: "#ddd6fe",
        },
      }}
    >
      <Stack direction="row" spacing={1.5} alignItems="center" sx={{ minWidth: 0 }}>
        <Box
          sx={{
            width: 44,
            height: 44,
            borderRadius: 2.5,
            bgcolor: bg,
            color,
            fontWeight: 800,
            fontSize: 18,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            flexShrink: 0,
          }}
        >
          {initial}
        </Box>

        <Box sx={{ flexGrow: 1, minWidth: 0 }}>
          <Typography fontWeight={800} fontSize={15.5} noWrap>
            {project.name}
          </Typography>
          <Typography variant="caption" color="text.secondary" noWrap>
            {formatDate(project.createdDate)}
          </Typography>
        </Box>

        <IconButton size="small" onClick={(e) => setAnchorEl(e.currentTarget)} sx={{ flexShrink: 0 }}>
          <MoreVertRoundedIcon fontSize="small" />
        </IconButton>
        <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={() => setAnchorEl(null)}>
          <MenuItem
            onClick={() => {
              setAnchorEl(null);
              onOpen(project);
            }}
            sx={{ fontSize: 14 }}
          >
            Open project
          </MenuItem>
        </Menu>
      </Stack>

      <Box
        sx={{
          display: "grid",
          gridTemplateColumns: "1fr 1fr 1fr",
          alignItems: "center",
          columnGap: 1,
          mt: "auto",
          pt: 2.5,
          borderTop: "1px solid #f3f4f6",
          mx: -0.5,
        }}
      >
        <Stack direction="row" spacing={0.75} alignItems="center" justifyContent="center" color="#6b7280">
          <DescriptionOutlinedIcon sx={{ fontSize: 16 }} />
          <Typography variant="caption" whiteSpace="nowrap" sx={{ lineHeight: 1 }}>
            {project.documentCount ?? 0} Docs
          </Typography>
        </Stack>

        <Stack direction="row" spacing={0.75} alignItems="center" justifyContent="center" color="#6b7280">
          <AutoAwesomeOutlinedIcon sx={{ fontSize: 16 }} />
          <Typography variant="caption" whiteSpace="nowrap" sx={{ lineHeight: 1 }}>
            AI
          </Typography>
        </Stack>

        <Button
          variant="outlined"
          size="small"
          onClick={() => onOpen(project)}
          sx={{
            borderRadius: 2,
            justifySelf: "center",
            borderColor: "#e5e7eb",
            color: "#6d28d9",
            "&:hover": { borderColor: "#ddd6fe", bgcolor: "#f5f3ff" },
          }}
        >
          Open
        </Button>
      </Box>
    </Paper>
  );
};

const Projects = () => {
  const navigate = useNavigate();
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [dialogOpen, setDialogOpen] = useState(false);
  const [form, setForm] = useState({ name: "", description: "" });
  const [saving, setSaving] = useState(false);
  const [saveError, setSaveError] = useState("");

  const loadProjects = useCallback(() => {
    getProjects()
      .then((response) => {
        setProjects(Array.isArray(response.data) ? response.data : []);
        setError("");
      })
      .catch((err) => {
        setError(err.response?.data?.message || "Failed to load projects.");
      })
      .finally(() => {
        setLoading(false);
      });
  }, []);

  useEffect(() => {
    loadProjects();
  }, [loadProjects]);

  const openProject = (project) => {
    navigate(`/dashboard/projects/${project.id}`, { state: { project } });
  };

  const closeDialog = () => {
    if (saving) return;
    setDialogOpen(false);
    setForm({ name: "", description: "" });
    setSaveError("");
  };

  const handleCreate = async (event) => {
    event.preventDefault();
    setSaveError("");
    try {
      setSaving(true);
      const response = await createProject({
        name: form.name.trim(),
        description: form.description.trim(),
      });
      setDialogOpen(false);
      setForm({ name: "", description: "" });
      if (response.data?.id) {
        openProject(response.data);
      } else {
        setLoading(true);
        loadProjects();
      }
    } catch (err) {
      setSaveError(err.response?.data?.message || "Failed to create project.");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box>
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        alignItems={{ xs: "flex-start", sm: "center" }}
        spacing={2}
        mb={4.5}
        sx={{ width: "100%" }}
      >
        <Box sx={{ flexGrow: 1, minWidth: 0 }}>
          <Typography variant="h4" fontWeight={800}>
            Projects
          </Typography>
          <Typography color="text.secondary" mb={10} mt={0.5}>
            {loading
              ? "Manage all your projects in one place."
              : `Manage all your projects in one place · ${projects.length} ${
                  projects.length === 1 ? "project" : "projects"
                }`}
          </Typography>
        </Box>
        <Button
          variant="contained"
          size="small"
          startIcon={<AddRoundedIcon />}
          onClick={() => setDialogOpen(true)}
          sx={{ ...gradientBtn, px: 2, py: 0.85, fontSize: 13.5, alignSelf: { sm: "center" } }}
        >
          New Project
        </Button>
      </Stack>

      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {loading ? (
        <Stack alignItems="center" py={8}>
          <CircularProgress sx={{ color: "#7c3aed" }} />
        </Stack>
      ) : projects.length === 0 ? (
        <Paper
          elevation={0}
          sx={{
            p: { xs: 4, md: 6 },
            borderRadius: 3,
            border: "1px solid #ececf1",
            textAlign: "center",
          }}
        >
          <Stack alignItems="center" spacing={1.5}>
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
              <FolderOffOutlinedIcon sx={{ fontSize: 34 }} />
            </Box>
            <Typography fontWeight={700}>No projects yet.</Typography>
            <Typography variant="body2" color="text.secondary">
              Create your first project to get started.
            </Typography>
          </Stack>
        </Paper>
      ) : (
        <Box
          sx={{
            display: "grid",
            gridTemplateColumns: {
              xs: "1fr",
              sm: "repeat(2, minmax(0, 1fr))",
              lg: "repeat(3, minmax(0, 1fr))",
            },
            gap: 2.5,
          }}
        >
          {projects.map((project) => (
            <ProjectCard key={project.id} project={project} onOpen={openProject} />
          ))}
        </Box>
      )}

      <Dialog open={dialogOpen} onClose={closeDialog} fullWidth maxWidth="sm">
        <Box component="form" onSubmit={handleCreate}>
          <DialogTitle sx={{ fontWeight: 800 }}>New Project</DialogTitle>
          <DialogContent>
            {saveError && (
              <Alert severity="error" sx={{ mb: 2 }}>
                {saveError}
              </Alert>
            )}
            <TextField
              fullWidth
              label="Project Name"
              value={form.name}
              onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
              margin="normal"
              required
              autoFocus
            />
            <TextField
              fullWidth
              label="Description"
              value={form.description}
              onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
              margin="normal"
              required
              multiline
              minRows={4}
            />
          </DialogContent>
          <DialogActions sx={{ px: 3, pb: 2.5 }}>
            <Button
              onClick={closeDialog}
              disabled={saving}
              sx={{ textTransform: "none", fontWeight: 700, color: "#6b7280" }}
            >
              Cancel
            </Button>
            <Button
              type="submit"
              variant="contained"
              disabled={saving}
              sx={{ ...gradientBtn, px: 2.5, py: 1 }}
            >
              {saving ? "Creating..." : "Create Project"}
            </Button>
          </DialogActions>
        </Box>
      </Dialog>
    </Box>
  );
};

export default Projects;
