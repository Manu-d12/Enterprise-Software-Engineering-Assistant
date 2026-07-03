import axiosClient from "./axiosClient";

// GET /documents?projectId={id} -> [{ id, url }]
export const getDocuments = (projectId) => {
  return axiosClient.get("/documents", { params: { projectId } });
};

// POST /rag/upload/{projectId} (multipart form-data, field name "files") -> "Success!!"
export const uploadDocuments = (projectId, files) => {
  const formData = new FormData();

  Array.from(files).forEach((file) => {
    formData.append("files", file);
  });

  return axiosClient.post(`/documents/create/${projectId}`, formData, {
    headers: { "Content-Type": "multipart/form-data" },
  });
};
