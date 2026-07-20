import axios from "axios";

let authToken = "";

export const setAuthToken = (token) => {
  authToken = token || "";
};

export const getAuthToken = () => authToken;

export const BASE_URL = import.meta.env.VITE_AI_ASSISTANT_BASE_URL;

const axiosClient = axios.create({
  baseURL: BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

axiosClient.interceptors.request.use((config) => {
  if (authToken) {
    config.headers.Authorization = `Bearer ${authToken}`;
  } else {
    delete config.headers.Authorization;
  }

  return config;
});

export default axiosClient;
