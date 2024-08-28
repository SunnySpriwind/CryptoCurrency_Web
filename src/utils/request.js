// This is request document.

import axios from "axios";


// API
const request = axios.create({
    baseURL: 'http://localhost:8080',
    timeout: 5000
})

// Adding token in header
request.interceptors.request.use(config => {
    config.headers['Content-Type'] = 'application/json; charset=UTF-8';

    const token = localStorage.getItem('token');
    if (token) {
        const cleanToken = token.replace(/"/g, '');
        config.headers['token'] = cleanToken;
    }

    return config;
}, error => {
    return Promise.reject(error);
});


// To get JWT
request.interceptors.response.use(
    response => {
        let res = response.data;
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res;
        }
        return res;
    },
    error => {
        console.log("error:" + error);
        return Promise.reject(error);
    }
)

export default request;