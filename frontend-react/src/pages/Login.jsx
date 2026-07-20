import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  IconButton,
  InputAdornment,
  TextField,
  Typography,
} from "@mui/material";
import EmailOutlinedIcon from "@mui/icons-material/EmailOutlined";
import LockOutlinedIcon from "@mui/icons-material/LockOutlined";
import LoginOutlinedIcon from "@mui/icons-material/LoginOutlined";
import VisibilityOffOutlinedIcon from "@mui/icons-material/VisibilityOffOutlined";
import VisibilityOutlinedIcon from "@mui/icons-material/VisibilityOutlined";
import AuthLayout from "../components/AuthLayout";
import { loginUser } from "../api/auth";
import { useAuth } from "../context/useAuth";


const Login = () => {
  const { storeUserInfo } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData({
      ...formData,
      [name]: value,
    });
  };


  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    try {
      setLoading(true);

      const response = await loginUser({
        username: formData.email,
        password: formData.password,
      });
      storeUserInfo(response.data);
      setSuccess("Signed in successfully.");
      navigate("/dashboard", { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout>
      <Card
        elevation={0}
        sx={{
          width: "100%",
          maxWidth: 430,
          borderRadius: 4,
          border: "1px solid #e5e7eb",
          boxShadow: "0 24px 60px rgba(17, 24, 39, 0.08)",
        }}
      >
        <CardContent sx={{ p: { xs: 3, sm: 5 } }}>
          <Typography variant="h5" fontWeight={800} textAlign="center">
            Welcome Back
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
            textAlign="center"
            mt={1}
            mb={4}
          >
            Sign in to continue building with your AI engineering assistant.
          </Typography>

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

          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Email"
              name="email"
              type="email"
              placeholder="Enter your email"
              value={formData.email}
              onChange={handleChange}
              margin="normal"
              autoComplete="email"
              required
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <EmailOutlinedIcon fontSize="small" />
                    </InputAdornment>
                  ),
                },
              }}
            />

            <TextField
              fullWidth
              label="Password"
              name="password"
              type={showPassword ? "text" : "password"}
              placeholder="Enter your password"
              value={formData.password}
              onChange={handleChange}
              margin="normal"
              autoComplete="current-password"
              required
              slotProps={{
                input: {
                  startAdornment: (
                    <InputAdornment position="start">
                      <LockOutlinedIcon fontSize="small" />
                    </InputAdornment>
                  ),
                  endAdornment: (
                    <InputAdornment position="end">
                      <IconButton
                        aria-label={showPassword ? "Hide password" : "Show password"}
                        edge="end"
                        onClick={() => setShowPassword((value) => !value)}
                      >
                        {showPassword ? (
                          <VisibilityOffOutlinedIcon fontSize="small" />
                        ) : (
                          <VisibilityOutlinedIcon fontSize="small" />
                        )}
                      </IconButton>
                    </InputAdornment>
                  ),
                },
              }}
            />

            <Button
              fullWidth
              type="submit"
              variant="contained"
              startIcon={<LoginOutlinedIcon />}
              disabled={loading}
              sx={{
                mt: 3,
                py: 1.3,
                borderRadius: 2,
                textTransform: "none",
                fontWeight: 700,
                background: "linear-gradient(135deg, #7c3aed, #5b21b6)",
              }}
            >
              {loading ? "Signing in..." : "Sign in"}
            </Button>
          </Box>

          <Typography variant="body2" textAlign="center" mt={3}>
            New here?{" "}
            <Box
              component={Link}
              to="/register"
              sx={{
                color: "#6d28d9",
                fontWeight: 700,
                textDecoration: "none",
              }}
            >
              Create an account
            </Box>
          </Typography>
        </CardContent>
      </Card>
    </AuthLayout>
  );
};

export default Login;
