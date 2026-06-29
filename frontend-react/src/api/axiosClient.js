import axios from "axios";


const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_AI_ASSISTANT_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

axiosClient.interceptors.request.use((config) => {
  const token =
    localStorage.getItem("accessToken") || sessionStorage.getItem("accessToken");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default axiosClient;
