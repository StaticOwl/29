import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080'; // Change this to your server's URL

const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    withCredentials: true, // if you need to send cookies with your request
    headers: {
        'Content-Type': 'application/json',
    },
});

export default axiosInstance;