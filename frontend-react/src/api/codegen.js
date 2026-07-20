import { BASE_URL, getAuthToken } from "./axiosClient";

/**
 * Streams the code-generation SSE endpoint.
 *
 * The endpoint is a GET behind JWT auth, and native EventSource can't send an
 * Authorization header — so we read the stream manually with fetch + a
 * ReadableStream reader and parse the SSE frames ourselves.
 *
 * SSE frames are separated by a blank line. Within a frame we care about the
 * `event:` name (progress | complete | error) and one or more `data:` lines
 * (joined with newlines), matching how CodeGenService emits them.
 *
 * @param {string} query    the requirement to build
 * @param {object} handlers { onEvent, onError, onClose }
 * @returns {() => void}    call to abort the stream
 */
export const streamGenerateCode = (query, { onEvent, onError, onClose } = {}) => {
  const controller = new AbortController();
  const token = getAuthToken();
  const url = `${BASE_URL}/generate-code?q=${encodeURIComponent(query)}`;

  (async () => {
    try {
      const response = await fetch(url, {
        method: "GET",
        headers: {
          Accept: "text/event-stream",
          ...(token ? { Authorization: `Bearer ${token}` } : {}),
        },
        signal: controller.signal,
      });

      if (!response.ok || !response.body) {
        throw new Error(`Request failed with status ${response.status}`);
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = "";

      // Continuously read chunks and split them into complete SSE frames.
      // A frame ends at a blank line (\n\n); \r\n is normalized to \n first.
      for (;;) {
        const { value, done } = await reader.read();
        if (done) break;

        buffer += decoder.decode(value, { stream: true }).replace(/\r\n/g, "\n");

        let sepIndex;
        while ((sepIndex = buffer.indexOf("\n\n")) !== -1) {
          const rawFrame = buffer.slice(0, sepIndex);
          buffer = buffer.slice(sepIndex + 2);
          const parsed = parseFrame(rawFrame);
          if (parsed) onEvent?.(parsed);
        }
      }

      onClose?.();
    } catch (err) {
      if (err?.name === "AbortError") return;
      onError?.(err);
    }
  })();

  return () => controller.abort();
};

const parseFrame = (rawFrame) => {
  let event = "message";
  const dataLines = [];

  for (const line of rawFrame.split("\n")) {
    if (!line || line.startsWith(":")) continue; // comment / heartbeat
    const colon = line.indexOf(":");
    const field = colon === -1 ? line : line.slice(0, colon);
    // Spec: a single leading space after the colon is stripped.
    let value = colon === -1 ? "" : line.slice(colon + 1);
    if (value.startsWith(" ")) value = value.slice(1);

    if (field === "event") event = value;
    else if (field === "data") dataLines.push(value);
  }

  if (dataLines.length === 0) return null;
  return { event, data: dataLines.join("\n") };
};
