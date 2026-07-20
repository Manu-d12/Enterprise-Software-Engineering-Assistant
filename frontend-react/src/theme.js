import { createTheme } from "@mui/material/styles";

// Central design tokens so every page shares the same look & feel.
const theme = createTheme({
  palette: {
    mode: "light",
    primary: { main: "#7c3aed", dark: "#5b21b6", light: "#a78bfa" },
    secondary: { main: "#6d28d9" },
    background: { default: "#f8f7fb", paper: "#ffffff" },
    text: { primary: "#111827", secondary: "#6b7280" },
    divider: "#ececf1",
  },
  shape: { borderRadius: 12 },
  typography: {
    fontFamily:
      'Inter, system-ui, -apple-system, "Segoe UI", Roboto, Arial, sans-serif',
    h4: { fontWeight: 800 },
    h5: { fontWeight: 800 },
    h6: { fontWeight: 800 },
    button: { textTransform: "none", fontWeight: 700 },
  },
  components: {
    MuiButton: {
      defaultProps: { disableElevation: true },
      styleOverrides: {
        root: { borderRadius: 8 },
      },
    },
    MuiPaper: {
      styleOverrides: {
        rounded: { borderRadius: 12 },
      },
    },
  },
});

export default theme;
