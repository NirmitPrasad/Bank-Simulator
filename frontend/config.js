// Configuration for the Bank Simulator Frontend
const CONFIG = {
    // Replace with your Render backend URL after deployment
    // Example: API_URL: 'https://bank-simulator-api.onrender.com'
    API_URL: 'http://localhost:8080'
};

// Export for use in other scripts
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CONFIG;
} else {
    window.APP_CONFIG = CONFIG;
}
