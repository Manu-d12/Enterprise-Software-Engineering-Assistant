// Format an ISO date string into e.g. "Jul 1, 2026".
export const formatDate = (isoString) => {
  if (!isoString) return "—";

  const date = new Date(isoString);

  if (Number.isNaN(date.getTime())) return "—";

  return date.toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
};

// Derive a readable file name from a document path/url.
export const fileNameFromPath = (path) => {
  if (!path) return "Untitled";

  const segments = path.split(/[\\/]/);
  return segments[segments.length - 1] || path;
};

// Pick a stable avatar color for a project based on its id.
const AVATAR_COLORS = [
  { bg: "#e6f7ee", color: "#16a34a" },
  { bg: "#e6f0ff", color: "#2563eb" },
  { bg: "#efe7ff", color: "#7c3aed" },
  { bg: "#ffeede", color: "#ea580c" },
  { bg: "#fde8ef", color: "#db2777" },
];

export const avatarColor = (id = 0) => {
  return AVATAR_COLORS[Math.abs(id) % AVATAR_COLORS.length];
};
