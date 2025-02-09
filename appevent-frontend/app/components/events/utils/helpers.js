export const formatDateTime = (dateTime) => {
  const date = new Date(dateTime);
  return date.toLocaleString();
};

export const truncateText = (text = "", maxLength) => {
  const isTruncated = text.length > maxLength;
  const truncatedText = isTruncated ? text.slice(0, maxLength) : text;
  return { truncatedText, isTruncated };
};
