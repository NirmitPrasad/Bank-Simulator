# Bank Simulator Frontend

A modern, responsive frontend for the Bank Simulator project built with HTML, CSS, and JavaScript.

## Features

- **Clean Login Interface**: Modern design with light blue background
- **Responsive Design**: Works on desktop, tablet, and mobile devices
- **Form Validation**: Client-side validation
- **Interactive Elements**: Animated characters and phone illustration
- **Accessibility**: Keyboard navigation and proper form labels

## File Structure

```
frontend/
├── index.html          # Main login page
├── styles.css          # CSS styles and responsive design
├── script.js           # JavaScript functionality and animations
└── README.md           # This file
```

## Getting Started

1. **Open the login page**: Simply open `index.html` in your web browser
2. **Local server** (recommended): Use a local server for better development experience:
   ```bash
   # Using Python
   python -m http.server 8000
   
   # Using Node.js (if you have http-server installed)
   npx http-server
   
   # Using PHP
   php -S localhost:8000
   ```

3. **Access the page**: Navigate to `http://localhost:8000` in your browser

## Features Overview

### Login Form
- Account number input (numeric validation)
- Password input (minimum 6 characters)
- Login, Signup, and Reset buttons
- Form validation with user feedback

### Design Elements
- **Left Section**: Login form with bank branding
- **Right Section**: Interactive illustrations including:
  - Smartphone mockup with login screen
  - Animated characters (male and female)
  - Decorative cloud elements
  - Handshake icon at bottom

### Responsive Design
- **Desktop**: Two-column layout with full illustrations
- **Tablet**: Optimized spacing and sizing
- **Mobile**: Single-column layout with simplified illustrations

## Customization

### Colors
The design uses a light blue theme (`#E6F3FF`). To change colors, modify the CSS variables in `styles.css`:

```css
:root {
  --primary-bg: #E6F3FF;
  --button-bg: #000;
  --accent-color: #4A90E2;
}
```

### Animations
Animations are controlled in `script.js`. You can:
- Disable animations by commenting out the animation code
- Modify timing by changing the `setTimeout` values
- Add new animations by extending the existing functions

## Integration with Backend

The frontend is designed to work with your Spring Boot backend:

1. **API Endpoints**: Update the `performLogin()` function in `script.js` to call your actual backend
2. **Authentication**: Implement proper JWT or session-based authentication
3. **Error Handling**: Add proper error handling for API responses

### Example Backend Integration

```javascript
async function performLogin(accountNo, password) {
    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ accountNo, password })
        });
        
        if (response.ok) {
            const data = await response.json();
            // Handle successful login
            window.location.href = 'dashboard.html';
        } else {
            // Handle login error
            alert('Invalid credentials');
        }
    } catch (error) {
        console.error('Login error:', error);
        alert('Login failed. Please try again.');
    }
}
```

## Browser Support

- Chrome 60+
- Firefox 55+
- Safari 12+
- Edge 79+

## Development

For development, you can:
1. Use browser developer tools to test responsive design
2. Modify CSS for styling changes
3. Update JavaScript for functionality changes
4. Test form validation and user interactions

## Next Steps

1. **Dashboard Page**: Create a dashboard after successful login
2. **Signup Page**: Implement user registration
3. **Account Management**: Add account details and transaction history
4. **Security**: Implement proper authentication and authorization
5. **Testing**: Add unit tests for JavaScript functionality
