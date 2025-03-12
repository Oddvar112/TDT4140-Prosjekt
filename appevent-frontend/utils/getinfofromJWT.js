/**
 * Retrieves the JWT from localStorage.
 * Change the key 'token' if your token is stored under a different name.
 */
function getToken() {
  // Check if we're in a browser environment
  if (typeof window !== 'undefined') {
    return localStorage.getItem("token");
  }
  return null;
}

/**
 * Decodes the Base64Url encoded payload of the JWT.
 * @param {string} token - The JWT string.
 * @returns {object} - The parsed payload object.
 * @throws Will throw an error if the token format is invalid.
 */
function decodePayload(token) {
  const parts = token.split(".");
  if (parts.length !== 3) {
    throw new Error("Invalid token format");
  }
  const payload = parts[1];
  // Replace URL-safe characters with standard Base64 characters
  const base64 = payload.replace(/-/g, "+").replace(/_/g, "/");
  // atob decodes a base64 encoded string
  const jsonPayload = decodeURIComponent(
    atob(base64)
      .split("")
      .map((char) => "%" + ("00" + char.charCodeAt(0).toString(16)).slice(-2))
      .join("")
  );
  return JSON.parse(jsonPayload);
}

/**
 * Gets the decoded payload from the JWT stored in localStorage.
 * @returns {object|null} - The payload or null if token is not found/decoded.
 */
export function getPayload() {
  // Ensure this only runs in the browser
  if (typeof window === 'undefined') {
    return null;
  }

  const token = getToken();
  if (!token) {
    console.warn("No token found in localStorage");
    return null;
  }
  try {
    return decodePayload(token);
  } catch (error) {
    console.error("Error decoding token:", error);
    return null;
  }
}

/**
 * Returns the username from the token.
 * Assumes that the username is stored in the "sub" claim.
 * @returns {string|null} - The username or null if not available.
 */
export function getUsername() {
  const payload = getPayload();
  return payload && payload.sub ? payload.sub : null;
}

/**
 * Returns the user ID from the token.
 * Assumes that the user ID is stored in the "userId" claim.
 * @returns {string|null} - The user ID or null if not available.
 */
export function getUserId() {
  const payload = getPayload();
  return payload && payload.userId ? payload.userId : null;
}

/**
 * Returns whether the user is an admin.
 * Assumes that the admin status is stored in the "isAdmin" claim.
 * @returns {boolean} - true if admin, false otherwise.
 */
export function isAdmin() {
  const payload = getPayload();
  return payload && typeof payload.isAdmin !== "undefined"
    ? payload.isAdmin
    : false;
}