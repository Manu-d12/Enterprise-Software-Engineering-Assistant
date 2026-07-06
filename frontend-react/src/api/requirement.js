import axiosClient from "./axiosClient";

export const analyzeRequirements = (query) => {
  return axiosClient.get("/requirement-analysis", {
    params: { q: query },
  });
};
