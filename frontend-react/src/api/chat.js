import axiosClient, { BASE_URL, getAuthToken } from "./axiosClient";

// GET /rag/ask/{projectId}?q={question} -> { projectId, content }
export const askProject = (projectId, question) => {
  return axiosClient.get(`/rag/ask/${projectId}`, {
    params: { q: question },
  });
};

// GET /rag/askStream/{projectId}?q={question} -> Flux<String> (streamed tokens)
// Uses fetch + ReadableStream so tokens render progressively as they arrive.
export const askProjectStream = async (
  projectId,
  question,
  { onChunk, signal } = {}
) => {
  const token = getAuthToken();
  const url = `${BASE_URL}/rag/askStream/${projectId}?q=${encodeURIComponent(
    question
  )}`;

  const response = await fetch(url, {
    method: "GET",
    headers: {
      Accept: "text/event-stream, text/plain",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    signal,
  });

  if (!response.ok || !response.body) {
    const message = await response.text().catch(() => "");
    throw new Error(message || `Request failed with status ${response.status}`);
  }

  const reader = response.body.getReader();
  const decoder = new TextDecoder();
  let full = "";

  // Read raw chunks and emit them. Handles both plain streamed text and
  // SSE frames ("data:" prefixed lines separated by blank lines).
  for (;;) {
    const { value, done } = await reader.read();
    if (done) break;

    const raw = decoder.decode(value, { stream: true });
    const piece = raw.includes("data:") ? parseSseChunk(raw) : raw;

    if (piece) {
      full += piece;
      onChunk?.(piece, full);
    }
  }

  return full;
};

const parseSseChunk = (raw) =>
  raw
    .split(/\r?\n/)
    .filter((line) => line.startsWith("data:"))
    .map((line) => line.slice(5).replace(/^ /, ""))
    .join("");
