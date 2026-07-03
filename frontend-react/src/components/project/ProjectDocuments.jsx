import { useRef, useState } from "react";
import {
  Alert,
  Box,
  Button,
  CircularProgress,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Paper,
  Stack,
  Typography,
} from "@mui/material";
import CloudUploadOutlinedIcon from "@mui/icons-material/CloudUploadOutlined";
import InsertDriveFileOutlinedIcon from "@mui/icons-material/InsertDriveFileOutlined";
import DescriptionOffOutlinedIcon from "@mui/icons-material/FolderOffOutlined";
import { uploadDocuments } from "../../api/document";
import { fileNameFromPath } from "../../utils/format";

const ProjectDocuments = ({ projectId, documents, loading, onUploaded }) => {
  const fileInputRef = useRef(null);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleFilesSelected = async (event) => {
    const files = event.target.files;
    if (!files || files.length === 0) return;

    setError("");
    setSuccess("");
    setUploading(true);
    try {
      await uploadDocuments(projectId, files);
      setSuccess(`Uploaded ${files.length} file${files.length > 1 ? "s" : ""} successfully.`);
      onUploaded?.();
    } catch (err) {
      setError(err.response?.data?.message || "Upload failed. Please try again.");
    } finally {
      setUploading(false);
      if (fileInputRef.current) fileInputRef.current.value = "";
    }
  };

  return (
    <Paper
      elevation={0}
      sx={{ p: { xs: 3, md: 4 }, borderRadius: 3, border: "1px solid #ececf1" }}
    >
      <Stack
        direction={{ xs: "column", sm: "row" }}
        justifyContent="space-between"
        alignItems={{ xs: "flex-start", sm: "center" }}
        spacing={2}
        mb={3}
      >
        <Box>
          <Typography variant="h6" fontWeight={800}>
            Documents
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Upload files to power AI answers for this project.
          </Typography>
        </Box>

        <Button
          variant="contained"
          startIcon={<CloudUploadOutlinedIcon />}
          disabled={uploading}
          onClick={() => fileInputRef.current?.click()}
          sx={{
            borderRadius: 2,
            textTransform: "none",
            fontWeight: 700,
            px: 2.5,
            py: 1.1,
            background: "linear-gradient(135deg, #7c3aed, #5b21b6)",
          }}
        >
          {uploading ? "Uploading..." : "Upload Documents"}
        </Button>
        <input
          ref={fileInputRef}
          type="file"
          multiple
          hidden
          onChange={handleFilesSelected}
        />
      </Stack>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {success && (
        <Alert severity="success" sx={{ mb: 2 }}>
          {success}
        </Alert>
      )}

      {loading ? (
        <Stack alignItems="center" py={5}>
          <CircularProgress sx={{ color: "#7c3aed" }} />
        </Stack>
      ) : documents.length === 0 ? (
        <Stack alignItems="center" textAlign="center" spacing={1.5} py={5}>
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
            <DescriptionOffOutlinedIcon sx={{ fontSize: 30 }} />
          </Box>
          <Typography color="text.secondary" fontWeight={600}>
            No documents uploaded yet.
          </Typography>
        </Stack>
      ) : (
        <List disablePadding>
          {documents.map((doc) => (
            <ListItem
              key={doc.id}
              sx={{
                border: "1px solid #ececf1",
                borderRadius: 2,
                mb: 1.25,
                px: 2,
              }}
            >
              <ListItemIcon sx={{ minWidth: 40, color: "#7c3aed" }}>
                <InsertDriveFileOutlinedIcon />
              </ListItemIcon>
              <ListItemText
                primary={fileNameFromPath(doc.url)}
                secondary={doc.url}
                primaryTypographyProps={{ fontWeight: 600, fontSize: 14 }}
                secondaryTypographyProps={{
                  fontSize: 12,
                  sx: { wordBreak: "break-all" },
                }}
              />
            </ListItem>
          ))}
        </List>
      )}
    </Paper>
  );
};

export default ProjectDocuments;
