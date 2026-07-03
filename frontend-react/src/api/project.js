import axiosClient from "./axiosClient";

// GET /projects -> [{ id, name, description, createdDate, lastModifiedDate }]
export const getProjects = () => {
  return axiosClient.get("/projects");
};

// GET /projects/{id} -> { id, name, description, createdDate, lastModifiedDate }
export const getProjectById = (projectId) => {
  return axiosClient.get(`/projects/${projectId}`);
};

// POST /projects/save -> created project
export const createProject = (payload) => {
  return axiosClient.post("/projects/save", payload);
};
