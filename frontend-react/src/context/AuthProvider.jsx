import { useCallback, useEffect, useMemo, useState } from "react";
import { setAuthToken } from "../api/axiosClient";
import AuthContext from "./AuthContext";

const STORAGE_KEY = "ai_assistant_auth";

const getAccessToken = (data) => data?.token;

const readStoredUser = () => {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
};

export const AuthProvider = ({ children }) => {
  const [userInfo, setUserInfo] = useState(() => readStoredUser());
  const [accessToken, setAccessToken] = useState(
    () => getAccessToken(readStoredUser()) || ""
  );

  // Keep the axios auth header in sync with the token, including on refresh
  // where the token is rehydrated from localStorage before the first request.
  useEffect(() => {
    setAuthToken(accessToken);
  }, [accessToken]);

  const storeUserInfo = useCallback((data) => {
    const accessTokenValue = getAccessToken(data);

    setAuthToken(accessTokenValue);
    setAccessToken(accessTokenValue || "");
    setUserInfo(data || null);

    if (data) {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
    } else {
      localStorage.removeItem(STORAGE_KEY);
    }
  }, []);

  const logout = useCallback(() => {
    setAuthToken("");
    setAccessToken("");
    setUserInfo(null);
    localStorage.removeItem(STORAGE_KEY);
  }, []);

  const value = useMemo(
    () => ({
      accessToken,
      isAuthenticated: Boolean(accessToken),
      logout,
      setUserInfo,
      storeUserInfo,
      userInfo,
    }),
    [accessToken, logout, storeUserInfo, userInfo]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
