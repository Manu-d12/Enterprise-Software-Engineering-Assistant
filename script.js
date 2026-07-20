const res = await fetch('http://localhost:8080/ai-assistant/sse-time/stream-sse-mvc');
const reader = res.body.getReader();
const decoder = new TextDecoder();

while (true) {
  const { value, done } = await reader.read();
  if (done) break;
  const chunk = decoder.decode(value, { stream: true });
  process.stdout.write(chunk);   // raw SSE lines: "event: ...", "data: ..."
}