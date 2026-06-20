import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import {
  Alert,
  Box,
  Button,
  Card,
  CardContent,
  TextField,
  Typography,
} from "@mui/material";
import AuthLayout from "../components/AuthLayout";
import { registerUser } from "../api/auth";

const Register = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (event) => {
    setFormData({
      ...formData,
      [event.target.name]: event.target.value,
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setError("");

    if (formData.password !== formData.confirmPassword) {
      setError("Password and confirm password do not match");
      return;
    }

    try {
      setLoading(true);

      await registerUser({
        name: formData.name,
        email: formData.email,
        password: formData.password,
      });

      navigate("/login");
    } catch (err) {
      setError(
        err.response?.data?.message || "Registration failed. Please try again."
      );
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
            Create Account
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
            textAlign="center"
            mt={1}
            mb={4}
          >
            Start turning requirements into designs, code, and delivery plans.
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit}>
            <TextField
              fullWidth
              label="Full Name"
              name="name"
              placeholder="Enter your full name"
              value={formData.name}
              onChange={handleChange}
              margin="normal"
              required
            />

            <TextField
              fullWidth
              label="Email"
              name="email"
              type="email"
              placeholder="Enter your email"
              value={formData.email}
              onChange={handleChange}
              margin="normal"
              required
            />

            <TextField
              fullWidth
              label="Password"
              name="password"
              type="password"
              placeholder="Create a password"
              value={formData.password}
              onChange={handleChange}
              margin="normal"
              required
            />

            <TextField
              fullWidth
              label="Confirm Password"
              name="confirmPassword"
              type="password"
              placeholder="Confirm your password"
              value={formData.confirmPassword}
              onChange={handleChange}
              margin="normal"
              required
            />

            <Button
              fullWidth
              type="submit"
              variant="contained"
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
              {loading ? "Registering..." : "Register"}
            </Button>
          </Box>

          <Typography variant="body2" textAlign="center" mt={3}>
            Already have an account?{" "}
            <Box
              component={Link}
              to="/login"
              sx={{
                color: "#6d28d9",
                fontWeight: 700,
                textDecoration: "none",
              }}
            >
              Sign in
            </Box>
          </Typography>
        </CardContent>
      </Card>
    </AuthLayout>
  );
};

export default Register;
