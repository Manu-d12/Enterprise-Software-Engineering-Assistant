import { useCallback, useMemo, useState } from "react";
import { setAuthToken } from "../api/axiosClient";
import AuthContext from "./AuthContext";

const getAccessToken = (data) => data?.token;

export const AuthProvider = ({ children }) => {
  const [userInfo, setUserInfo] = useState(null);
  const [accessToken, setAccessToken] = useState("");

  const storeUserInfo = useCallback((data) => {
    const accessTokenValue = getAccessToken(data);

    setAuthToken(accessTokenValue);
    setAccessToken(accessTokenValue);
    setUserInfo(data || null);
  }, []);

  const logout = useCallback(() => {
    setAuthToken("");
    setAccessToken("");
    setUserInfo(null);
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
