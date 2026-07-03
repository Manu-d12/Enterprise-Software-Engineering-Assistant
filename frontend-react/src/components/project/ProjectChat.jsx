import { useEffect, useRef, useState } from "react";
import {
  Avatar,
  Box,
  CircularProgress,
  IconButton,
  Paper,
  Stack,
  TextField,
  Typography,
} from "@mui/material";
import SendRoundedIcon from "@mui/icons-material/SendRounded";
import AutoAwesomeOutlinedIcon from "@mui/icons-material/AutoAwesomeOutlined";
import { askProjectStream } from "../../api/chat";

const ProjectChat = ({ projectId }) => {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const scrollRef = useRef(null);

  useEffect(() => {
    scrollRef.current?.scrollTo({ top: scrollRef.current.scrollHeight, behavior: "smooth" });
  }, [messages, loading]);

  const handleSend = async (event) => {
    event.preventDefault();
    const question = input.trim();
    if (!question || loading) return;

    // Append the user turn plus an empty assistant turn that we stream into.
    const assistantIndex = messages.length + 1;
    setMessages((prev) => [
      ...prev,
      { role: "user", content: question },
      { role: "assistant", content: "", streaming: true },
    ]);
    setInput("");
    setLoading(true);

    const updateAssistant = (patch) => {
      setMessages((prev) => {
        const next = [...prev];
        next[assistantIndex] = { ...next[assistantIndex], ...patch };
        return next;
      });
    };

    try {
      await askProjectStream(projectId, question, {
        onChunk: (_piece, full) => {
          updateAssistant({ content: full });
        },
      });
      updateAssistant({ streaming: false });
    } catch (err) {
      updateAssistant({
        content:
          err.message || "Something went wrong. Please try again.",
        error: true,
        streaming: false,
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <Paper
      elevation={0}
      sx={{
        borderRadius: 3,
        border: "1px solid #ececf1",
        display: "flex",
        flexDirection: "column",
        height: "62vh",
        overflow: "hidden",
      }}
    >
      <Box ref={scrollRef} sx={{ flexGrow: 1, overflowY: "auto", p: { xs: 2, md: 3 } }}>
        {messages.length === 0 && !loading ? (
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
              <AutoAwesomeOutlinedIcon sx={{ fontSize: 30 }} />
            </Box>
            <Typography fontWeight={700}>Ask anything about this project</Typography>
            <Typography variant="body2" color="text.secondary">
              Answers are grounded in the documents you&apos;ve uploaded.
            </Typography>
          </Stack>
        ) : (
          <Stack spacing={2}>
            {messages.map((message, index) => {
              const isUser = message.role === "user";
              return (
                <Stack
                  key={index}
                  direction="row"
                  spacing={1.25}
                  justifyContent={isUser ? "flex-end" : "flex-start"}
                >
                  {!isUser && (
                    <Avatar
                      sx={{
                        width: 32,
                        height: 32,
                        bgcolor: "#efe7ff",
                        color: "#7c3aed",
                      }}
                    >
                      <AutoAwesomeOutlinedIcon sx={{ fontSize: 18 }} />
                    </Avatar>
                  )}
                  <Box
                    sx={{
                      maxWidth: "78%",
                      px: 2,
                      py: 1.25,
                      borderRadius: 2.5,
                      bgcolor: isUser ? "#7c3aed" : message.error ? "#fef2f2" : "#f5f3ff",
                      color: isUser ? "#fff" : message.error ? "#b91c1c" : "#1f2937",
                      whiteSpace: "pre-wrap",
                      lineHeight: 1.55,
                    }}
                  >
                    {message.content ? (
                      <Typography variant="body2">{message.content}</Typography>
                    ) : (
                      <CircularProgress size={16} sx={{ color: "#7c3aed" }} />
                    )}
                  </Box>
                </Stack>
              );
            })}
          </Stack>
        )}
      </Box>

      <Box
        component="form"
        onSubmit={handleSend}
        sx={{ borderTop: "1px solid #ececf1", p: 2, bgcolor: "#fff" }}
      >
        <Stack direction="row" spacing={1.25} alignItems="flex-end">
          <TextField
            fullWidth
            placeholder="Ask a question about this project..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            multiline
            maxRows={4}
            size="small"
            onKeyDown={(e) => {
              if (e.key === "Enter" && !e.shiftKey) {
                handleSend(e);
              }
            }}
            sx={{ "& .MuiOutlinedInput-root": { borderRadius: 2.5 } }}
          />
          <IconButton
            type="submit"
            disabled={loading || !input.trim()}
            sx={{
              width: 44,
              height: 44,
              color: "#fff",
              background: "linear-gradient(135deg, #7c3aed, #5b21b6)",
              "&:hover": { background: "linear-gradient(135deg, #6d28d9, #4c1d95)" },
              "&.Mui-disabled": { background: "#e5e7eb", color: "#9ca3af" },
            }}
          >
            <SendRoundedIcon fontSize="small" />
          </IconButton>
        </Stack>
      </Box>
    </Paper>
  );
};

export default ProjectChat;
