import { useState } from "react";
import { NavLink, Outlet, useNavigate } from "react-router-dom";
import {
  AppBar,
  Avatar,
  Box,
  Drawer,
  IconButton,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Menu,
  MenuItem,
  Stack,
  Toolbar,
  Typography,
  useMediaQuery,
} from "@mui/material";
import { useTheme } from "@mui/material/styles";
import MenuIcon from "@mui/icons-material/Menu";
import NotificationsNoneOutlinedIcon from "@mui/icons-material/NotificationsNoneOutlined";
import KeyboardArrowDownRoundedIcon from "@mui/icons-material/KeyboardArrowDownRounded";
import LogoutOutlinedIcon from "@mui/icons-material/LogoutOutlined";
import SpaceDashboardOutlinedIcon from "@mui/icons-material/SpaceDashboardOutlined";
import FolderOutlinedIcon from "@mui/icons-material/FolderOutlined";
import DescriptionOutlinedIcon from "@mui/icons-material/DescriptionOutlined";
import ChatBubbleOutlineOutlinedIcon from "@mui/icons-material/ChatBubbleOutlineOutlined";
import CodeOutlinedIcon from "@mui/icons-material/CodeOutlined";
import RateReviewOutlinedIcon from "@mui/icons-material/RateReviewOutlined";
import AssignmentOutlinedIcon from "@mui/icons-material/AssignmentOutlined";
import SettingsOutlinedIcon from "@mui/icons-material/SettingsOutlined";
import { useAuth } from "../context/useAuth";

const DRAWER_WIDTH = 264;

const navItems = [
  { label: "Dashboard", to: "/dashboard", icon: <SpaceDashboardOutlinedIcon /> },
  { label: "Projects", to: "/dashboard/projects", icon: <FolderOutlinedIcon /> },
  { label: "Documents", to: "/dashboard/documents", icon: <DescriptionOutlinedIcon /> },
  { label: "AI Chat", to: "/dashboard/chat", icon: <ChatBubbleOutlineOutlinedIcon /> },
  { label: "Code Generator", to: "/dashboard/code-generator", icon: <CodeOutlinedIcon /> },
  { label: "Code Review", to: "/dashboard/code-review", icon: <RateReviewOutlinedIcon /> },
  {
    label: "Requirement Analyzer",
    to: "/dashboard/requirement-analyzer",
    icon: <AssignmentOutlinedIcon />,
  },
  { label: "Settings", to: "/dashboard/settings", icon: <SettingsOutlinedIcon /> },
];

const SidebarContent = ({ onNavigate }) => {
  return (
    <Box sx={{ px: 2, py: 3 }}>
      <List disablePadding sx={{ display: "flex", flexDirection: "column", gap: 0.5 }}>
        {navItems.map((item) => (
          <ListItemButton
            key={item.to}
            component={NavLink}
            to={item.to}
            end={item.to === "/dashboard"}
            onClick={onNavigate}
            sx={{
              borderRadius: 2.5,
              px: 1.75,
              py: 1.15,
              color: "#6b7280",
              "& .MuiListItemIcon-root": { color: "inherit", minWidth: 38 },
              "&:hover": { bgcolor: "#f5f3ff", color: "#6d28d9" },
              "&.active": {
                bgcolor: "#efe7ff",
                color: "#6d28d9",
                fontWeight: 700,
              },
            }}
          >
            <ListItemIcon>{item.icon}</ListItemIcon>
            <ListItemText
              primary={item.label}
              primaryTypographyProps={{ fontSize: 13.5, fontWeight: "inherit" }}
            />
          </ListItemButton>
        ))}
      </List>
    </Box>
  );
};

const DashboardLayout = () => {
  const theme = useTheme();
  const isDesktop = useMediaQuery(theme.breakpoints.up("md"));
  const navigate = useNavigate();
  const { logout, userInfo } = useAuth();

  const [mobileOpen, setMobileOpen] = useState(false);
  const [anchorEl, setAnchorEl] = useState(null);

  const displayName = userInfo?.username || userInfo?.name || "Admin";
  const avatarInitial = displayName.charAt(0).toUpperCase();

  const handleLogout = () => {
    setAnchorEl(null);
    logout();
    navigate("/login", { replace: true });
  };

  return (
    <Box sx={{ display: "flex", minHeight: "100svh", bgcolor: "#f8f7fb" }}>
      {/* Top navbar */}
      <AppBar
        position="fixed"
        elevation={0}
        sx={{
          bgcolor: "#fff",
          color: "#111827",
          borderBottom: "1px solid #ececf1",
          zIndex: (t) => t.zIndex.drawer + 1,
        }}
      >
        <Toolbar sx={{ gap: 1.5 }}>
          <IconButton
            edge="start"
            onClick={() => setMobileOpen((open) => !open)}
            sx={{ color: "#374151" }}
          >
            <MenuIcon />
          </IconButton>

          <Typography variant="h6" fontWeight={800} sx={{ flexGrow: 1, fontSize: 16 }}>
            AI Software Engineering Assistant
          </Typography>

          <IconButton sx={{ color: "#4b5563" }}>
            <NotificationsNoneOutlinedIcon />
          </IconButton>

          <Stack
            direction="row"
            alignItems="center"
            spacing={1}
            onClick={(event) => setAnchorEl(event.currentTarget)}
            sx={{ cursor: "pointer", pl: 0.5 }}
          >
            <Avatar
              sx={{
                width: 36,
                height: 36,
                fontSize: 16,
                fontWeight: 700,
                background: "linear-gradient(135deg, #7c3aed, #5b21b6)",
              }}
            >
              {avatarInitial}
            </Avatar>
            <Typography
              fontWeight={700}
              fontSize={13.5}
              sx={{ display: { xs: "none", sm: "block" } }}
            >
              {displayName}
            </Typography>
            <KeyboardArrowDownRoundedIcon sx={{ color: "#6b7280" }} />
          </Stack>

          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={() => setAnchorEl(null)}
            anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
            transformOrigin={{ vertical: "top", horizontal: "right" }}
          >
            <MenuItem onClick={handleLogout} sx={{ gap: 1.2, fontSize: 14 }}>
              <LogoutOutlinedIcon fontSize="small" />
              Logout
            </MenuItem>
          </Menu>
        </Toolbar>
      </AppBar>

      {/* Sidebar */}
      <Box
        component="nav"
        sx={{ width: { md: DRAWER_WIDTH }, flexShrink: { md: 0 } }}
      >
        {isDesktop ? (
          <Drawer
            variant="permanent"
            open
            sx={{
              "& .MuiDrawer-paper": {
                width: DRAWER_WIDTH,
                boxSizing: "border-box",
                bgcolor: "#fff",
                borderRight: "1px solid #ececf1",
              },
            }}
          >
            <Toolbar />
            <SidebarContent />
          </Drawer>
        ) : (
          <Drawer
            variant="temporary"
            open={mobileOpen}
            onClose={() => setMobileOpen(false)}
            ModalProps={{ keepMounted: true }}
            sx={{
              "& .MuiDrawer-paper": {
                width: DRAWER_WIDTH,
                boxSizing: "border-box",
                bgcolor: "#fff",
              },
            }}
          >
            <Toolbar />
            <SidebarContent onNavigate={() => setMobileOpen(false)} />
          </Drawer>
        )}
      </Box>

      {/* Outlet / content area */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          width: { md: `calc(100% - ${DRAWER_WIDTH}px)` },
          p: { xs: 2.5, md: 4 },
        }}
      >
        <Toolbar />
        <Outlet />
      </Box>
    </Box>
  );
};

export default DashboardLayout;
