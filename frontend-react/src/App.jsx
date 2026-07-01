import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import ComingSoon from "./pages/ComingSoon";
import DashboardLayout from "./components/DashboardLayout";
import "./App.css";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        <Route path="/dashboard" element={<DashboardLayout />}>
          <Route index element={<Dashboard />} />
          <Route path="projects" element={<ComingSoon title="Projects" />} />
          <Route path="documents" element={<ComingSoon title="Documents" />} />
          <Route path="chat" element={<ComingSoon title="AI Chat" />} />
          <Route path="code-generator" element={<ComingSoon title="Code Generator" />} />
          <Route path="code-review" element={<ComingSoon title="Code Review" />} />
          <Route
            path="requirement-analyzer"
            element={<ComingSoon title="Requirement Analyzer" />}
          />
          <Route path="settings" element={<ComingSoon title="Settings" />} />
        </Route>

        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
