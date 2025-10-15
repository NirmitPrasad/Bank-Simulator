// Login form functionality
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const accountNoInput = document.getElementById('accountNo');
    const passwordInput = document.getElementById('password');

    // Form submission handler
    loginForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const accountNo = accountNoInput.value.trim();
        const password = passwordInput.value.trim();
        
        // Basic validation
        if (!accountNo || !password) {
            // Please fill in all fields
            return;
        }
        
        // Account number validation (should be numeric)
        if (!/^\d+$/.test(accountNo)) {
            // Account number should contain only numbers
            return;
        }
        
        // Password validation (minimum 6 characters)
        if (password.length < 6) {
            // Password should be at least 6 characters long
            return;
        }
        
        // Simulate login process
        performLogin(accountNo, password);
    });
    
    // Input field animations
    const inputs = document.querySelectorAll('input');
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });
        
        input.addEventListener('blur', function() {
            if (!this.value) {
                this.parentElement.classList.remove('focused');
            }
        });
    });
    
});

// Login function
function performLogin(accountNo, password) {
    // Show loading state
    const submitBtn = document.querySelector('.btn-primary');
    const originalText = submitBtn.textContent;
    submitBtn.textContent = 'LOGGING IN...';
    submitBtn.disabled = true;
    
    // Simulate API call
    setTimeout(() => {
        // For demo purposes, accept any account number and password
        // In real implementation, this would make an API call to your backend
        console.log('Login attempt:', { accountNo, password });
        
        // Simulate successful login
        // Login successful! Redirecting to dashboard...
        
        // Reset button state
        submitBtn.textContent = originalText;
        submitBtn.disabled = false;
        
        // Redirect to dashboard
        setTimeout(() => {
            window.location.href = 'dashboard.html';
        }, 1000);
        
    }, 1500);
}

// Signup function
function showSignup() {
    console.log('Redirecting to add account page...');
    window.location.href = 'create-account.html';
}


// Forgot password function
function forgotPassword() {
    const accountNo = document.getElementById('accountNo').value.trim();
    
    if (!accountNo) {
        showModal('Validation Error', 'Please enter your account number to reset your password.', 'error');
        document.getElementById('accountNo').focus();
        return;
    }
    
    // Validate account number format
    if (!/^\d+$/.test(accountNo)) {
        showModal('Invalid Input', 'Please enter a valid account number.', 'error');
        document.getElementById('accountNo').focus();
        return;
    }
    
    // Show confirmation dialog
    showConfirmModal(
        'Confirm Password Reset',
        `Are you sure you want to reset the password for account number ${accountNo}?`,
        () => {
            // Simulate password reset process
            showModal('Password Reset Sent', 'Password reset instructions have been sent to your registered email address.', 'success');
            
            // In a real application, this would:
            // 1. Make an API call to your backend
            // 2. Send reset instructions via email/SMS
            // 3. Redirect to a password reset page
            
            console.log('Password reset requested for account:', accountNo);
        }
    );
}

// Add some interactive animations
document.addEventListener('DOMContentLoaded', function() {
    // Animate characters on page load
    const characters = document.querySelectorAll('.character');
    characters.forEach((character, index) => {
        setTimeout(() => {
            character.style.opacity = '0';
            character.style.transform = 'translateY(20px)';
            character.style.transition = 'all 0.6s ease';
            
            setTimeout(() => {
                character.style.opacity = '1';
                character.style.transform = 'translateY(0)';
            }, 100);
        }, index * 200);
    });
    
    // Animate phone
    const phone = document.querySelector('.phone');
    setTimeout(() => {
        phone.style.opacity = '0';
        phone.style.transform = 'scale(0.8)';
        phone.style.transition = 'all 0.6s ease';
        
        setTimeout(() => {
            phone.style.opacity = '1';
            phone.style.transform = 'scale(1)';
        }, 300);
    }, 100);
    
    // Animate clouds
    const clouds = document.querySelectorAll('.cloud');
    clouds.forEach((cloud, index) => {
        setTimeout(() => {
            cloud.style.opacity = '0';
            cloud.style.transform = 'translateY(10px)';
            cloud.style.transition = 'all 0.8s ease';
            
            setTimeout(() => {
                cloud.style.opacity = '0.6';
                cloud.style.transform = 'translateY(0)';
            }, 200);
        }, index * 150);
    });
});

