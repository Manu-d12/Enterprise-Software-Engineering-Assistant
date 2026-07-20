import axiosClient from "./axiosClient";

export const registerUser = (payload) => {
  return axiosClient.post("/auth/register", payload);
};

export const loginUser = (payload) => {
  return axiosClient.post("/auth/login", payload);
};
