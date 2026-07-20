import { useCallback, useEffect, useRef, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  LinearProgress,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import AutoAwesomeOutlinedIcon from "@mui/icons-material/AutoAwesomeOutlined";
import StopRoundedIcon from "@mui/icons-material/StopRounded";
import DownloadRoundedIcon from "@mui/icons-material/DownloadRounded";
import TerminalRoundedIcon from "@mui/icons-material/TerminalRounded";
import CheckCircleRoundedIcon from "@mui/icons-material/CheckCircleRounded";
import ErrorRoundedIcon from "@mui/icons-material/ErrorRounded";
import ChevronRightRoundedIcon from "@mui/icons-material/ChevronRightRounded";
import CodeOutlinedIcon from "@mui/icons-material/CodeOutlined";
import { streamGenerateCode } from "../api/codegen";

const STATUS = {
  IDLE: "idle",
  RUNNING: "running",
  COMPLETED: "completed",
  ERROR: "error",
};

const STATUS_META = {
  [STATUS.IDLE]: { label: "Idle", bg: "#f3f4f6", color: "#4b5563" },
  [STATUS.RUNNING]: { label: "Generating", bg: "#efe7ff", color: "#7c3aed" },
  [STATUS.COMPLETED]: { label: "Completed", bg: "#e6f7ee", color: "#16a34a" },
  [STATUS.ERROR]: { label: "Failed", bg: "#fde8ef", color: "#db2777" },
};

// Line accent + icon by SSE event type.
const LINE_META = {
  progress: { color: "#c4b5fd", icon: ChevronRightRoundedIcon },
  complete: { color: "#4ade80", icon: CheckCircleRoundedIcon },
  error: { color: "#f87171", icon: ErrorRoundedIcon },
  message: { color: "#94a3b8", icon: ChevronRightRoundedIcon },
};

const formatTime = (date) =>
  date.toLocaleTimeString([], { hour12: false }) +
  "." +
  String(date.getMilliseconds()).padStart(3, "0");

const CodeGenerator = () => {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState(STATUS.IDLE);
  const [logs, setLogs] = useState([]);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState("");

  const abortRef = useRef(null);
  const logEndRef = useRef(null);
  const keyRef = useRef(0);

  const isRunning = status === STATUS.RUNNING;

  const appendLog = useCallback((event, message) => {
    const entry = {
      key: keyRef.current++,
      event,
      message,
      time: new Date(),
    };
    setLogs((prev) => [...prev, entry]);
  }, []);

  // Auto-scroll the console to the newest line.
  useEffect(() => {
    logEndRef.current?.scrollIntoView({ behavior: "smooth", block: "end" });
  }, [logs]);

  // Abort any in-flight stream when the page unmounts.
  useEffect(() => () => abortRef.current?.(), []);

  const handleGenerate = (event) => {
    event?.preventDefault();
    const q = query.trim();
    if (!q || isRunning) return;

    abortRef.current?.();
    setLogs([]);
    setError("");
    setProgress(0);
    setStatus(STATUS.RUNNING);
    keyRef.current = 0;

    abortRef.current = streamGenerateCode(q, {
      onEvent: ({ event: name, data }) => {
        appendLog(name, data);

        // Derive a rough progress bar from the "Step N/M" markers.
        const stepMatch = /Step\s+(\d+)\s*\/\s*(\d+)/i.exec(data || "");
        if (stepMatch) {
          const [, current, totalRaw] = stepMatch;
          const total = Number(totalRaw) || 5;
          setProgress(Math.min(95, Math.round((Number(current) / total) * 100)));
        }

        if (name === "complete") {
          setProgress(100);
          setStatus(STATUS.COMPLETED);
        } else if (name === "error") {
          setStatus(STATUS.ERROR);
          setError(data);
        }
      },
      onError: (err) => {
        appendLog("error", err?.message || "Connection to the server failed.");
        setError(err?.message || "Connection to the server failed.");
        setStatus(STATUS.ERROR);
      },
      onClose: () => {
        // If the stream closed without an explicit complete/error, settle status.
        setStatus((prev) => (prev === STATUS.RUNNING ? STATUS.COMPLETED : prev));
      },
    });
  };

  const handleStop = () => {
    abortRef.current?.();
    abortRef.current = null;
    appendLog("error", "Generation stopped by user.");
    setStatus(STATUS.ERROR);
  };

  const handleDownload = () => {
    if (logs.length === 0) return;
    const header = [
      "AI Code Generation — Step Log",
      `Requirement: ${query.trim()}`,
      `Exported: ${new Date().toString()}`,
      "=".repeat(60),
      "",
    ].join("\n");

    const body = logs
      .map((l) => `[${formatTime(l.time)}] [${l.event.toUpperCase()}] ${l.message}`)
      .join("\n");

    const blob = new Blob([header + body + "\n"], { type: "text/plain" });
    const url = URL.createObjectURL(blob);
    const anchor = document.createElement("a");
    anchor.href = url;
    anchor.download = `code-generation-steps-${Date.now()}.txt`;
    document.body.appendChild(anchor);
    anchor.click();
    anchor.remove();
    URL.revokeObjectURL(url);
  };

  const statusMeta = STATUS_META[status];

  return (
    <Box>
      <Typography variant="h4" fontWeight={800} mb={3}>
        Code Generator
      </Typography>

      <Stack spacing={3}>
        {/* Prompt card */}
        <Paper
          elevation={0}
          sx={{ p: { xs: 2.5, md: 3 }, borderRadius: 3, border: "1px solid #ececf1" }}
        >
          <Typography variant="body2" color="text.secondary" mb={2}>
            Describe what you want to build. The engine plans a blueprint and generates the
            project file by file — you'll see every step stream in live below.
          </Typography>

          <Box component="form" onSubmit={handleGenerate}>
            <TextField
              fullWidth
              multiline
              minRows={2}
              maxRows={6}
              placeholder="e.g. A Ludo game with HTML, CSS and JS for 2 players"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              disabled={isRunning}
              onKeyDown={(e) => {
                if (e.key === "Enter" && (e.metaKey || e.ctrlKey)) handleGenerate(e);
              }}
              sx={{ "& .MuiOutlinedInput-root": { borderRadius: 2.5 } }}
            />

            <Stack
              direction="row"
              spacing={1.5}
              alignItems="center"
              justifyContent="flex-end"
              mt={2}
            >
              <Typography variant="caption" color="text.secondary" sx={{ mr: "auto" }}>
                Tip: press ⌘/Ctrl + Enter to generate
              </Typography>

              {isRunning ? (
                <Button
                  onClick={handleStop}
                  variant="outlined"
                  startIcon={<StopRoundedIcon />}
                  sx={{
                    borderRadius: 2.5,
                    textTransform: "none",
                    fontWeight: 700,
                    borderColor: "#e5e7eb",
                    color: "#db2777",
                    "&:hover": { borderColor: "#db2777", bgcolor: "#fdf2f8" },
                  }}
                >
                  Stop
                </Button>
              ) : null}

              <Button
                type="submit"
                disabled={isRunning || !query.trim()}
                variant="contained"
                disableElevation
                startIcon={
                  isRunning ? (
                    <CircularProgress size={16} sx={{ color: "#fff" }} />
                  ) : (
                    <AutoAwesomeOutlinedIcon />
                  )
                }
                sx={{
                  borderRadius: 2.5,
                  textTransform: "none",
                  fontWeight: 700,
                  px: 2.5,
                  background: "linear-gradient(135deg, #7c3aed, #5b21b6)",
                  "&:hover": { background: "linear-gradient(135deg, #6d28d9, #4c1d95)" },
                  "&.Mui-disabled": { background: "#e5e7eb", color: "#9ca3af" },
                }}
              >
                {isRunning ? "Generating…" : "Generate Code"}
              </Button>
            </Stack>
          </Box>
        </Paper>

        {error && status === STATUS.ERROR && <Alert severity="error">{error}</Alert>}

        {/* Empty state */}
        {logs.length === 0 && !isRunning && (
          <Stack alignItems="center" textAlign="center" spacing={1.5} py={6}>
            <Box
              sx={{
                width: 64,
                height: 64,
                borderRadius: "50%",
                bgcolor: "#efe7ff",
                color: "#7c3aed",
                display: "flex",
                alignItems: "center",
                justifyContent: "center",
              }}
            >
              <CodeOutlinedIcon sx={{ fontSize: 30 }} />
            </Box>
            <Typography fontWeight={700}>Nothing generated yet</Typography>
            <Typography variant="body2" color="text.secondary">
              Describe a project above and watch the build stream in real time.
            </Typography>
          </Stack>
        )}

        {/* Live console */}
        {(logs.length > 0 || isRunning) && (
          <Paper
            elevation={0}
            sx={{ borderRadius: 3, border: "1px solid #ececf1", overflow: "hidden" }}
          >
            {/* Console header */}
            <Stack
              direction="row"
              alignItems="center"
              spacing={1.25}
              sx={{
                px: { xs: 2, md: 2.5 },
                py: 1.5,
                borderBottom: "1px solid #ececf1",
                bgcolor: "#faf9fb",
              }}
            >
              <Box
                sx={{
                  width: 34,
                  height: 34,
                  borderRadius: 2,
                  bgcolor: "#efe7ff",
                  color: "#7c3aed",
                  display: "flex",
                  alignItems: "center",
                  justifyContent: "center",
                }}
              >
                <TerminalRoundedIcon fontSize="small" />
              </Box>
              <Typography fontWeight={800}>Build Console</Typography>
              <Chip
                label={statusMeta.label}
                size="small"
                icon={
                  isRunning ? (
                    <CircularProgress size={11} thickness={6} sx={{ color: statusMeta.color }} />
                  ) : undefined
                }
                sx={{
                  bgcolor: statusMeta.bg,
                  color: statusMeta.color,
                  fontWeight: 700,
                  height: 24,
                  "& .MuiChip-icon": { color: statusMeta.color, ml: 0.75 },
                }}
              />
              <Chip
                label={`${logs.length} step${logs.length === 1 ? "" : "s"}`}
                size="small"
                sx={{ bgcolor: "#f3f4f6", fontWeight: 700, height: 24 }}
              />

              <Button
                onClick={handleDownload}
                disabled={logs.length === 0}
                size="small"
                startIcon={<DownloadRoundedIcon />}
                sx={{
                  ml: "auto",
                  textTransform: "none",
                  fontWeight: 700,
                  borderRadius: 2,
                  color: "#7c3aed",
                  "&:hover": { bgcolor: "#f5f3ff" },
                }}
              >
                Download steps
              </Button>
            </Stack>

            {/* Progress bar */}
            <LinearProgress
              variant={isRunning && progress === 0 ? "indeterminate" : "determinate"}
              value={progress}
              sx={{
                height: 3,
                bgcolor: "#f3f4f6",
                "& .MuiLinearProgress-bar": {
                  background: "linear-gradient(90deg, #7c3aed, #a855f7)",
                },
              }}
            />

            {/* Log stream */}
            <Box
              sx={{
                bgcolor: "#0f172a",
                px: { xs: 2, md: 2.5 },
                py: 2,
                maxHeight: 460,
                overflowY: "auto",
                fontFamily:
                  '"SFMono-Regular", ui-monospace, Menlo, Consolas, "Liberation Mono", monospace',
                fontSize: 13,
                lineHeight: 1.7,
              }}
            >
              {logs.map((line) => {
                const meta = LINE_META[line.event] || LINE_META.message;
                const LineIcon = meta.icon;
                return (
                  <Stack
                    key={line.key}
                    direction="row"
                    spacing={1}
                    alignItems="flex-start"
                    sx={{ py: 0.15 }}
                  >
                    <Box
                      component="span"
                      sx={{ color: "#475569", flexShrink: 0, userSelect: "none" }}
                    >
                      {formatTime(line.time)}
                    </Box>
                    <LineIcon sx={{ fontSize: 16, color: meta.color, mt: "3px", flexShrink: 0 }} />
                    <Box
                      component="span"
                      sx={{ color: meta.color, whiteSpace: "pre-wrap", wordBreak: "break-word" }}
                    >
                      {line.message}
                    </Box>
                  </Stack>
                );
              })}

              {isRunning && (
                <Stack direction="row" spacing={1} alignItems="center" sx={{ py: 0.4, pl: "3px" }}>
                  <Box
                    component="span"
                    sx={{
                      width: 8,
                      height: 15,
                      bgcolor: "#c4b5fd",
                      animation: "cg-blink 1s step-start infinite",
                      "@keyframes cg-blink": { "50%": { opacity: 0 } },
                    }}
                  />
                </Stack>
              )}
              <div ref={logEndRef} />
            </Box>
          </Paper>
        )}
      </Stack>
    </Box>
  );
};

export default CodeGenerator;