// Add keyboard navigation
document.addEventListener('keydown', function(e) {
    if (e.key === 'Enter') {
        const activeElement = document.activeElement;
        if (activeElement.tagName === 'INPUT') {
            const form = activeElement.closest('form');
            if (form) {
                form.dispatchEvent(new Event('submit'));
            }
        }
    }
});

// Add form validation styling
function addValidationStyles() {
    const style = document.createElement('style');
    style.textContent = `
        .form-group.focused label {
            color: #4A90E2;
            transform: translateY(-2px);
            transition: all 0.3s ease;
        }
        
        .form-group input:invalid {
            border-color: #ff4444;
        }
        
        .form-group input:valid {
            border-color: #4CAF50;
        }
        
        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }
        
        .character {
            transition: transform 0.3s ease;
        }
        
        .character:hover {
            transform: translateY(-5px) scale(1.05);
        }
        
        .phone {
            transition: transform 0.3s ease;
        }
        
        .phone:hover {
            transform: scale(1.05);
        }
    `;
    document.head.appendChild(style);
}

// Initialize validation styles
addValidationStyles();

// Custom Modal Functions
function showModal(title, message, type = 'info') {
    const modal = document.getElementById('customModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const modalIcon = document.querySelector('.modal-icon');
    const modalOkBtn = document.getElementById('modalOkBtn');
    const modalCancelBtn = document.getElementById('modalCancelBtn');
    
    // Set content
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    
    // Set icon based on type
    switch(type) {
        case 'error':
            modalIcon.textContent = '⚠️';
            modalIcon.style.background = 'rgba(220, 53, 69, 0.2)';
            break;
        case 'success':
            modalIcon.textContent = '✅';
            modalIcon.style.background = 'rgba(40, 167, 69, 0.2)';
            break;
        case 'warning':
            modalIcon.textContent = '⚠️';
            modalIcon.style.background = 'rgba(255, 193, 7, 0.2)';
            break;
        default:
            modalIcon.textContent = '🏛️';
            modalIcon.style.background = 'rgba(255, 255, 255, 0.2)';
    }
    
    // Hide cancel button for simple modals
    modalCancelBtn.style.display = 'none';
    
    // Show modal
    modal.classList.add('show');
    
    // Handle OK button click
    modalOkBtn.onclick = function() {
        hideModal();
    };
    
    // Handle escape key
    document.addEventListener('keydown', function escapeHandler(e) {
        if (e.key === 'Escape') {
            hideModal();
            document.removeEventListener('keydown', escapeHandler);
        }
    });
    
    // Handle overlay click
    modal.onclick = function(e) {
        if (e.target === modal) {
            hideModal();
        }
    };
}

function showConfirmModal(title, message, onConfirm) {
    const modal = document.getElementById('customModal');
    const modalTitle = document.getElementById('modalTitle');
    const modalMessage = document.getElementById('modalMessage');
    const modalIcon = document.querySelector('.modal-icon');
    const modalOkBtn = document.getElementById('modalOkBtn');
    const modalCancelBtn = document.getElementById('modalCancelBtn');
    
    // Set content
    modalTitle.textContent = title;
    modalMessage.textContent = message;
    modalIcon.textContent = '❓';
    modalIcon.style.background = 'rgba(255, 193, 7, 0.2)';
    
    // Show both buttons
    modalCancelBtn.style.display = 'inline-block';
    modalOkBtn.textContent = 'Confirm';
    modalCancelBtn.textContent = 'Cancel';
    
    // Show modal
    modal.classList.add('show');
    
    // Handle button clicks
    modalOkBtn.onclick = function() {
        hideModal();
        if (onConfirm) onConfirm();
    };
    
    modalCancelBtn.onclick = function() {
        hideModal();
    };
    
    // Handle escape key
    document.addEventListener('keydown', function escapeHandler(e) {
        if (e.key === 'Escape') {
            hideModal();
            document.removeEventListener('keydown', escapeHandler);
        }
    });
    
    // Handle overlay click
    modal.onclick = function(e) {
        if (e.target === modal) {
            hideModal();
        }
    };
}

function hideModal() {
    const modal = document.getElementById('customModal');
    modal.classList.remove('show');
    
    // Reset button text
    const modalOkBtn = document.getElementById('modalOkBtn');
    modalOkBtn.textContent = 'OK';
}
